package com.doodle.models.messages;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RoundCompletedMessage implements Data {
    private Integer newRound;
    private Integer time;
}
