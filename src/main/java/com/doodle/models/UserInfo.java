package com.doodle.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class UserInfo {
    private UUID id;
    private String name;
    private Integer score;
    private List<Integer> avatarCustomizations;
    @JsonIgnore
    private Integer turnScore;
    @JsonIgnore
    private Boolean turnCompleted;
    @JsonIgnore
    private String sessionId;
    @JsonIgnore
    private UUID roomId;

    public UserInfo(UUID id, String name, List<Integer> avatarCustomizations, String sessionId) {
        this.id = id;
        this.name = name;
        this.avatarCustomizations = avatarCustomizations;
        this.score = 0;
        this.turnCompleted = false;
        this.sessionId = sessionId;
    }
}
