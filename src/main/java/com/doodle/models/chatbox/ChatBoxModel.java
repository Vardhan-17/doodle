package com.doodle.models.chatbox;

import com.doodle.enums.ChatBoxType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public abstract class ChatBoxModel {
    private Integer id;
    private ChatBoxType type;
    private String message;
}
