package com.mycompany.chatappbackend.model.dto.Notification;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class NotificationAndRecipents {

	private List<Integer> ids;
	private NotificationResponse notificationResponse;
}
