package com.mycompany.chatappbackend.model.entity;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MESSAGE")
@Getter
@Setter
@NoArgsConstructor
public class Message {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "created_at")
	@CreationTimestamp
	private OffsetDateTime createdAt;
	
	@Enumerated(EnumType.STRING)
	private MessageType messageType;
	
	@OneToOne(mappedBy = "message", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private MessageContent messageContent;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns(
			{ @JoinColumn(name = "user_id", referencedColumnName = "user_id"),
			@JoinColumn(name = "chatroom_id", referencedColumnName = "chatroom_id")})
	private ChatRoomUser chatRoomUser;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "chatroom_id", referencedColumnName = "id", insertable = false, updatable = false)
	private ChatRoom chatRoom;
	
	@OneToMany(mappedBy = "message", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
	private final Set<MessageUserActivity> messageUserActivitys = new HashSet<>();

	public Message(MessageType type) {
		this.messageType = type;
	}
	
	public Message(MessageType type, ChatRoomUser chatRoomUser) {
		this.messageType = type;
		this.chatRoomUser = chatRoomUser;
	}
	
	public void setMessageContent(MessageContent messageContent) {
		this.messageContent = messageContent;
		messageContent.setMessage(this);
	}
	
	public void addUserActivity(MessageUserActivity messageUserActivity) {
		messageUserActivitys.add(messageUserActivity);
		messageUserActivity.setMessage(this);
	}
	
}
