package com.doodle.models.chatbox;

import com.doodle.enums.ChatBoxType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Notification extends ChatBoxModel {
    private String color;

    public Notification(Integer id, String message, String color) {
        super(id, ChatBoxType.NOTIFICATION, message);
        this.color = color;
    }

    public static Notification userJoined(Integer id, String username) {
        var message = String.format("%s joined the room", username);
        return new Notification(id, message, "GREEN");
    }

    public static Notification userDrawing(int id, String username) {
        var message = String.format("%s is drawing", username);
        return new Notification(id, message, "BLUE");
    }

    public static Notification userGuessed(int id, String username) {
        var message = String.format("%s guessed the word", username);
        return new Notification(id, message, "GREEN");
    }

    public static Notification userCloseGuessed(int id, String username) {
        var message = String.format("%s guess is close", username);
        return new Notification(id, message, "BLUE");
    }

    public static Notification userDisconnected(int id, String username) {
        var message = String.format("%s left the room", username);
        return new Notification(id, message, "RED");
    }
}
