package com.mycompany.chatappbackend.model.dto.Message;

import java.time.OffsetDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MessageSeenDTO {

	private List<Integer> messageIds;
	private Integer chatroomId;
	private OffsetDateTime seenAt;
}
