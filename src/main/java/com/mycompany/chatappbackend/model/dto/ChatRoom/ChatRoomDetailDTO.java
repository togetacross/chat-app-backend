package com.mycompany.chatappbackend.model.dto.ChatRoom;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ChatRoomDetailDTO {

	private Integer id;
	private String name;
	private Object type;
	private OffsetDateTime lastActivity;
	private Integer chatRoomUserCount;
	private Integer messageCount;
}
