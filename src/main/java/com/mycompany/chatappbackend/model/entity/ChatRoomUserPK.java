package com.mycompany.chatappbackend.model.entity;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@EqualsAndHashCode
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ChatRoomUserPK implements Serializable {

	private static final long serialVersionUID = 3000325917895872227L;
	
	@Column(name = "user_id", nullable = false)
	private Integer userId;
	
	@Column(name = "chatroom_id",  nullable = false)
	private Integer chatRoomId;

}
