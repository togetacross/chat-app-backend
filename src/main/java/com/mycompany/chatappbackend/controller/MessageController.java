package com.mycompany.chatappbackend.controller;

import java.time.OffsetDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.mycompany.chatappbackend.security.UserPrinciple;
import com.mycompany.chatappbackend.service.MessageService;

@RestController
@RequestMapping("/chatapp/conversation/messages")
public class MessageController {
	
	@Autowired
	private MessageService messageService;

	@PreAuthorize("@convesrationRoleService.hasRole(#userPrinciple.getId, #chatRoomId, 'USER')")
	@GetMapping("/paginate")
	public ResponseEntity<?> paginateMessagesByChatRoomId(
			@RequestParam(name = "chatRoomId") Integer chatRoomId,
			@RequestParam(name = "dateTime") OffsetDateTime offsetDateTime,
			@AuthenticationPrincipal UserPrinciple userPrinciple
			) 
	{	
		return new ResponseEntity<>(messageService.getPaginateMessages(chatRoomId, offsetDateTime) , HttpStatus.OK);
	}
	

}
