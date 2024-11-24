package com.doodle.services;

import com.doodle.enums.ChatBoxType;
import com.doodle.enums.MessageType;
import com.doodle.enums.RoomStatus;
import com.doodle.enums.WordMatchingStatus;
import com.doodle.managers.RoomManager;
import com.doodle.managers.UserManager;
import com.doodle.models.Hint;
import com.doodle.models.Message;
import com.doodle.models.RoomInfo;
import com.doodle.models.UserInfo;
import com.doodle.models.chatbox.Chat;
import com.doodle.models.chatbox.Notification;
import com.doodle.models.messages.ChatBoxMessage;
import com.doodle.models.messages.ChosenWordMessage;
import com.doodle.models.messages.BoardMessage;
import com.doodle.models.messages.JoinRoomMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Service
@AllArgsConstructor
public class DoodleService {
    private final UserManager userManager;
    private final RoomManager roomManager;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final RandomWordService randomWordService;
    private final WordMatchingService wordMatchingService;


    public void handleUserMessage(Message message, String sessionId) {
        if (MessageType.JOIN_ROOM.equals(message.getType())) {
            handleUserJoinRoomMessage(message, sessionId);
        }
    }

    public void handleRoomMessage(Message message, String sessionId) {
        if (MessageType.CHOSEN_WORD.equals(message.getType())) {
            var userInfo = userManager.getUser(sessionId);
            var roomId = userInfo.getRoomId();
            handleUserChosenWordMessage(roomId, message);
            return;
        }

        if (MessageType.BOARD.equals(message.getType())) {
            handleBoardMessage(sessionId, message);
        }

        if (MessageType.CHAT_BOX.equals(message.getType())) {
            handleChatBoxMessage(sessionId, message);
        }
    }

    private void handleUserJoinRoomMessage(Message message, String sessionId) {
        var joinRoomMessage = (JoinRoomMessage) message.getData();
        var userInfo = userManager.addUser(sessionId, joinRoomMessage);
        var roomInfo = roomManager.joinRoom(userInfo);

        var userJoinedRoomNotification = Notification.userJoined(roomInfo.getChatBox().size() + 1, userInfo.getName());
        roomInfo.getChatBox().add(userJoinedRoomNotification);

        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createUserInfosMessage(roomInfo.getUserInfos()));
        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createChatBoxMessage(userJoinedRoomNotification));

        startGameIfConditionsAllow(roomInfo);

        simpMessagingTemplate.convertAndSend(String.format("/app/user/%s/messages", userInfo.getId()), Message.createJoinedRoomMessage(roomInfo));
    }

    private void startGameIfConditionsAllow(RoomInfo roomInfo) {
        if (!RoomStatus.WAITING.equals(roomInfo.getStatus()) && !RoomStatus.GAME_COMPLETED.equals(roomInfo.getStatus())) {
            //Game is in progress
            return;
        }

        if (roomInfo.getUserInfos().size() < 2) {
            //No.of users criteria not met
            return;
        }

        startRound(roomInfo);
    }

    private void startRound(RoomInfo roomInfo) {
        roomInfo.getUserInfos().forEach(userInfo -> userInfo.setTurnCompleted(false));
        sendWordsChoicesToUser(roomInfo, roomInfo.getUserInfos().getFirst());
    }

    private void sendWordsChoicesToUser(RoomInfo roomInfo, UserInfo userInfo) {
        roomInfo.setStatus(RoomStatus.CHOOSING_WORD);
        var randomWords = randomWordService.getRandomWords();

        roomInfo.setDrawingUserId(userInfo.getId());
        roomInfo.setWordChoices(randomWords);
        roomInfo.setTime(15);

        var userDrawingNotification = Notification.userDrawing(roomInfo.getChatBox().size() + 1, userInfo.getName());
        roomInfo.getChatBox().add(userDrawingNotification);

        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createChooseWordRoomMessage(userInfo.getId(), userInfo.getName(), roomInfo.getTime()));
        simpMessagingTemplate.convertAndSend(String.format("/app/user/%s/messages", userInfo.getId()), Message.createChooseWordUserMessage(randomWords));
        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createChatBoxMessage(userDrawingNotification));

        Consumer<RoomInfo> postprocessing = (RoomInfo room) -> {
            if (RoomStatus.CHOOSING_WORD.equals(room.getStatus())) {
                var wordChoice = (int) (Math.random() * room.getWordChoices().size());
                var chosenWordMessage = new ChosenWordMessage(wordChoice);
                var message = new Message(MessageType.CHOSEN_WORD, chosenWordMessage);
                handleUserChosenWordMessage(room.getId(), message);
            }
        };
        runCountdown(roomInfo, postprocessing);
    }

    private void handleUserChosenWordMessage(UUID roomId, Message message) {
        var roomInfo = roomManager.getRoom(roomId);
        var chosenWordMessage = (ChosenWordMessage) message.getData();
        var word = roomInfo.getWordChoices().get(chosenWordMessage.getWordChoice());
        var wordsLength = Arrays.stream(word.split(" ")).map(String::length).toList();

        roomInfo.setTime(90);
        roomInfo.setWord(word);
        roomInfo.setWordLength(wordsLength);
        roomInfo.setHints(new ArrayList<>(2));
        roomInfo.setStatus(RoomStatus.GUESSING_IN_PROGRESS);

        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createGuessWordRoomMessage(wordsLength, roomInfo.getTime()));
        simpMessagingTemplate.convertAndSend(String.format("/app/user/%s/messages", roomInfo.getDrawingUserId()), Message.createGuessWordUserMessage(word));

        runGuessingInProgressCountdown(roomInfo, this::guessingInProgressPostprocessing);
    }

    private void guessingInProgressPostprocessing(RoomInfo roomInfo) {
        roomInfo.setStatus(RoomStatus.GUESSING_COMPLETED);
        roomInfo.setTime(3);
        roomInfo.setBoard(new ArrayList<>());

        UserInfo currentTurnUser = null;
        var numberOfUsersGuessed = 0;
        for (var userInfo : roomInfo.getUserInfos()) {
            if (Objects.equals(userInfo.getId(), roomInfo.getDrawingUserId())) {
                currentTurnUser = userInfo;
                continue;
            }

            if (Objects.nonNull(userInfo.getTurnScore())) {
                numberOfUsersGuessed++;
            }
        }
        var score = (numberOfUsersGuessed / (roomInfo.getUserInfos().size() - 1)) * 100;
        currentTurnUser.setScore(currentTurnUser.getScore() + score);
        currentTurnUser.setTurnScore(score);
        currentTurnUser.setTurnCompleted(true);

        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages",
                roomInfo.getId()), Message.createGuessingCompletedMessage(roomInfo.getWord(), roomInfo.getUserInfos(), roomInfo.getTime()));

        Consumer<RoomInfo> guessingCompletedPostprocessing = this::guessingCompletedPostprocessing;
        runCountdown(roomInfo, guessingCompletedPostprocessing);
    }

    private void guessingCompletedPostprocessing(RoomInfo roomInfo) {
        var roundInProgress = false;
        UserInfo nextTurnUser = null;

        for (var userInfo : roomInfo.getUserInfos()) {
            userInfo.setTurnScore(null);

            if (Objects.isNull(nextTurnUser) && !userInfo.getTurnCompleted()) {
                nextTurnUser = userInfo;
                roundInProgress = true;
            }
        }


        if (roundInProgress) {
            sendWordsChoicesToUser(roomInfo, nextTurnUser);
            return;
        }

        if (roomInfo.getRound() < 3) {
            handleRoundCompletion(roomInfo);
            return;
        }

        handleGameCompletion(roomInfo);
    }

    private void handleRoundCompletion(RoomInfo roomInfo) {
        roomInfo.setStatus(RoomStatus.ROUND_COMPLETED);
        roomInfo.setTime(3);
        roomInfo.setRound(roomInfo.getRound() + 1);
        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createRoundCompletedMessage(roomInfo.getRound(), roomInfo.getTime()));

        runCountdown(roomInfo, this::startRound);
    }

    private void handleGameCompletion(RoomInfo roomInfo) {
        roomInfo.setStatus(RoomStatus.GAME_COMPLETED);
        roomInfo.setTime(15);
        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createGameCompletedMessage(roomInfo.getUserInfos(), roomInfo.getTime()));

        runCountdown(roomInfo, this::gameCompletedPostprocessing);
    }

    private void gameCompletedPostprocessing(RoomInfo roomInfo) {
        roomInfo.setRound(1);
        roomInfo.setChatBox(new ArrayList<>());

        startGameIfConditionsAllow(roomInfo);
    }

    private void runGuessingInProgressCountdown(RoomInfo roomInfo, Consumer<RoomInfo> postprocessing) {
        var scheduler = Executors.newSingleThreadScheduledExecutor();

        var countdownTask = new Runnable() {
            @Override
            public void run() {
                if (roomInfo.getTime() == 0) {
                    log.info("Guessing in progress countdown finished.");
                    postprocessing.accept(roomInfo);
                    scheduler.shutdown();
                    return;
                }

                if (roomInfo.getTime() <= 60 && roomInfo.getHints().isEmpty()) {
                    var wordIdx = (int) (Math.random() * roomInfo.getWordLength().size());
                    var words = roomInfo.getWord().split(" ");
                    var charIdx = (int) (Math.random() * words[wordIdx].length());
                    var firstHint = new Hint(wordIdx, charIdx, words[wordIdx].charAt(charIdx));
                    roomInfo.getHints().add(firstHint);

                    simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createHintMessage(firstHint));
                }

                if (roomInfo.getTime() <= 30 && roomInfo.getHints().size() < 2) {
                    var firstHint = roomInfo.getHints().getFirst();

                    int wordIdx, charIdx;
                    var words = roomInfo.getWord().split(" ");
                    do {
                        wordIdx = (int) (Math.random() * roomInfo.getWordLength().size());
                        charIdx = (int) (Math.random() * words[wordIdx].length());
                    } while (wordIdx == firstHint.getWordIndex() && charIdx == firstHint.getCharacterIndex());

                    var secondHint = new Hint(wordIdx, charIdx, words[wordIdx].charAt(charIdx));
                    roomInfo.getHints().add(secondHint);

                    simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createHintMessage(secondHint));
                }

                log.info("Guessing in progress countdown {}", roomInfo.getTime());
                roomInfo.setTime(roomInfo.getTime() - 1);
            }
        };

        scheduler.scheduleAtFixedRate(countdownTask, 0, 1, TimeUnit.SECONDS);
    }

    private void runCountdown(RoomInfo roomInfo, Consumer<RoomInfo> postprocessing) {
        var scheduler = Executors.newSingleThreadScheduledExecutor();

        var countdownTask = new Runnable() {
            @Override
            public void run() {
                if (roomInfo.getTime() > 0) {
                    log.info("Countdown {}", roomInfo.getTime());
                    roomInfo.setTime(roomInfo.getTime() - 1);
                } else {
                    log.info("Countdown finished.");
                    postprocessing.accept(roomInfo);
                    scheduler.shutdown();
                }
            }
        };

        scheduler.scheduleAtFixedRate(countdownTask, 0, 1, TimeUnit.SECONDS);
    }

    private void handleBoardMessage(String sessionId, Message message) {
        var userInfo = userManager.getUser(sessionId);
        var roomInfo = roomManager.getRoom(userInfo.getRoomId());

        var boardMessage = (BoardMessage) message.getData();
        var draw = boardMessage.getBoard();
        draw.setId(roomInfo.getBoard().size() + 1);

        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createBoardMessage(draw));
    }

    private void handleChatBoxMessage(String sessionId, Message message) {
        var userInfo = userManager.getUser(sessionId);
        var roomInfo = roomManager.getRoom(userInfo.getRoomId());

        if (RoomStatus.GUESSING_IN_PROGRESS.equals(roomInfo.getStatus())) {
            handleGuessingInProgressChatBoxMessage(message, roomInfo, userInfo);
            return;
        }

        forwardChatMessageToRoom(message, roomInfo, userInfo);
    }

    private void forwardChatMessageToRoom(Message message, RoomInfo roomInfo, UserInfo userInfo) {
        var chatBoxMessage = (ChatBoxMessage) message.getData();
        var chat = (Chat) chatBoxMessage.getChat();

        chat.setId(roomInfo.getChatBox().size() + 1);
        chat.setUsername(userInfo.getName());
        chat.setType(ChatBoxType.CHAT);
        roomInfo.getChatBox().add(chat);

        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), message);
    }

    private void handleGuessingInProgressChatBoxMessage(Message message, RoomInfo roomInfo, UserInfo userInfo) {
        if (Objects.equals(userInfo.getId(), roomInfo.getDrawingUserId())) {
            log.warn("Drawing users can't send chat messages");
            return;
        }

        if (Objects.nonNull(userInfo.getTurnScore())) {
            log.warn("Users who guessed the word can't send chat messages");
            return;
        }

        var chatBoxMessage = (ChatBoxMessage) message.getData();
        var chat = (Chat) chatBoxMessage.getChat();

        var wordMatchingStatus = wordMatchingService.match(roomInfo.getWord(), chat.getMessage());

        if (WordMatchingStatus.MISMATCH.equals(wordMatchingStatus)) {
            forwardChatMessageToRoom(message, roomInfo, userInfo);
            return;
        }

        if (WordMatchingStatus.MATCH.equals(wordMatchingStatus)) {
            handleCorrectUserGuess(roomInfo, userInfo);
            return;
        }

        handleCloseUserGuess(roomInfo, userInfo);
    }

    private void handleCorrectUserGuess(RoomInfo roomInfo, UserInfo userInfo) {
        var userGuessedNotification = Notification.userGuessed(roomInfo.getChatBox().size() + 1, userInfo.getName());
        roomInfo.getChatBox().add(userGuessedNotification);

        var score = (10 * roomInfo.getTime()) / 9;
        userInfo.setScore(userInfo.getScore() + score);
        userInfo.setTurnScore(score);

        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createChatBoxMessage(userGuessedNotification));
        simpMessagingTemplate.convertAndSend(String.format("/app/user/%s/messages", userInfo.getId()), Message.createGuessedWordMessage(roomInfo.getWord()));

        var allUsersGuessed = roomInfo.getUserInfos().stream().allMatch(user -> Objects.nonNull(user.getTurnScore()));
        if (allUsersGuessed) {
            roomInfo.setTime(0);
            return;
        }

        if (roomInfo.getTime() > 30) {
            roomInfo.setTime(30);
            simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createTimeUpdateMessage(roomInfo.getTime()));
        }
    }

    private void handleCloseUserGuess(RoomInfo roomInfo, UserInfo userInfo) {
        var userCloseGuessedNotification = Notification.userCloseGuessed(roomInfo.getChatBox().size() + 1, userInfo.getName());
        roomInfo.getChatBox().add(userCloseGuessedNotification);

        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createChatBoxMessage(userCloseGuessedNotification));
    }

    public void handleUserDisconnect(String sessionId) {
        var userInfo = userManager.getUser(sessionId);
        var roomId = userInfo.getRoomId();
        var roomInfo = roomManager.getRoom(roomId);

        roomInfo.getUserInfos().remove(userInfo);

        userManager.removeSession(sessionId);
        if (roomInfo.getUserInfos().isEmpty()) {
            roomManager.removeRoom(roomId);
        }

        if (roomInfo.getUserInfos().size() == 1) {
            roomInfo.setTime(0);
            roomInfo.setStatus(RoomStatus.WAITING);
            simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createWaitingMessage());
        }

        var userDisconnectedNotification = Notification.userDisconnected(roomInfo.getChatBox().size() + 1, userInfo.getName());
        simpMessagingTemplate.convertAndSend(String.format("/app/room/%s/messages", roomInfo.getId()), Message.createChatBoxMessage(userDisconnectedNotification));
    }
}
