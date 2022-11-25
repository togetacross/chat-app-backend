package com.mycompany.chatappbackend.service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mycompany.chatappbackend.model.dto.ChatRoom.DisplayChatRoomDTO;
import com.mycompany.chatappbackend.model.dto.ChatRoom.InitConversationDTO;
import com.mycompany.chatappbackend.model.dto.ChatRoom.NewChatRoomDTO;
import com.mycompany.chatappbackend.model.dto.Message.DisplayMessageDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageSliceDTO;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationResponse;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationType;
import com.mycompany.chatappbackend.model.dto.User.ChatRoomUserDTO;
import com.mycompany.chatappbackend.model.dto.User.UserAndMessageDTO;
import com.mycompany.chatappbackend.model.entity.ChatRoom;
import com.mycompany.chatappbackend.model.entity.ChatRoomType;
import com.mycompany.chatappbackend.model.entity.ChatRoomUser;
import com.mycompany.chatappbackend.model.entity.ChatRoomUserRole;
import com.mycompany.chatappbackend.model.entity.GroupConversationProfile;
import com.mycompany.chatappbackend.model.entity.Message;
import com.mycompany.chatappbackend.model.entity.MessageType;
import com.mycompany.chatappbackend.model.entity.PrivateConversationProfile;
import com.mycompany.chatappbackend.model.entity.User;
import com.mycompany.chatappbackend.repository.ChatRoomRepository;
import com.mycompany.chatappbackend.exception.ResourceNotFoundException;

@Service
public class ChatRoomService {

	@Autowired
	private ChatRoomRepository chatRoomRepository;

	@Autowired
	private UserService userService;
	
	@Autowired
	private ChatRoomUserService chatRoomUserService;
	
	@Autowired
	private MessageService messageService;
	
	@Autowired
	private NotificationService notificationService;
	
	@Autowired
	private FileService fileService;

	@Transactional
	public void leaveUserFromConversation(Integer roomId, Integer userId) {	
		List<Integer> userIds = chatRoomUserService.loadActiveChatRoomUserIds(roomId);
		NotificationResponse removeNotification = new NotificationResponse(roomId, NotificationType.CONVERSATION_REMOVED);
		if(userIds.size() <= 2) {
			chatRoomRepository.deleteById(roomId);
			userIds.forEach(id -> notificationService.sendToUser(id.toString(), removeNotification));
		} else {
			chatRoomUserService.updateRole(userId, roomId, ChatRoomUserRole.NONE);
			DisplayMessageDTO message = messageService.saveUserActivityMessage(userId, roomId, MessageType.LEAVE);
			userIds.stream().filter(id-> id != userId).forEach(id -> notificationService.sendToUser(id.toString(), new NotificationResponse(message, NotificationType.LEAVE)));
			notificationService.sendToUser(userId.toString(), removeNotification);
		}
	}

	@Transactional
	public void addUserToConversation(Integer roomId, Integer userId) {
		ChatRoom chatRoom = chatRoomRepository.findGroupChatRoomByIdWithProfile(roomId)
				.orElseThrow(()-> new ResourceNotFoundException("Conversation not found!"));
		
		User user = userService.getUserByIdWithProfile(userId);
		ChatRoomUser chatroomUser = chatRoomUserService.newChatRoomUser(user, chatRoom);
		DisplayMessageDTO displayMessageDTO = messageService.saveUserActivityMessage(userId, roomId, MessageType.JOIN);
		
		GroupConversationProfile gcp = (GroupConversationProfile) chatRoom.getConversationProfile().iterator().next();
		DisplayChatRoomDTO chatroomDTO = DisplayChatRoomDTO.builder()
				.id(chatRoom.getId())
				.name(gcp.getName())
				.image(gcp.getImage())
				.type(chatRoom.getType().name())
				.createdAt(OffsetDateTime.now(ZoneId.systemDefault()))
				.build();
		
		ChatRoomUserDTO chatRoomUserDTO = ChatRoomUserDTO.builder()
				.id(chatroomUser.getKey().getUserId())
				.name(user.getUserProfile().getName())
				.image(user.getUserProfile().getImage())
				.role(chatroomUser.getRole())
				.isOnline(false)
				.build();
			
		notificationService.sendToTopic(roomId, new NotificationResponse(new UserAndMessageDTO(chatRoomUserDTO, displayMessageDTO), NotificationType.JOIN_USER));				
		notificationService.sendToUser(user.getId().toString(), new NotificationResponse(chatroomDTO, NotificationType.NEW_CONVERSATION));
	}
	
	
	@Transactional
	public void createGroupConversation(MultipartFile file, NewChatRoomDTO chatRoomDTO, int creatorUserId) {
		byte[] image = fileService.getMultipartFileInByte(file);
		OffsetDateTime currentDateTime = OffsetDateTime.now(ZoneId.systemDefault());	
		ChatRoom chatroom = new ChatRoom(ChatRoomType.GROUP, currentDateTime);
		chatroom.addConversationProfile(new GroupConversationProfile(chatRoomDTO.getName(), image));
		List<Integer> userIds = chatRoomDTO.getUserIds();
		userIds.add(0, creatorUserId);
		
		Set<User> users = userService.getUsersWhereIdIn(userIds);
		users.forEach(user -> {
				boolean isSenderUser = user.getId() == creatorUserId;
				ChatRoomUser chatroomUser = new ChatRoomUser(user, isSenderUser ? ChatRoomUserRole.ADMIN : ChatRoomUserRole.USER);
				chatroom.addChatRoomUser(chatroomUser);
				chatroomUser.addMessage(new Message(isSenderUser ? MessageType.CREATED : MessageType.JOIN, currentDateTime));
			}
		);
		
		ChatRoom savedChatRoom = chatRoomRepository.save(chatroom);
		GroupConversationProfile gcp = (GroupConversationProfile) savedChatRoom.getConversationProfile().iterator().next();
		DisplayChatRoomDTO savedChatRoomDTO = DisplayChatRoomDTO.builder()
				.id(savedChatRoom.getId())
				.name(gcp.getName())
				.image(gcp.getImage())
				.type(savedChatRoom.getType().name())
				.createdAt(savedChatRoom.getCreatedAt())
				.build();

		users.forEach(user-> notificationService.sendToUser(user.getId().toString(), new NotificationResponse(savedChatRoomDTO, NotificationType.NEW_CONVERSATION)));
	}
	
	@Transactional
	public void createPrivateConversation(int senderUserId, int receiverUserId) {	
		OffsetDateTime currentDateTime = OffsetDateTime.now(ZoneId.systemDefault());
		
		boolean hasConversation = chatRoomRepository.existsByUsersInPrivateProfile(senderUserId, receiverUserId);
		if(hasConversation) {
			throw new ResourceNotFoundException("Conversation is already exists!");
		}
		
		ChatRoom chatroom = new ChatRoom(ChatRoomType.PRIVATE, currentDateTime);
		
		List<User> users = userService.getUsersWhereIdInWithProfile(List.of(senderUserId, receiverUserId));
		chatroom.addConversationProfile(new PrivateConversationProfile(users.get(0).getUserProfile(), users.get(1)));
		chatroom.addConversationProfile(new PrivateConversationProfile(users.get(1).getUserProfile(), users.get(0)));
		users.forEach(user -> {
			ChatRoomUser chatroomUser = new ChatRoomUser(user, ChatRoomUserRole.ADMIN);
			if(user.getId() == senderUserId) { 
				chatroomUser.addMessage(new Message(MessageType.CREATED, currentDateTime));
			}
			chatroom.addChatRoomUser(chatroomUser);
		});
		ChatRoom savedChatRoom = chatRoomRepository.save(chatroom);
		
		savedChatRoom.getConversationProfile().forEach(profile-> {
			PrivateConversationProfile privateConversationProfile = (PrivateConversationProfile) profile;
			DisplayChatRoomDTO chatRoomDTO = DisplayChatRoomDTO.builder()
					.id(savedChatRoom.getId())
					.createdAt(savedChatRoom.getCreatedAt())
					.type(savedChatRoom.getType().name())
					.name(privateConversationProfile.getRecieverUserProfile().getName())
					.image(privateConversationProfile.getRecieverUserProfile().getImage())
					.build();
			
			notificationService.sendToUser(profile.getUser().getId().toString(), new NotificationResponse(chatRoomDTO, NotificationType.NEW_CONVERSATION));
		});
	}
	
	public List<DisplayChatRoomDTO> getUserConversations(int userId) throws ResourceNotFoundException {	
		List<DisplayChatRoomDTO> conversations = chatRoomRepository.findByUserId(userId);
		if(conversations.isEmpty()) {
			throw new ResourceNotFoundException("No conversations found!");
		}
		return conversations;
	}
	
	public InitConversationDTO initConversation(Integer chatRoomId) {
		List<ChatRoomUserDTO> chatroomUsersDto = chatRoomUserService.loadChatRoomUsersById(chatRoomId);
		MessageSliceDTO messageSliceDTO = messageService.getTopMessagesByChatRoomId(chatRoomId);
		return new InitConversationDTO(chatroomUsersDto, messageSliceDTO);
	}
	
	@Transactional
	public void processNewMessage(MultipartFile[] files, MessageDTO messageDTO, Integer userId) {
		DisplayMessageDTO displayMessageDTO = messageService.saveMessage(files, messageDTO, userId);
		chatRoomRepository.updateChatRoomLastActivity(messageDTO.getDateTime(), messageDTO.getChatRoomId());
		List<Integer> conversationUserIds = chatRoomUserService.loadActiveChatRoomUserIds(messageDTO.getChatRoomId());
		conversationUserIds.forEach(id -> notificationService.sendToUser(id.toString(), new NotificationResponse(displayMessageDTO, NotificationType.MESSAGE)));
	}

	/*
	@Autowired
	private FileService fileService;
	
		byte[] image = null;
		Resource resource = new ClassPathResource("group.png");
		try {
			image = resource.getInputStream().readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	 */

}
