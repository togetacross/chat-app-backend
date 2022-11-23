package com.mycompany.chatappbackend.model.entity;

import java.time.OffsetDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "MESSAGE_USER_ACTIVITY")
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class MessageUserActivity {

	@EmbeddedId
	private MessageActivityUserKey messageActivityUserKey = new MessageActivityUserKey();
	
	@ManyToOne(optional = false, cascade = CascadeType.MERGE)
	@MapsId("userId")
	@JoinColumn(name = "user_id", insertable = false, updatable = false)
	private User user;

	@ManyToOne(optional = false, fetch = FetchType.LAZY, cascade = CascadeType.MERGE)
	@MapsId("messageId")
	@JoinColumn(name = "message_id", insertable = false, updatable = false)
	private Message message;
	
	@Column(name = "seen_at")
	private OffsetDateTime seenAt;
	
	private boolean liked;

	public MessageUserActivity(User user, Message message) {
		this.user = user;
		this.message = message;
	}
	
	public MessageUserActivity(User user, OffsetDateTime seenAt) {
		super();
		this.user = user;
		this.seenAt = seenAt;
	}

	public MessageUserActivity(User user, Message message, OffsetDateTime seenAt) {
		super();
		this.user = user;
		this.message = message;
		this.seenAt = seenAt;
	}

}
