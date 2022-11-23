package com.mycompany.chatappbackend.model.dto.User;

import lombok.Value;

@Value
public class UserDTO {

	private Integer id;
	private String name;
	private byte[] image;
}
