package com.doodle.models.messages;

import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@JsonTypeName("JOIN_ROOM")
public class JoinRoomMessage implements Data {
    private UUID userId;
    private String username;
    private List<Integer> avatarCustomizations;
}
