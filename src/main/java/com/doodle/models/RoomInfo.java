package com.doodle.models;

import com.doodle.enums.RoomStatus;
import com.doodle.models.board.BoardModel;
import com.doodle.models.chatbox.ChatBoxModel;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class RoomInfo {
    private UUID id;
    private RoomStatus status;
    private List<UserInfo> userInfos;
    private List<BoardModel> board;
    private List<ChatBoxModel> chatBox;
    private Integer time;
    private UUID drawingUserId;
    private List<Integer> wordLength;
    private List<Hint> hints;
    private Integer round;
    @JsonIgnore
    private String word;
    @JsonIgnore
    private List<String> wordChoices;


    public RoomInfo() {
        this.id = UUID.randomUUID();
        this.status = RoomStatus.WAITING;
        this.round = 1;
        this.userInfos = new ArrayList<>();
        this.board = new ArrayList<>();
        this.chatBox = new ArrayList<>();
        this.hints = new ArrayList<>(2);
    }
}
