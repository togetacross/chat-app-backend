package com.mycompany.chatappbackend.repository;

import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mycompany.chatappbackend.model.entity.Message;

@Repository
public interface MessageRepository extends PagingAndSortingRepository<Message, Integer> {

	@Query("SELECT m.id FROM Message m "
			+ "WHERE m.chatRoom.id = :chatRoomId AND m.createdAt < :createdAt "
			+ "ORDER BY m.createdAt DESC")
	Slice<Integer> findByChatRoomIdByCreatedAtBefore(@Param("chatRoomId") Integer chatRoomId, @Param("createdAt") OffsetDateTime createdAt, Pageable pageable);
	
	@Query("SELECT m.id FROM Message m "
			+ "WHERE m.chatRoom.id = :chatRoomId "
			+ "ORDER BY m.createdAt DESC")
	Slice<Integer> findTop10IdByChatRoomIdOrderByCreatedAtDesc(@Param("chatRoomId") Integer chatRoomId, Pageable pageable);
	
	@Query("SELECT DISTINCT m FROM Message m "
			+ "LEFT JOIN FETCH m.messageContent c "
			+ "LEFT JOIN FETCH c.attachments a "
			+ "LEFT JOIN FETCH m.messageUserActivitys ma "
			+ "WHERE m.id IN :ids "
			+ "ORDER BY m.createdAt DESC")
	List<Message> findByIdInOrderByCreatedAtdDesc(@Param("ids") List<Integer> ids);
}
