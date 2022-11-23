package com.mycompany.chatappbackend.model.dto.Message;

import java.time.OffsetDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MessageUserActivityDTO {

	private Integer userId;
	private OffsetDateTime seenAt;
	private boolean liked;
}
