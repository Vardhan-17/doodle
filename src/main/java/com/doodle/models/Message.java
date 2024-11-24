package com.doodle.models;

import com.doodle.enums.MessageType;
import com.doodle.models.board.BoardModel;
import com.doodle.models.board.DrawOnBoard;
import com.doodle.models.chatbox.ChatBoxModel;
import com.doodle.models.messages.*;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private MessageType type;
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type", include = JsonTypeInfo.As.EXTERNAL_PROPERTY)
    @JsonSubTypes({
            @JsonSubTypes.Type(value = JoinRoomMessage.class, name = "JOIN_ROOM"),
            @JsonSubTypes.Type(value = ChatBoxMessage.class, name = "CHAT_BOX"),
            @JsonSubTypes.Type(value = ChosenWordMessage.class, name = "CHOSEN_WORD"),
            @JsonSubTypes.Type(value = BoardMessage.class, name = "DRAW")
    })
    private Data data;

    public static Message createJoinedRoomMessage(RoomInfo roomInfo) {
        var data = new JoinedRoomMessage(roomInfo);
        return new Message(MessageType.JOINED_ROOM, data);
    }

    public static Message createUserInfosMessage(List<UserInfo> userInfos) {
        var data = new UserInfosMessage(userInfos);
        return new Message(MessageType.USER_INFOS, data);
    }

    public static Message createBoardMessage(BoardModel boardModel) {
        var data = new BoardMessage(boardModel);
        return new Message(MessageType.BOARD, data);
    }

    public static Message createChatBoxMessage(ChatBoxModel chatBoxModel) {
        var data = new ChatBoxMessage(chatBoxModel);
        return new Message(MessageType.CHAT_BOX, data);
    }

    public static Message createChooseWordRoomMessage(UUID userId, String username, int time) {
        var data = new ChooseWordRoomMessage(userId, username, time);
        return new Message(MessageType.CHOOSE_WORD_ROOM, data);
    }

    public static Message createChooseWordUserMessage(List<String> randomWords) {
        var data = new ChooseWordUserMessage(randomWords);
        return new Message(MessageType.CHOOSE_WORD_USER, data);
    }

    public static Message createGuessWordRoomMessage(List<Integer> wordsLength, int time) {
        var data = new GuessWordRoomMessage(wordsLength, time);
        return new Message(MessageType.GUESS_WORD_ROOM, data);
    }

    public static Message createGuessWordUserMessage(String word) {
        var data = new GuessWordUserMessage(word);
        return new Message(MessageType.GUESS_WORD_USER, data);
    }

    public static Message createGuessedWordMessage(String word) {
        var data = new GuessWordUserMessage(word);
        return new Message(MessageType.GUESSED_WORD, data);
    }

    public static Message createGuessingCompletedMessage(String word, List<UserInfo> userInfos, Integer time) {
        var userScoreInfos = userInfos.stream().map(UserScoreInfo::new).toList();
        var data = new GuessingCompletedMessage(word, userScoreInfos, time);
        return new Message(MessageType.GUESSING_COMPLETED, data);
    }

    public static Message createRoundCompletedMessage(Integer nextRound, Integer time) {
        var data = new RoundCompletedMessage(nextRound, time);
        return new Message(MessageType.ROUND_COMPLETED, data);
    }

    public static Message createGameCompletedMessage(List<UserInfo> userInfos, Integer time) {
        var data = new GameCompletedMessage(userInfos, time);
        return new Message(MessageType.GAME_COMPLETED, data);
    }

    public static Message createTimeUpdateMessage(Integer time) {
        var data = new TimeUpdateMessage(time);
        return new Message(MessageType.TIME_UPDATE, data);
    }

    public static Message createHintMessage(Hint hint) {
        var data = new HintMessage(hint);
        return new Message(MessageType.HINT, data);
    }

    public static Message createWaitingMessage() {
        var data = new WaitingMessage();
        return new Message(MessageType.WAITING, data);
    }
}
