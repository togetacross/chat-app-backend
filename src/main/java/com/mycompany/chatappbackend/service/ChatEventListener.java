package com.mycompany.chatappbackend.service;

import java.util.HashSet;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import com.mycompany.chatappbackend.model.dto.ChatRoom.DisplayChatRoomDTO;
import com.mycompany.chatappbackend.model.dto.Message.DisplayMessageDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageUserActivityDTO;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationAndRecipents;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationResponse;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationType;
import com.mycompany.chatappbackend.model.dto.User.ChatRoomUserDTO;
import com.mycompany.chatappbackend.model.dto.User.UserAndMessageDTO;
import com.mycompany.chatappbackend.model.entity.ChatRoom;
import com.mycompany.chatappbackend.model.entity.GroupConversationProfile;
import com.mycompany.chatappbackend.model.entity.Message;
import com.mycompany.chatappbackend.model.entity.PrivateConversationProfile;
import com.mycompany.chatappbackend.repository.MessageRepository;

@Component
public class ChatEventListener {

	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private ChatRoomUserService chatRoomUserService;
	
	@Autowired
	private MessageRepository messageRepository;
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void sendMessage(DisplayMessageDTO messageDTO) {
		List<Integer> userIds = chatRoomUserService.loadActiveChatRoomUserIds(messageDTO.getRoomId());
		userIds.stream().forEach(id -> notificationService.sendToUser(id.toString(), new NotificationResponse(messageDTO, NotificationType.MESSAGE)));			
	}
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void leaveOrRemoveUser(NotificationAndRecipents notificationAndRecipents) {
		notificationAndRecipents.getIds().forEach(id -> notificationService.sendToUser(id.toString(), notificationAndRecipents.getNotificationResponse()));
	}
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void newPrivateConversation(ChatRoom chatRoom) {
		PrivateConversationProfile profile = (PrivateConversationProfile) chatRoom.getConversationProfile();	
		for (int i = 0; i < 2; i++) {
			DisplayChatRoomDTO chatRoomDTO = DisplayChatRoomDTO.builder()
					.id(chatRoom.getId())
					.createdAt(chatRoom.getCreatedAt())
					.type(chatRoom.getType().name())
					.name(i == 0 ? profile.getRecieverUserProfile().getName() : profile.getSenderUserProfile().getName())
					.image(i == 0 ? profile.getRecieverUserProfile().getImage() : profile.getSenderUserProfile().getImage())
					.build();
			notificationService.sendToUser(
					i == 0 ? profile.getSenderUserProfile().getId().toString() : profile.getRecieverUserProfile().getId().toString(), 
					new NotificationResponse(chatRoomDTO, NotificationType.NEW_CONVERSATION)
			);
		}			
	} 
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void newGroupConversation(ChatRoom chatRoom) {
		GroupConversationProfile gcp = (GroupConversationProfile) chatRoom.getConversationProfile();
		DisplayChatRoomDTO chatRoomDTO = DisplayChatRoomDTO.builder()
				.id(chatRoom.getId())
				.name(gcp.getName())
				.image(gcp.getImage())
				.type(chatRoom.getType().name())
				.createdAt(chatRoom.getCreatedAt())
				.build();
		chatRoom.getChatRoomUsers().forEach(u-> notificationService.sendToUser(u.getKey().getUserId().toString(), new NotificationResponse(chatRoomDTO, NotificationType.NEW_CONVERSATION)));			
	} 
	
	@TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
	public void joinUser(int messageId) {
		Message message = messageRepository.findByIdWithChatRoomAndUser(messageId);
		
		GroupConversationProfile gcp = (GroupConversationProfile) message.getChatRoomUser().getChatRoom().getConversationProfile();
		DisplayChatRoomDTO chatroomDTO = DisplayChatRoomDTO.builder()
				.id(message.getChatRoomUser().getKey().getChatRoomId())
				.name(gcp.getName())
				.image(gcp.getImage())
				.type(message.getChatRoomUser().getChatRoom().getType().name())
				.createdAt(message.getChatRoomUser().getChatRoom().getCreatedAt())
				.build();
		
		DisplayMessageDTO displayMessageDTO = DisplayMessageDTO.builder()
				.id(message.getId())
				.userId(message.getChatRoomUser().getKey().getUserId())
				.roomId(message.getChatRoomUser().getKey().getChatRoomId())
				.type(message.getMessageType().name())
				.createdAt(message.getCreatedAt())
				.userActivitys(new HashSet<MessageUserActivityDTO>())
				.build();
		
		ChatRoomUserDTO chatRoomUserDTO = ChatRoomUserDTO.builder()
				.id(message.getChatRoomUser().getKey().getUserId())
				.name(message.getChatRoomUser().getUser().getUserProfile().getName())
				.image(message.getChatRoomUser().getUser().getUserProfile().getImage())
				.role(message.getChatRoomUser().getRole())
				.isOnline(false)
				.build();
		
		notificationService.sendToTopic(message.getChatRoomUser().getKey().getChatRoomId(), new NotificationResponse(new UserAndMessageDTO(chatRoomUserDTO, displayMessageDTO), NotificationType.JOIN_USER));				
		notificationService.sendToUser(String.valueOf(message.getChatRoomUser().getKey().getUserId()), new NotificationResponse(chatroomDTO, NotificationType.NEW_CONVERSATION));
	}	
}
