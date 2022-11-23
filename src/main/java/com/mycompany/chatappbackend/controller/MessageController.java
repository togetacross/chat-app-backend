package com.mycompany.chatappbackend.controller;

import java.time.OffsetDateTime;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.mycompany.chatappbackend.model.dto.Message.MessageDTO;
import com.mycompany.chatappbackend.security.UserPrinciple;
import com.mycompany.chatappbackend.service.MessageService;

@RestController
@RequestMapping("/chatapp/conversation/messages")
public class MessageController {
	
	@Autowired
	private MessageService messageService;

	
	@PostMapping("/new")
	public ResponseEntity<?> proccessNewMessage(
			@RequestPart(name = "files", required = false) MultipartFile[] files, 
			@Valid @RequestPart(name = "messageDetails", required = false) MessageDTO messageDTO,
			@AuthenticationPrincipal UserPrinciple principle) 
	{		
		return ResponseEntity.ok().build();
	}
	
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
