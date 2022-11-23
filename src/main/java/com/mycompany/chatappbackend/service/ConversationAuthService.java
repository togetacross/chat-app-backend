package com.mycompany.chatappbackend.service;

import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mycompany.chatappbackend.model.entity.ChatRoomUser;
import com.mycompany.chatappbackend.model.entity.ChatRoomUserRole;

@Service("convesrationRoleService")
public class ConversationAuthService {

	@Autowired
	private ChatRoomUserService chatRoomUserService;
	
	private static final Map<String, Integer> roleHierarchy = Map.of(
				ChatRoomUserRole.ADMIN.name(), 3,
				ChatRoomUserRole.USER.name(), 2,
				ChatRoomUserRole.NONE.name(), 1
			);
	
	public boolean hasRole(Integer userId, Integer convesrationId, String role) {
		Optional<ChatRoomUser> chatRoomUser = chatRoomUserService.getChatRoomUserById(userId, convesrationId);
		return chatRoomUser.isPresent() ? hasPermission(role, chatRoomUser.get().getRole()) : false;
	}
	
	private boolean hasPermission(String role, ChatRoomUserRole userRole) {
		return roleHierarchy.get(userRole.name()) >= roleHierarchy.get(role);
	}
	
}
