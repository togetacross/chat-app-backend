package com.mycompany.chatappbackend.model.dto.Message;

import com.mycompany.chatappbackend.model.entity.AttachmentType;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AttachmentDTO {

	private Integer id;
	private String name;
	private byte[] file;
	private AttachmentType attachmentType;
}
