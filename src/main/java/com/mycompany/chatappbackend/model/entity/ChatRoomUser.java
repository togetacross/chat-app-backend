package com.mycompany.chatappbackend.model.entity;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CHATROOM_CHATROOM_USER")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatRoomUser {

	@EmbeddedId
	private ChatRoomUserPK key = new ChatRoomUserPK();

	@Enumerated(EnumType.STRING)
	@Column(name = "role")
	private ChatRoomUserRole role;

	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@MapsId("userId")
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@MapsId("chatRoomId")
	@JoinColumn(name = "chatroom_id", insertable = false, updatable = false)
	private ChatRoom chatRoom;

	@OneToMany(mappedBy = "chatRoomUser", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private Set<Message> messages = new HashSet<>();
	
	public ChatRoomUser(User user, ChatRoom chatRoom, ChatRoomUserRole role) {
		this.user = user;
		this.chatRoom = chatRoom;
		this.role = role;
	}
	
	public ChatRoomUser(User user, ChatRoom chatRoom) {
		this.user = user;
		this.chatRoom = chatRoom;
	}
	
	public ChatRoomUser(User user, ChatRoomUserRole role) {
		this.user = user;
		this.role = role;
	}
	
	public void addMessage(Message message) {
		message.setChatRoomUser(this);
		this.messages.add(message);
	}
	
}