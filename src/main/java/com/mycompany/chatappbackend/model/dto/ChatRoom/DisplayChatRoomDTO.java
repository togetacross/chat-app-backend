package com.mycompany.chatappbackend.model.dto.ChatRoom;

import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class DisplayChatRoomDTO {

	private Integer id;
	private String name;
	private byte[] image;
	private Object type;
 	private OffsetDateTime createdAt;
 	//private boolean hasNewMessage;
 	
}
