package com.mycompany.chatappbackend.model.dto.ChatRoom;

import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NewChatRoomDTO {

	@NotBlank(message = "The name field is required!")
	@Size(min = 3, max = 40, message = "Name field must be 3-40 chars!")
	private String name;
	
	private List<Integer> userIds;	
}
