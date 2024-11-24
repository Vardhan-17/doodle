package com.doodle.models.board;

import com.doodle.enums.BoardMessageType;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public abstract class BoardModel {
    private Integer id;
    private BoardMessageType type;
}
