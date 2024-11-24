package com.doodle.models.messages;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.*;

@Getter
@Setter
@ToString
@JsonSerialize
public class WaitingMessage implements Data {
}
