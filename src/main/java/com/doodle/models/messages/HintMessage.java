package com.doodle.models.messages;

import com.doodle.models.Hint;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class HintMessage implements Data {
    private Hint hint;
}
