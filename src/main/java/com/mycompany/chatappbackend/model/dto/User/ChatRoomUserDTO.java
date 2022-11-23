package com.mycompany.chatappbackend.model.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatRoomUserDTO {

	private Integer id;
	private String name;
	private Object role;
	private byte[] image;
	private boolean isOnline;
	
	public ChatRoomUserDTO(Integer id, String name, Object role, byte[] image) {
		this.id = id;
		this.name = name;
		this.role = role;
		this.image = image;
	} 
	
 }
