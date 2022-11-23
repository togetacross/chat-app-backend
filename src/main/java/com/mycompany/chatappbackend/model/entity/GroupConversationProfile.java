package com.mycompany.chatappbackend.model.entity;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Lob;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@DiscriminatorValue("1")
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@NoArgsConstructor
public class GroupConversationProfile extends ConversationProfile {

	@Column(name = "name")
	private String name;
	
	@Lob
	@Column(name = "image")
	private byte[] image;
	
	public GroupConversationProfile(String name, byte[] image) {
		super();
		this.name = name;
		this.image = image;
	}
}
