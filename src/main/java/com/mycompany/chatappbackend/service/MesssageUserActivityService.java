package com.mycompany.chatappbackend.service;

import java.time.OffsetDateTime;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.mycompany.chatappbackend.model.entity.Message;
import com.mycompany.chatappbackend.model.entity.MessageActivityUserKey;
import com.mycompany.chatappbackend.model.entity.MessageUserActivity;
import com.mycompany.chatappbackend.model.entity.User;
import com.mycompany.chatappbackend.repository.MesageUserActivityRepository;

@Service
public class MesssageUserActivityService {
	
	@Autowired
	private MesageUserActivityRepository messageUserActivityRepository;
	
	@Transactional
	public void seenMessage(Message message, User user, OffsetDateTime dateTime) {
		MessageUserActivity messageUserActivity = messageUserActivityRepository
				.findById(new MessageActivityUserKey(user.getId(), message.getId()))
				.orElse(new MessageUserActivity(user, message));
		
		if(messageUserActivity.getSeenAt() == null) {
			messageUserActivity.setSeenAt(dateTime);
		}
		messageUserActivityRepository.save(messageUserActivity);
	}
	
	@Transactional
	public void proccessLike(Message message, User user, boolean isLiked) {
		MessageUserActivity messageUserActivity = messageUserActivityRepository
				.findById(new MessageActivityUserKey(user.getId(), message.getId()))
				.orElse(new MessageUserActivity(user, message));
		messageUserActivity.setLiked(isLiked);
		messageUserActivityRepository.save(messageUserActivity);
	}
	
}
