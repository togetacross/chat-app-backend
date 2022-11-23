package com.mycompany.chatappbackend.model.dto.ChatRoom;

import java.util.List;
import com.mycompany.chatappbackend.model.dto.Message.MessageSliceDTO;
import com.mycompany.chatappbackend.model.dto.User.ChatRoomUserDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class InitConversationDTO {

	private List<ChatRoomUserDTO> users;
	private MessageSliceDTO messageSlice;
}
