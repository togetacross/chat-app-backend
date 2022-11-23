package com.mycompany.chatappbackend.model.entity;

import java.util.ArrayList;
import java.util.List;

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
@Table(name = "USER_PROFILE")
@Getter
@Setter
@NoArgsConstructor
public class UserProfile {
	
	@Id
	private Integer id;

	@Lob
	@Column(name = "image")
	private byte[] image;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	// hangulat
	
	@OneToOne(fetch = FetchType.LAZY)
	@MapsId
	@JoinColumn(name = "user_id", referencedColumnName = "id")
	private User user;
	
	@OneToMany(mappedBy = "recieverUserProfile", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private List<PrivateConversationProfile> conversationProfile = new ArrayList<>();
	
	public UserProfile(User user) {
		this.user = user;
	}
	
}
