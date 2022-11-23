package com.mycompany.chatappbackend.model.dto.Message;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class MessageSliceDTO {

	private List<DisplayMessageDTO> messages;
	private boolean hasMore;
}
