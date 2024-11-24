package com.doodle.models.messages;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GuessedWordUserMessage implements Data {
    private String word;
}
