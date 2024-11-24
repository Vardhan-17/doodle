package com.doodle.models.messages;

import com.doodle.models.board.BoardModel;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class BoardMessage implements Data {
    private BoardModel board;
}
