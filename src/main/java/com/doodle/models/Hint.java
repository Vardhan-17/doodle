package com.doodle.models;

import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Hint {
    private Integer wordIndex;
    private Integer characterIndex;
    private Character character;
}
