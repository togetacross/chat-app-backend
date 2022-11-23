package com.mycompany.chatappbackend.controller;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RestController;
import com.mycompany.chatappbackend.model.dto.Message.MessageLikeDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageSeenDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageSeenResponseDTO;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationResponse;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationType;
import com.mycompany.chatappbackend.model.dto.Notification.UserTypeNotification;
import com.mycompany.chatappbackend.model.dto.User.UserTypingDTO;
import com.mycompany.chatappbackend.model.entity.Message;
import com.mycompany.chatappbackend.model.entity.User;
import com.mycompany.chatappbackend.service.MessageService;
import com.mycompany.chatappbackend.service.MesssageUserActivityService;
import com.mycompany.chatappbackend.service.NotificationService;
import com.mycompany.chatappbackend.service.UserService;
import com.mycompany.chatappbackend.security.UserPrinciple;

@RestController
public class WsChatController {
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserService userService;

	@Autowired
	private MesssageUserActivityService messsageUserActivityService;
	
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
	
		List<Message> messages = messageService.getMessagesByMessageIds(messageSeenDTO.getMessageIds());
		User user = userService.getReferenceById(userPrinciple.getId());
		OffsetDateTime seenAt = OffsetDateTime.now(ZoneId.systemDefault());
		
		messages.forEach(message-> messsageUserActivityService.seenMessage(message, user, seenAt));	
		notificationService.sendToTopic(
				messageSeenDTO.getChatroomId(), 
				new NotificationResponse(
						new MessageSeenResponseDTO(
								messageSeenDTO.getMessageIds(),
								userPrinciple.getId(), 
								OffsetDateTime.now(ZoneId.systemDefault()
						)), 
						NotificationType.SEEN)
		);
	}
	
	@PreAuthorize("@convesrationRoleService.hasRole(#userPrinciple.getId, #messageLikeDTO.roomId, 'USER')")
	@MessageMapping("/like")
	public void processLikeRequest(
			@Payload MessageLikeDTO messageLikeDTO, 
			@AuthenticationPrincipal UserPrinciple userPrinciple) 
	{		
		User user = userService.getReferenceById(userPrinciple.getId());
		Message message = messageService.getMessageById(messageLikeDTO.getMessageId());
		messsageUserActivityService.proccessLike(message, user, messageLikeDTO.isLiked());
		
		messageLikeDTO.setUserId(userPrinciple.getId());
		
		notificationService.sendToTopic(
				messageLikeDTO.getRoomId(), 
				new NotificationResponse(messageLikeDTO, NotificationType.LIKE)
		);
	}
	
	
}
