package com.doodle.models.messages;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GuessWordRoomMessage implements Data {
    private List<Integer> wordsLength;
    private Integer time;
}
