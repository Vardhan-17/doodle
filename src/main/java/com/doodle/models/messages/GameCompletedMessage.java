package com.doodle.models.messages;

import com.doodle.models.UserInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class GameCompletedMessage implements Data {
    private List<UserInfo> userInfos;
    private Integer time;
}
