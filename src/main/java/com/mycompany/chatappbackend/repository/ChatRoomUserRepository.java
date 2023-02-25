package com.mycompany.chatappbackend.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mycompany.chatappbackend.model.dto.User.ChatRoomUserDTO;
import com.mycompany.chatappbackend.model.entity.ChatRoomUser;
import com.mycompany.chatappbackend.model.entity.ChatRoomUserPK;

@Repository
public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, ChatRoomUserPK> {

	Set<ChatRoomUser> findByChatRoomId(Integer chatRoomId);
	
	@Query("SELECT new com.mycompany.chatappbackend.model.dto.User.ChatRoomUserDTO(cu.key.userId, u.name, cu.role, up.image) "		
			+ "FROM ChatRoomUser cu LEFT JOIN cu.user u LEFT JOIN u.userProfile up "		
			+ "WHERE cu.key.chatRoomId = :chatRoomId")
	List<ChatRoomUserDTO> findByChatRoomIdWithUserProfile(@Param("chatRoomId") Integer chatRoomId);
	
	@Query("SELECT new com.mycompany.chatappbackend.model.dto.User.ChatRoomUserDTO(cu.key.userId, u.name, cu.role, up.image) "		
			+ "FROM ChatRoomUser cu LEFT JOIN cu.user u LEFT JOIN u.userProfile up "		
			+ "WHERE cu.key.chatRoomId = :chatRoomId AND cu.key.userId = :userId")
	ChatRoomUserDTO findByIdWithProfile(@Param("chatRoomId") Integer chatRoomId, @Param("userId") Integer userId);
	
	@Query("SELECT up.name FROM ChatRoomUser cu LEFT JOIN cu.user u LEFT JOIN u.userProfile up "		
			+ "WHERE cu.key.chatRoomId = :chatRoomId AND cu.role <> 'NONE'")
	List<String> findNameByChatRoomIdAndRoleIsNotNone(@Param("chatRoomId") Integer chatRoomId);
	
	@Query("SELECT COUNT(cu.key.userId) FROM ChatRoomUser cu WHERE cu.key.chatRoomId = :chatRoomId AND cu.role <> 'NONE'")
	Integer getActiveMemberCount(@Param("chatRoomId") Integer chatRoomId);
	
	@Query("SELECT cu.key.userId FROM ChatRoomUser cu WHERE cu.key.chatRoomId = :chatRoomId AND cu.role <> 'NONE'")
	List<Integer> getActiveUserIdsByChatRoomId(@Param("chatRoomId") Integer chatRoomId);

}