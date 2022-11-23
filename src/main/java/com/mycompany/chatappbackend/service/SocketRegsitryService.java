package com.mycompany.chatappbackend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.user.SimpSubscription;
import org.springframework.messaging.simp.user.SimpSubscriptionMatcher;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

@Service
public class SocketRegsitryService {

	@Autowired
	private SimpUserRegistry simpUserRegistry;
	
	public boolean isOnlineUser(String userName) {
		return simpUserRegistry.getUser(userName) != null;
	}
	
	public boolean hasOpenedTopic(Integer conversationId) {
		return simpUserRegistry.getUsers()
				.stream()
				.anyMatch(simpUser -> 
				{
					return simpUserRegistry.findSubscriptions(new SimpSubscriptionMatcher() {
					@Override
					public boolean match(SimpSubscription subscription) {        	
						return subscription.getDestination().endsWith("/private/" + conversationId);
					}
				    }).size() == 1;
				}
		);
	}
}
