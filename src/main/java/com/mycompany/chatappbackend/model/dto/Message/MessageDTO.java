package com.mycompany.chatappbackend.model.dto.Message;

import java.time.OffsetDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MessageDTO {
	
	@NotNull
	private Integer chatRoomId;
	
	@NotBlank(message = "The content field is required!")
	private String content;
	
	// @DateTimeFormat(iso = ISO.DATE_TIME)
	private OffsetDateTime dateTime;
	
}
