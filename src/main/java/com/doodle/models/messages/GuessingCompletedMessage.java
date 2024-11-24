package com.doodle.models.messages;

import com.doodle.models.UserScoreInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GuessingCompletedMessage implements Data {
    private String word;
    private List<UserScoreInfo> userScoreInfos;
    private Integer time;
}
