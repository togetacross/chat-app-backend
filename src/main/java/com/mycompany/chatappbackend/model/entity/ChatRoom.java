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
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "CHATROOM")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ChatRoom {

	@Id
	@EqualsAndHashCode.Include
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "created_at")
	@CreationTimestamp
	private OffsetDateTime createdAt;
	
	@Column(name = "type")
	@Enumerated(EnumType.STRING)
	private ChatRoomType type;
	
	@OneToOne(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private ConversationProfile conversationProfile;

	@OneToMany(mappedBy = "chatRoom", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<ChatRoomUser> chatRoomUsers = new HashSet<>();
	
	@OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
	private Set<Message> messages = new HashSet<>();

	public ChatRoom (ChatRoomType type, ConversationProfile conversationProfile) {
		this.type = type;
		conversationProfile.setChatRoom(this);
		this.setConversationProfile(conversationProfile);
	}
	
	public void addMessage(Message message, ChatRoomUser chatRoomUser) {
		this.messages.add(message);
		message.setChatRoomUser(chatRoomUser);
	}

	public void addChatRoomUser(ChatRoomUser chatRoomUser) {
		this.chatRoomUsers.add(chatRoomUser);
		chatRoomUser.setChatRoom(this);;
	}
	
	public void removeChatRoomUser(ChatRoomUser chatRoomUser) {
		chatRoomUsers.remove(chatRoomUser);
	}

}