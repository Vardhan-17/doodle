package com.doodle.models.board;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DrawOnBoard extends BoardModel {
    private Double startX;
    private Double startY;
    private Double endX;
    private Double endY;
    private String color;
    private Integer lineWidth;
}
