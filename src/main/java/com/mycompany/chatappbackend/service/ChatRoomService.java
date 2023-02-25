package com.mycompany.chatappbackend.service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
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
import com.mycompany.chatappbackend.model.dto.Notification.NotificationAndRecipents;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationResponse;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationType;
import com.mycompany.chatappbackend.model.dto.User.ChatRoomUserDTO;
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
	private FileService fileService;
	
	@Autowired
	private ChatEventListener chatEventListener;

	
	@Transactional
	public void createGroupConversation(MultipartFile file, NewChatRoomDTO chatRoomDTO, int creatorUserId) {
		byte[] image = fileService.getMultipartFileInByte(file);	
		ChatRoom chatroom = new ChatRoom(ChatRoomType.GROUP, new GroupConversationProfile(chatRoomDTO.getName(), image));
		List<Integer> userIds = chatRoomDTO.getUserIds();
		userIds.add(0, creatorUserId);
		
		Set<User> users = userService.getUsersWhereIdIn(userIds);
		users.forEach(user -> {
				boolean isSenderUser = user.getId() == creatorUserId;
				ChatRoomUser chatroomUser = new ChatRoomUser(user, isSenderUser ? ChatRoomUserRole.ADMIN : ChatRoomUserRole.USER);
				chatroom.addChatRoomUser(chatroomUser);
				chatroomUser.addMessage(new Message(isSenderUser ? MessageType.CREATED : MessageType.JOIN));
			}
		);
		
		ChatRoom savedChatRoom = chatRoomRepository.save(chatroom);	
		chatEventListener.newGroupConversation(savedChatRoom);
	}
	
	@Transactional
	public void createPrivateConversation(int senderUserId, int receiverUserId) {
		
		boolean hasConversation = chatRoomRepository.existsByUsersInPrivateProfile(senderUserId, receiverUserId);
		if(hasConversation) {
			throw new RuntimeException("Conversation is already exists!");
		}
		
		List<User> users = userService.getUsersWhereIdInWithProfile(List.of(senderUserId, receiverUserId));
		
		ChatRoom chatroom = new ChatRoom(ChatRoomType.PRIVATE, new PrivateConversationProfile(users.get(0).getUserProfile(), users.get(1).getUserProfile()));
		
		users.forEach(user -> {
			ChatRoomUser chatroomUser = new ChatRoomUser(user, ChatRoomUserRole.ADMIN);
			if(user.getId() == senderUserId) { 
				chatroomUser.addMessage(new Message(MessageType.CREATED));
			}
			chatroom.addChatRoomUser(chatroomUser);
		});
		
		ChatRoom savedChatRoom = chatRoomRepository.save(chatroom);
		chatEventListener.newPrivateConversation(savedChatRoom);
		
	}
	
	@Transactional
	public void leaveUserFromConversation(Integer roomId, Integer userId) {	
		List<Integer> userIds = chatRoomUserService.loadActiveChatRoomUserIds(roomId);
		NotificationResponse removeNotification = new NotificationResponse(roomId, NotificationType.CONVERSATION_REMOVED);
		if(userIds.size() <= 2) {
			chatRoomRepository.deleteById(roomId);
			chatEventListener.leaveOrRemoveUser(new NotificationAndRecipents(userIds, removeNotification));
		} else {
			chatRoomUserService.updateRole(userId, roomId, ChatRoomUserRole.NONE);
			userIds.removeIf(id -> id == userId);
			DisplayMessageDTO message = messageService.saveUserActivityMessage(userId, roomId, MessageType.LEAVE);
			chatEventListener.leaveOrRemoveUser(new NotificationAndRecipents(userIds, new NotificationResponse(message, NotificationType.LEAVE)));
			chatEventListener.leaveOrRemoveUser(new NotificationAndRecipents(List.of(userId), removeNotification));
		}
	}
	
	@Transactional
	public void addUserToConversation(Integer roomId, Integer userId) {
		ChatRoom chatRoom = chatRoomRepository.getReferenceById(roomId);	
		User user = userService.getReferenceById(userId);
		chatRoomUserService.newChatRoomUser(user, chatRoom);
		DisplayMessageDTO displayMessageDTO = messageService.saveUserActivityMessage(userId, roomId, MessageType.JOIN);
		chatEventListener.joinUser(displayMessageDTO.getId());
	}
	
	public List<DisplayChatRoomDTO> getUserConversations(int userId) throws ResourceNotFoundException {
		List<ChatRoom> conversations = chatRoomRepository.findByUserId(userId);
		
		if(conversations.isEmpty()) {
			throw new ResourceNotFoundException("Conversations not found!");
		}
		
		List<DisplayChatRoomDTO> conversationsDTO = conversations.stream().map(c -> {
			String name = "";
			byte [] image = null;
			if(c.getConversationProfile() instanceof PrivateConversationProfile) {			
				PrivateConversationProfile profile = (PrivateConversationProfile) c.getConversationProfile();
				boolean isSender = profile.getSenderUserProfile().getId() == userId;
				name = isSender ? profile.getRecieverUserProfile().getName() : profile.getSenderUserProfile().getName();
				image = isSender ? profile.getRecieverUserProfile().getImage() : profile.getSenderUserProfile().getImage() ;
			} else {
				GroupConversationProfile profile = (GroupConversationProfile) c.getConversationProfile();				
				name = profile.getName();
				image = profile.getImage();
			}
					
			DisplayChatRoomDTO cr =	DisplayChatRoomDTO.builder()
					.id(c.getId())
					.createdAt(c.getCreatedAt())
					.type(c.getType().name())
					.name(name)
					.image(image)
					.build();
			return cr;
		}).collect(Collectors.toList());
		
	
		return conversationsDTO;
	}
	
	
	public InitConversationDTO initConversation(Integer chatRoomId) {
		List<ChatRoomUserDTO> chatroomUsersDto = chatRoomUserService.loadChatRoomUsersById(chatRoomId);
		MessageSliceDTO messageSliceDTO = messageService.getPaginateMessages(chatRoomId, null);
		return new InitConversationDTO(chatroomUsersDto, messageSliceDTO);
	}
	
	public Integer loadAllConversationCount() {
		return chatRoomRepository.findAll().size();
	}
	
	@Transactional
	public void processNewMessage(MultipartFile[] files, MessageDTO messageDTO, Integer userId) {
		DisplayMessageDTO displayMessageDTO = messageService.saveMessage(files, messageDTO, userId);
		chatEventListener.sendMessage(displayMessageDTO);
		chatRoomRepository.updateChatRoomLastActivity(messageDTO.getDateTime(), messageDTO.getChatRoomId());
	}
}
