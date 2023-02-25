package com.mycompany.chatappbackend.repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.mycompany.chatappbackend.model.entity.ChatRoom;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Integer> {
	
	@Query("SELECT CASE WHEN COUNT(cp) > 0 THEN TRUE ELSE FALSE END FROM PrivateConversationProfile cp "
			+ "WHERE (cp.recieverUserProfile.id = :userIdOne AND cp.senderUserProfile.id = :userIdTwo) "
			+ "OR (cp.recieverUserProfile.id = :userIdTwo AND cp.senderUserProfile.id = :userIdOne)")
	boolean existsByUsersInPrivateProfile(@Param("userIdOne") Integer userIdOne, @Param("userIdTwo") Integer userIdTwo);
	
	@Query("SELECT c FROM ChatRoom c LEFT JOIN FETCH c.conversationProfile p "
			+ "WHERE c.id = :chatroomId AND c.type = 'GROUP'")
	Optional<ChatRoom> findGroupChatRoomByIdWithProfile(@Param("chatroomId") Integer chatroomId);
	
	
	@Query("SELECT c "
			+ "FROM ChatRoomUser u "
			+ "LEFT JOIN u.chatRoom c "
			+ "LEFT JOIN c.conversationProfile p "
			+ "WHERE u.key.userId = :userId AND u.role <> 'NONE' "
			+ "ORDER BY c.createdAt DESC")
	List<ChatRoom> findByUserId(@Param("userId") Integer id);
	
	@Modifying
	@Query("update ChatRoom ch set ch.createdAt = :dateTime where ch.id = :id")
	void updateChatRoomLastActivity(@Param("dateTime") OffsetDateTime dateTime, @Param("id") Integer id);

}

/*@Query("SELECT new com.mycompany.chatappbackend.model.dto.ChatRoom.DisplayChatRoomDTO("
			+ "u.key.chatRoomId as chatRoomId, "
			+ "CASE WHEN p.user.id =: userId THEN up.name ELSE p.name END, "
			+ "CASE WHEN p.user.id =: userId THEN up.image ELSE p.image END, "
			+ "c.type, "
			+ "c.createdAt as lastMessage) "
			//+ "CASE WHEN COUNT(m) > 0 THEN MAX(m.createdAt) ELSE c.createdAt END as lastMessage) "
			+ "FROM ChatRoomUser u "
			+ "LEFT JOIN u.chatRoom c "
			+ "LEFT JOIN c.conversationProfile p "
			+ "LEFT JOIN p.recieverUserProfile up "
			//+ "LEFT JOIN c.messages AS m "
			+ "WHERE u.key.userId = :userId AND u.role <> 'NONE' AND (p.user.id IS NULL OR p.user.id =: userId) "
			//+ "GROUP BY chatRoomId "
			+ "ORDER BY lastMessage DESC")
	List<DisplayChatRoomDTO> findByUserId(@Param("userId") Integer id);*/

/*@Query("SELECT new com.mycompany.chatappbackend.model.dto.ChatRoom.DisplayChatRoomDTO("
			+ "c.id, "
			+ "CASE WHEN TYPE(p) = 1 THEN p.name WHEN u.recieverUserProfile.id = :userId THEN p.senderUserProfile.name ELSE p.recieverUserProfile.name END, "
			+ "CASE WHEN TYPE(p) = 1 THEN p.image WHEN p.recieverUserProfile.id = :userId THEN p.senderUserProfile.image ELSE p.recieverUserProfile.image END, "
			+ "c.type, "
			+ "c.createdAt as lastMessage) "
			+ "FROM ChatRoomUser u "
			+ "LEFT JOIN u.chatRoom c "
			+ "LEFT JOIN c.conversationProfile p "
			+ "WHERE u.key.userId = :userId AND u.role <> 'NONE' "
			+ "ORDER BY lastMessage DESC")
	List<DisplayChatRoomDTO> findByUserId(@Param("userId") Integer id);*/