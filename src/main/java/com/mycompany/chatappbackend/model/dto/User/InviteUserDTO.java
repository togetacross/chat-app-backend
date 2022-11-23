package com.mycompany.chatappbackend.model.dto.User;

import lombok.Data;

@Data
public class InviteUserDTO {

	private Integer userId;
	
	private Integer chatRoomId;
}
