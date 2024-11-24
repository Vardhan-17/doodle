package com.doodle.models.messages;

import com.doodle.models.RoomInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class JoinedRoomMessage implements Data {
    private RoomInfo roomInfo;
}
