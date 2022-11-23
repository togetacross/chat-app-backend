package com.mycompany.chatappbackend.model.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MESSAGE_CONTENT")
@Getter
@Setter
@NoArgsConstructor
public class MessageContent {

	@Id
	@Column(name = "message_id")
	private Integer id;
	
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "message_id")
	private Message message;
	
	@Lob
	@Column(name = "content")
	private String content;
	
	@OneToMany(mappedBy="messageContent", cascade=CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private Set<Attachment> attachments = new HashSet<>();
	
	public void addAttachment(Attachment attachment) {
		attachments.add(attachment);
		attachment.setMessageContent(this);;
	}
}
