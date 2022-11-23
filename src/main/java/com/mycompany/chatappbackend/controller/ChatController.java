package com.mycompany.chatappbackend.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.mycompany.chatappbackend.model.dto.ChatRoom.NewChatRoomDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageDTO;
import com.mycompany.chatappbackend.model.dto.User.InviteUserDTO;
import com.mycompany.chatappbackend.model.dto.User.IdentifierDTO;
import com.mycompany.chatappbackend.security.UserPrinciple;
import com.mycompany.chatappbackend.service.ChatRoomService;

@RestController
@RequestMapping("/chatapp/conversation")
public class ChatController {
	
	@Autowired
	private ChatRoomService chatRoomService;
	
	@PostMapping("/group")
	public ResponseEntity<?> createGroupConversation(
			@RequestPart(name = "file", required = false) MultipartFile file,
			@Valid @RequestPart("room") NewChatRoomDTO newChatRoomDTO, 
			@AuthenticationPrincipal UserPrinciple userPrinciple) 
	{	
		chatRoomService.createGroupConversation(file, newChatRoomDTO, userPrinciple.getId());
		return new ResponseEntity<>(HttpStatus.CREATED);
	}
	
	@PostMapping("/private")
	public ResponseEntity<?> createPrivateConversation(
			@RequestBody IdentifierDTO identifierDTO, 
			@AuthenticationPrincipal UserPrinciple userPrinciple) 
	{	
		chatRoomService.createPrivateConversation(userPrinciple.getId(), identifierDTO.getId());
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@PreAuthorize("@convesrationRoleService.hasRole(#principle.getId, #inviteUserDTO.chatRoomId, 'ADMIN')")
	@PostMapping("/add")
	public ResponseEntity<?> addUser(@RequestBody InviteUserDTO inviteUserDTO,
			@AuthenticationPrincipal UserPrinciple principle) 
	{
		chatRoomService.addUserToConversation(inviteUserDTO.getChatRoomId(), inviteUserDTO.getUserId());
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PostMapping("/leave")
	public ResponseEntity<?> leave(
			@RequestBody IdentifierDTO roomIdDTO,
			@AuthenticationPrincipal UserPrinciple principle) 
	{
		chatRoomService.leaveUserFromConversation(roomIdDTO.getId(), principle.getId());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@PreAuthorize("@convesrationRoleService.hasRole(#principle.getId, #messageDTO.chatRoomId, 'USER')")
	@PostMapping("/message")
	public ResponseEntity<?> proccessNewMessage(
			@RequestPart(name = "files", required = false) MultipartFile[] files, 
			@Valid @RequestPart(name = "messageDetails", required = false) MessageDTO messageDTO,
			@AuthenticationPrincipal UserPrinciple principle) 
	{	
		chatRoomService.processNewMessage(files, messageDTO, principle.getId());
		return ResponseEntity.ok().build();
	}
	
	
	@GetMapping("/load")
	public ResponseEntity<?> initConversation(
			@RequestParam(name = "chatRoomId") Integer chatRoomId, 
			@AuthenticationPrincipal UserPrinciple userPrinciple) 
	{		
		return new ResponseEntity<>(chatRoomService.initConversation(chatRoomId), HttpStatus.OK);
	}
	
	
	@GetMapping("/all")
	public ResponseEntity<?> getChatRoomsByUserId(@AuthenticationPrincipal UserPrinciple userPrinciple) {
		return new ResponseEntity<>(chatRoomService.getUserConversations(userPrinciple.getId()), HttpStatus.OK);
	}

}
