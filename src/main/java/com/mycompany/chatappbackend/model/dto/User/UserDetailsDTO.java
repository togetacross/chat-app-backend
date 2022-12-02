package com.mycompany.chatappbackend.model.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDetailsDTO {

	private Integer id;
	private String name;
	private String email;
	private byte[] image;
	private long conversationCount;
}
