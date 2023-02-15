package com.mycompany.chatappbackend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import com.mycompany.chatappbackend.model.dto.Message.MessageLikeDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageSeenDTO;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationResponse;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationType;
import com.mycompany.chatappbackend.model.dto.Notification.UserTypeNotification;
import com.mycompany.chatappbackend.model.dto.User.UserTypingDTO;
import com.mycompany.chatappbackend.service.MesssageUserActivityService;
import com.mycompany.chatappbackend.service.NotificationService;
import com.mycompany.chatappbackend.security.UserPrinciple;

@RestController
public class WsChatController {
	
	@Autowired
	private NotificationService notificationService;

	@Autowired
	private MesssageUserActivityService messageUserActivityService;
	
	@PreAuthorize("@convesrationRoleService.hasRole(#userPrinciple.getId, #userTypingDTO.roomId, 'USER')")
	@MessageMapping("/type")
	public void processTypeRequest(@Payload UserTypingDTO userTypingDTO, @AuthenticationPrincipal UserPrinciple userPrinciple) {
		notificationService.sendToTopic(
				userTypingDTO.getRoomId(), 
				new NotificationResponse(new UserTypeNotification(userPrinciple.getId(), userTypingDTO.getRoomId(), userTypingDTO.isType()), NotificationType.TYPE)
		);
	}
	
	@PreAuthorize("@convesrationRoleService.hasRole(#userPrinciple.getId, #messageSeenDTO.chatroomId, 'USER')")
	@MessageMapping("/seen")
	public void processSeenRequest(@Payload MessageSeenDTO messageSeenDTO, @AuthenticationPrincipal UserPrinciple userPrinciple) {
		messageUserActivityService.seenMessage(messageSeenDTO, userPrinciple.getId());
	}
	
	@PreAuthorize("@convesrationRoleService.hasRole(#userPrinciple.getId, #messageLikeDTO.roomId, 'USER')")
	@MessageMapping("/like")
	public void processLikeRequest(
			@Payload MessageLikeDTO messageLikeDTO, 
			@AuthenticationPrincipal UserPrinciple userPrinciple) 
	{		
		messageUserActivityService.proccessLike(messageLikeDTO, userPrinciple.getId(), messageLikeDTO.isLiked());
	}
	
	
}
