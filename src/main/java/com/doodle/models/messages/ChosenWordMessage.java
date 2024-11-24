package com.doodle.models.messages;

import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ChosenWordMessage implements Data {
    private Integer wordChoice;
}
