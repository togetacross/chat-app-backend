package com.mycompany.chatappbackend.model.dto.User;

import com.mycompany.chatappbackend.model.dto.Message.DisplayMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserAndMessageDTO {

	private ChatRoomUserDTO user;
	private DisplayMessageDTO message;
}
