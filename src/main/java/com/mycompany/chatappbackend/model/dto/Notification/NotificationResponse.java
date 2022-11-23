package com.mycompany.chatappbackend.model.dto.Notification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class NotificationResponse {

	private Object response;
	private NotificationType notificationType;
}
