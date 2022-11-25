package com.mycompany.chatappbackend.service;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mycompany.chatappbackend.model.dto.User.ChatRoomUserDTO;
import com.mycompany.chatappbackend.model.entity.ChatRoom;
import com.mycompany.chatappbackend.model.entity.ChatRoomUser;
import com.mycompany.chatappbackend.model.entity.ChatRoomUserPK;
import com.mycompany.chatappbackend.model.entity.ChatRoomUserRole;
import com.mycompany.chatappbackend.model.entity.User;
import com.mycompany.chatappbackend.repository.ChatRoomUserRepository;

@Service
public class ChatRoomUserService {

	@Autowired
	private ChatRoomUserRepository chatRoomUserRepository;
	
	@Autowired
	private SocketRegsitryService socketRegsitryService;
	
	public void saveChatRoomUser(ChatRoomUser chatRoomUser) {
		chatRoomUserRepository.save(chatRoomUser);
	}
	
	public ChatRoomUser getReferenceById(Integer userId, Integer chatRoomId) {
		return chatRoomUserRepository.getReferenceById(new ChatRoomUserPK(userId, chatRoomId));
	}
	
	public Optional<ChatRoomUser> getChatRoomUserById(Integer userId, Integer chatRoomId) {
		return chatRoomUserRepository.findById(new ChatRoomUserPK(userId, chatRoomId));
	}

	public List<Integer> loadActiveChatRoomUserIds(Integer roomId) {
		return chatRoomUserRepository.getActiveUserIdsByChatRoomId(roomId);
	}
	
	public List<String> loadActiveChatRoomUserNames(Integer roomId) {
		return chatRoomUserRepository.findNameByChatRoomIdAndRoleIsNotNone(roomId);
	}
	
	public Integer getActiveMemberCount(Integer roomId) {
		return chatRoomUserRepository.getActiveMemberCount(roomId);
	}

	@Transactional
	public ChatRoomUser updateRole(int userId, int roomId, ChatRoomUserRole role) {
		ChatRoomUser chatroomUser = chatRoomUserRepository.findById(new ChatRoomUserPK(userId, roomId)).get();
		chatroomUser.setRole(role);
		return chatRoomUserRepository.save(chatroomUser);
	}

	public ChatRoomUser newChatRoomUser(User user, ChatRoom chatRoom) {
		ChatRoomUser chatroomUser = chatRoomUserRepository
				.findById(new ChatRoomUserPK(user.getId(), chatRoom.getId()))
				.orElse(new ChatRoomUser(user, chatRoom));
	
		if (chatroomUser.getRole() != null && !chatroomUser.getRole().equals(ChatRoomUserRole.NONE)) {
			throw new RuntimeException("User is already in conversation!");
		}	
		chatroomUser.setRole(ChatRoomUserRole.USER);
		return chatRoomUserRepository.save(chatroomUser);
	}
	
	public List<ChatRoomUserDTO> loadChatRoomUsersById(int roomId) {	
		List<ChatRoomUserDTO> list = chatRoomUserRepository.findByChatRoomIdWithUserProfile(roomId);	
		list.stream().forEach(user -> user.setOnline(socketRegsitryService.isOnlineUser(user.getId().toString())));
		return list;
	}
	
	
	
	

}
