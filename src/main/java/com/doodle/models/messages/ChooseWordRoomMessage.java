package com.doodle.models.messages;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChooseWordRoomMessage implements Data {
    private UUID userId;
    private String username;
    private Integer time;
}
