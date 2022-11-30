package com.mycompany.chatappbackend.security.websocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;
import io.jsonwebtoken.ExpiredJwtException;

@Configuration
public class StompErrorHandlerConfig {

	@Bean
	public StompSubProtocolErrorHandler customSocketErrorHandler() {
		return new StompSubProtocolErrorHandler() {
			@Override
			public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {
				StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
				
				if (ex.getCause() instanceof AccessDeniedException || ex.getCause() instanceof ExpiredJwtException )
				    {
				
					accessor.setMessage("403 Access Denied");
					accessor.setLeaveMutable(true);
				    return createErrorMessage(accessor, clientMessage.getPayload(), ex, accessor);
				    
				    }
				
				 return handleClientMessageProcessingError(clientMessage, ex);
			}

			@Override
			public Message<byte[]> handleErrorMessageToClient(Message<byte[]> errorMessage) {
				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(errorMessage, StompHeaderAccessor.class);
				
				if (!accessor.isMutable()) {
					accessor = StompHeaderAccessor.wrap(errorMessage);
				}
				
				return createErrorMessage(accessor, errorMessage.getPayload(), null, null);
			}
			
			protected Message<byte[]> createErrorMessage(
					StompHeaderAccessor errorHeaderAccessor, 
					byte[] errorPayload,
					Throwable cause, 
					StompHeaderAccessor clientHeaderAccessor) 
			{
				return MessageBuilder.createMessage(errorPayload, errorHeaderAccessor.getMessageHeaders());
			}
			
		};
	}

}
