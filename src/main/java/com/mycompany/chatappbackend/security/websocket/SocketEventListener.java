package com.mycompany.chatappbackend.security.websocket;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationResponse;
import com.mycompany.chatappbackend.model.dto.Notification.NotificationType;
import com.mycompany.chatappbackend.security.UserPrinciple;
import com.mycompany.chatappbackend.service.NotificationService;

@Component
public class SocketEventListener {

	@Autowired
	private NotificationService notificationService;
	
    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) { 
    	sendNotify(NotificationType.CONNECT, event.getUser());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
    	sendNotify(NotificationType.DISCONNECT, event.getUser());
    }
    
    private void sendNotify(NotificationType type, Principal principal) {
    	Integer userId = getAuthUserId(principal);
    	notificationService.sendToAll(new NotificationResponse(userId, type));
    }
    
    private Integer getAuthUserId(Principal principal) {
    	Authentication auth = (Authentication) principal;
        UserPrinciple userPrinciple = (UserPrinciple) auth.getPrincipal();
        return userPrinciple.getId();
    }
    
}
