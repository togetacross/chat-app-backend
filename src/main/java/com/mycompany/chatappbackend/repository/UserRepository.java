package com.mycompany.chatappbackend.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mycompany.chatappbackend.model.dto.User.UserDTO;
import com.mycompany.chatappbackend.model.entity.Role;
import com.mycompany.chatappbackend.model.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

	Optional<User> findByName(String name);
	
	Optional<User> findByEmail(String email);
	
	Set<User> findByIdIn(List<Integer> ids);

	@Query("select u from User u LEFT JOIN FETCH u.userProfile up where u.id in :ids" )
	List<User> findByIdInWithProfile(@Param("ids") List<Integer> ids);
	
	@Query("select u from User u LEFT JOIN FETCH u.userProfile up where u.id = :userId" )
	Optional<User> findByIdWithProfile(@Param("userId") Integer userId);

	@Query("SELECT new com.mycompany.chatappbackend.model.dto.User.UserDTO(u.id, u.name, up.image) "
			+ "FROM User u "
			+ "LEFT JOIN u.userProfile up "
			+ "WHERE u.name LIKE %:name% AND u.id NOT IN :userIds")
	List<UserDTO> findByNameStartingWithAndNotIn(@Param("name") String name, @Param("userIds") List<Integer> userIds);
	
	@Query("SELECT new com.mycompany.chatappbackend.model.dto.User.UserDTO(u.id, u.name, up.image) "
			+ "FROM User u "
			+ "LEFT JOIN u.userProfile up "
			+ "WHERE u.name LIKE %:name%")
	List<UserDTO> findByNameStartingWith(@Param("name") String name);
	
	@Query("SELECT u.id FROM User u")
	List<Integer> findAllReference();
	
	@Modifying
	@Query("update User set role = :role where email = :email")
	void updateUserRole(@Param("email") String email, @Param("role") Role role);
}

/*
@Query("SELECT cu.key.userId FROM ChatRoom c LEFT JOIN c.chatRoomUsers cu WHERE cu.key.chatRoomId = :chatroomId AND cu.role <> 'NONE'")
List<Integer> findUserIdsByChatRoomId(@Param("chatroomId") Integer chatroomId);
 
 
@Query("SELECT cu.key.userId FROM ChatRoom c LEFT JOIN c.chatRoomUsers cu WHERE cu.key.chatRoomId = :chatroomId AND cu.role <> 'NONE' AND cu.key.userId <> :userId")
List<Integer> findUserIdsAndNotContainUserIdByChatRoomId(@Param("chatroomId") Integer chatroomId, @Param("userId") Integer userId);
 * 
@Query( "select up from UserProfile up WHERE up.id = :userId" )
UserProfile findUserProfileById(@Param("userId") Integer userId);
*/

/*
@Query("SELECT new com.mycompany.chatappbackend.model.dto.ChatRoom.DisplayChatRoomDTO("
		+ "c.id, "
		+ "CASE WHEN p.user.id =: userId THEN up.name ELSE p.name END, "
		+ "CASE WHEN p.user.id =: userId THEN up.image ELSE p.image END, "
		+ "c.type, "
		+ "CASE WHEN COUNT(m) > 0 THEN MAX(m.createdAt) ELSE c.createdAt END as lastMessage) "
		+ "FROM ChatRoomUser u "
		+ "LEFT JOIN u.chatRoom c "
		+ "LEFT JOIN c.conversationProfile p "
		+ "LEFT JOIN p.recieverUserProfile up "
		+ "LEFT JOIN c.messages AS m "
		+ "WHERE u.key.userId = :userId AND u.role <> 'NONE' AND (p.user.id IS NULL OR p.user.id =: userId)"
		+ "GROUP BY c.id "
		+ "ORDER BY lastMessage DESC")
List<DisplayChatRoomDTO> findUserWithChatRooms(@Param("userId") Integer id);
*/

/*
@Query("SELECT new com.mycompany.chatappbackend.model.dto.ChatRoom.DisplayChatRoomDTO("
		+ "c.id, "
		+ "p.name, "
		+ "p.image, "
		+ "c.type, "
		+ "CASE WHEN COUNT(m) > 0 THEN MAX(m.createdAt) ELSE c.createdAt END as lastMessage) "
		+ "FROM User u "
		+ "LEFT JOIN u.chatRooms c "
		+ "LEFT JOIN c.conversationProfile p "
		+ "LEFT JOIN c.users us "
		+ "LEFT JOIN us.userProfile usp "
		+ "LEFT JOIN c.messages AS m "
		+ "WHERE us.id <> :userId AND u.id = :userId "
		+ "GROUP BY c "
		+ "ORDER BY lastMessage DESC")
List<DisplayChatRoomDTO> findUserWithChatRooms(@Param("userId") Integer id);*/
//+ "CASE WHEN COUNT(c.users) <= 1 THEN us.name ELSE p.name END, "
//+ "CASE WHEN COUNT(c.users) <= 1 THEN usp.image ELSE p.image END, "
// Not return single user conversations
/*@Query("SELECT new com.mycompany.chatappbackend.model.dto.ChatRoom.DisplayChatRoomDTO("
			+ "c.id, "
			+ "CASE WHEN p.user.id =: userId THEN up.name ELSE p.name END, "
			+ "CASE WHEN p.user.id =: userId THEN up.image ELSE p.image END, "
			+ "c.type, "
			+ "CASE WHEN COUNT(m) > 0 THEN MAX(m.createdAt) ELSE c.createdAt END as lastMessage) "
			+ "FROM User u "
			+ "LEFT JOIN u.chatRooms c "
			+ "LEFT JOIN c.conversationProfile p "
			+ "LEFT JOIN p.recieverUserProfile up "
			+ "LEFT JOIN c.messages AS m "
			+ "WHERE u.id = :userId AND (p.user.id IS NULL OR p.user.id =: userId)"
			+ "GROUP BY c "
			+ "ORDER BY lastMessage DESC")
	List<DisplayChatRoomDTO> findUserWithChatRooms(@Param("userId") Integer id);*/