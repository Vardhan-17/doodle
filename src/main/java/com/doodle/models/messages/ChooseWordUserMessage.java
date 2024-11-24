package com.doodle.models.messages;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChooseWordUserMessage implements Data {
    private List<String> wordChoices;
}
