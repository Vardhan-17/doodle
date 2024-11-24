package com.doodle.managers;

import com.doodle.models.RoomInfo;
import com.doodle.models.UserInfo;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RoomManager {
    private final Map<UUID, RoomInfo> rooms = new ConcurrentHashMap<>();

    public RoomInfo joinRoom(UserInfo userInfo) {
        RoomInfo roomInfo = null;

        for (var room : rooms.values()) {
            if (room.getUserInfos().size() < 6) {
                roomInfo = room;
                break;
            }
        }

        if (roomInfo == null) {
            roomInfo = new RoomInfo();
        }

        userInfo.setRoomId(roomInfo.getId());
        roomInfo.getUserInfos().add(userInfo);

        rooms.put(roomInfo.getId(), roomInfo);

        return roomInfo;
    }

    public RoomInfo getRoom(UUID roomId) {
        return rooms.get(roomId);
    }

    public void removeRoom(UUID roomId) {
        rooms.remove(roomId);
    }
}
