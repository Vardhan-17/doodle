package com.doodle.models.messages;

import com.doodle.models.chatbox.Chat;
import com.doodle.models.chatbox.ChatBoxModel;
import com.doodle.models.chatbox.Notification;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@JsonTypeName("CHAT_BOX")
public class ChatBoxMessage implements Data {
    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
    @JsonSubTypes({
            @JsonSubTypes.Type(value = Notification.class, name = "NOTIFICATION"),
            @JsonSubTypes.Type(value = Chat.class, name = "CHAT"),
    })
    private ChatBoxModel chat;
}
