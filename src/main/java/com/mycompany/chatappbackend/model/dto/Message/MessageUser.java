package com.mycompany.chatappbackend.model.dto.Message;

import lombok.Data;

@Data
public class MessageUser {

	private Integer id;
	private String userName;
	private byte [] image;
}
