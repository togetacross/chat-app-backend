package com.mycompany.chatappbackend.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mycompany.chatappbackend.model.dto.Message.MessageLikeDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageSeenDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageSeenResponseDTO;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationResponse;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationType;
import com.mycompany.chatappbackend.model.entity.Message;
import com.mycompany.chatappbackend.model.entity.MessageActivityUserKey;
import com.mycompany.chatappbackend.model.entity.MessageUserActivity;
import com.mycompany.chatappbackend.repository.MesageUserActivityRepository;

@Service
public class MesssageUserActivityService {
	
	@Autowired
	private MesageUserActivityRepository messageUserActivityRepository;
		
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private NotificationService notificationService;
	
	
	@Transactional
	public void seenMessage(MessageSeenDTO messageSeenDTO, int userId) {
		
		List<Message> messages = messageService.getMessagesByMessageIds(messageSeenDTO.getMessageIds());
		OffsetDateTime seenAt = OffsetDateTime.now(ZoneId.systemDefault());
		
		messages.forEach(message->
			{
				MessageUserActivity messageUserActivity = messageUserActivityRepository
						.findById(new MessageActivityUserKey(userId, message.getId()))
						.orElse(new MessageUserActivity(userService.getReferenceById(userId), message));
				
				if(messageUserActivity.getSeenAt() == null) {
					messageUserActivity.setSeenAt(seenAt);
				}
				messageUserActivityRepository.save(messageUserActivity);		
			});
			
		notificationService.sendToTopic(
				messageSeenDTO.getChatroomId(), 
				new NotificationResponse(
						new MessageSeenResponseDTO(
								messageSeenDTO.getMessageIds(),
								userId, 
								seenAt
						), 
						NotificationType.SEEN)
		);		
	}
	
	@Transactional
	public void proccessLike(MessageLikeDTO messageLikeDTO, int userId, boolean isLiked) {
		
		MessageUserActivity messageUserActivity = messageUserActivityRepository
				.findById(new MessageActivityUserKey(userId, messageLikeDTO.getMessageId()))
				.orElse(new MessageUserActivity(userService.getReferenceById(userId), 
						messageService.getMessageById(messageLikeDTO.getMessageId())));
		messageUserActivity.setLiked(isLiked);
		messageUserActivityRepository.save(messageUserActivity);
		
		notificationService.sendToTopic(
				messageLikeDTO.getRoomId(), 
				new NotificationResponse(messageLikeDTO, NotificationType.LIKE)
		);
	}
	
}
