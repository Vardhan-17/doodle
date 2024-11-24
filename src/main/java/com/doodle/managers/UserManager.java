package com.doodle.managers;

import com.doodle.models.messages.JoinRoomMessage;
import com.doodle.models.UserInfo;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserManager {
    private final Map<String, UserInfo> users = new ConcurrentHashMap<>();

    public UserInfo addUser(String sessionId, JoinRoomMessage joinRoomMessage) {
        var userInfo = new UserInfo(joinRoomMessage.getUserId(), joinRoomMessage.getUsername(), joinRoomMessage.getAvatarCustomizations(), sessionId);
        users.put(sessionId, userInfo);

        return userInfo;
    }

    public void removeSession(String sessionId) {
        users.remove(sessionId);
    }

    public UserInfo getUser(String sessionId) {
        return users.get(sessionId);
    }
}
