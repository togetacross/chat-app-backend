package com.mycompany.chatappbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationResponse;

@Service
public class NotificationService {
	
	private final static String PRIVATE_GROUP_CHANEL = "/conversation/";
	private final static String PRIVATE_CHANEL = "/private";
	private final static String PUBLIC_CHANEL = "/public";

	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	public void sendToUser(String userName, NotificationResponse response) {
		messagingTemplate.convertAndSendToUser(userName, PRIVATE_CHANEL, response);;
	}
	
	public void sendToAll(NotificationResponse response) {
		messagingTemplate.convertAndSend(PUBLIC_CHANEL, response);
	}

	public void sendToTopic(Integer conversationId ,NotificationResponse response) {
		messagingTemplate.convertAndSend(PRIVATE_GROUP_CHANEL + conversationId, response);
	}
}
