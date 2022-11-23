package com.mycompany.chatappbackend.model.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserTypingDTO {

	private Integer roomId;
	private boolean isType;
}
