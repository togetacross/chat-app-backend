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
import com.mycompany.chatappbackend.model.dto.User.UserDetailsDTO;
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
	
	@Query("SELECT new com.mycompany.chatappbackend.model.dto.User.UserDetailsDTO(u.id, u.name, u.email, up.image, Count(cu)) "
			+ "FROM User u "
			+ "LEFT JOIN u.userProfile up "
			+ "LEFT JOIN u.chatRoomUsers cu "
			+ "WHERE u.id = :userId AND cu.role <> 'NONE'")
	UserDetailsDTO findByIdWithDetails(@Param("userId") Integer userId);
	
	@Query("SELECT new com.mycompany.chatappbackend.model.dto.User.UserDTO(u.id, u.name, up.image) "
			+ "FROM User u LEFT JOIN u.userProfile up")
	List<UserDTO> findAllWithProfile();
	
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
