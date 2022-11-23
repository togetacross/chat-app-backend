package com.mycompany.chatappbackend.model.dto.Message;

import java.time.OffsetDateTime;
import java.util.Set;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.Singular;

@Getter
@Setter
@Builder
public class DisplayMessageDTO {

	private final Integer id;

	private final Integer userId;
	
	private final Integer roomId;

	private final String content;
	
	private String type;

	// @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "YY/MM/dd HH:mm:ss")
	private final OffsetDateTime createdAt;

	@Singular
	private final Set<AttachmentDTO> files;
	
	private final Set<MessageUserActivityDTO> userActivitys;

}
