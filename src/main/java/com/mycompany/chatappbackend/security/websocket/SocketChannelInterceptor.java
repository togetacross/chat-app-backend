package com.mycompany.chatappbackend.security.websocket;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import com.mycompany.chatappbackend.security.jwt.JwtProvider;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class SocketChannelInterceptor implements WebSocketMessageBrokerConfigurer {

	@Autowired
	private JwtProvider jwtProvider;

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.interceptors(new ChannelInterceptor() {
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
				if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
					List<String> authorization = accessor.getNativeHeader("authorization");
					String accessToken = authorization.get(0).split(" ")[1];
					Authentication authentication = jwtProvider.getSocketAuthentication(accessToken);
				
					if(authentication == null) {
						System.out.println("Not Authenticated");
						throw new AccessDeniedException("Unauthorized");	
					} 
					accessor.setUser(authentication);
				}
				
				return new GenericMessage<>(message.getPayload(), accessor.getMessageHeaders());
			}
		});
	}
	 

}