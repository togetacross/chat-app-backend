package com.mycompany.chatappbackend.model.dto.Message;

import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageLikeDTO {

	private Integer roomId;
	private Integer userId;
	private Integer messageId;
	private boolean liked;
}
