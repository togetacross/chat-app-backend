package com.mycompany.chatappbackend.model.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("2")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
public class PrivateConversationProfile extends ConversationProfile {
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "reciever_user_id")
	private UserProfile recieverUserProfile;
	
	public PrivateConversationProfile(UserProfile userProfile, User user) {
		super();
		this.recieverUserProfile = userProfile;
		setUser(user);
	}
}
