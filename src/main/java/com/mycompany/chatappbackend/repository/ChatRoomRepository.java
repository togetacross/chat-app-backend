package com.mycompany.chatappbackend.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mycompany.chatappbackend.model.dto.ChatRoom.DisplayChatRoomDTO;
import com.mycompany.chatappbackend.model.entity.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
	
	@Query("SELECT c FROM ChatRoom c LEFT JOIN FETCH c.chatRoomUsers cu WHERE cu.key.chatRoomId = :chatroomId AND cu.role = 'NONE'")
	ChatRoom findByIdWithUserAndChatRoomUser(@Param("chatroomId") Integer chatroomId);
	
	@Query("SELECT CASE WHEN COUNT(cp) > 0 THEN TRUE ELSE FALSE END FROM PrivateConversationProfile cp "
			+ "WHERE (cp.recieverUserProfile.id = :userIdOne AND cp.user.id = :userIdTwo) "
			+ "OR  (cp.recieverUserProfile.id = :userIdTwo AND cp.user.id = :userIdOne)")
	boolean existsByUsersInPrivateProfile(@Param("userIdOne") Integer userIdOne, @Param("userIdTwo") Integer userIdTwo);
	
	@Query("SELECT c FROM ChatRoom c LEFT JOIN FETCH c.conversationProfile p "
			+ "WHERE c.id = :chatroomId AND c.type = 'GROUP'")
	Optional<ChatRoom> findGroupChatRoomByIdWithProfile(@Param("chatroomId") Integer chatroomId);
	
	@Query("SELECT new com.mycompany.chatappbackend.model.dto.ChatRoom.DisplayChatRoomDTO("
			+ "u.key.chatRoomId as chatRoomId, "
			+ "CASE WHEN p.user.id =: userId THEN up.name ELSE p.name END, "
			+ "CASE WHEN p.user.id =: userId THEN up.image ELSE p.image END, "
			+ "c.type, "
			//+ "CASE WHEN COUNT(m) > 0 THEN MAX(m.createdAt) ELSE c.createdAt END as lastMessage) "
			//+ "MAX(m.createdAt) as lastMessage) "
			+ "m.createdAt as lastMessage) "
			+ "FROM ChatRoomUser u "
			+ "LEFT JOIN u.chatRoom c "
			+ "LEFT JOIN c.conversationProfile p "
			+ "LEFT JOIN p.recieverUserProfile up "
			//+ "LEFT JOIN c.messages AS m "
			+ "WHERE u.key.userId = :userId AND u.role <> 'NONE' AND (p.user.id IS NULL OR p.user.id =: userId) "
			//+ "GROUP BY chatRoomId "
			+ "ORDER BY lastMessage DESC")
	List<DisplayChatRoomDTO> findByUserId(@Param("userId") Integer id);

}
