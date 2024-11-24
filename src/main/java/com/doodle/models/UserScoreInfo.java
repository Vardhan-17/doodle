package com.doodle.models;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserScoreInfo {
    private UUID id;
    private Integer score;
    private Integer turnScore;

    public UserScoreInfo(UserInfo userInfo) {
        this.id = userInfo.getId();
        this.score = userInfo.getScore();
        this.turnScore = userInfo.getTurnScore();
    }
}
