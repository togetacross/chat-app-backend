package com.mycompany.chatappbackend.model.dto.Notification;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserTypeNotification {

	private Integer userId;
	private Integer roomId;
	private boolean typing;
}
