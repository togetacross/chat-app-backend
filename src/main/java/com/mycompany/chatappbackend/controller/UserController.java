package com.mycompany.chatappbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.mycompany.chatappbackend.exception.ResourceNotFoundException;
import com.mycompany.chatappbackend.security.UserPrinciple;
import com.mycompany.chatappbackend.service.ChatRoomUserService;
import com.mycompany.chatappbackend.service.UserService;

@RestController
@RequestMapping("/chatapp/")
public class UserController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private ChatRoomUserService chatRoomUserService;
	
	@GetMapping("/users/search")
	public ResponseEntity<?> loadUsersByNamePartAndNotInConversation(
			@RequestParam(name = "namePart") String prefix,
			@RequestParam(name = "roomId") Integer roomId) throws ResourceNotFoundException 
	{	
		List<Integer> activeUserIds = chatRoomUserService.loadActiveChatRoomUserIds(roomId);
		return new ResponseEntity<>(userService.getByNameStartsWithAndNotIn(prefix, activeUserIds), HttpStatus.OK);
	}
	
	@GetMapping("/users/search/all")
	public ResponseEntity<?> loadUsersByNamePart(@RequestParam(name = "namePart") String prefix) throws ResourceNotFoundException 
	{	
		return new ResponseEntity<>(userService.getByNameStartsWith(prefix), HttpStatus.OK);
	}
	
	@PutMapping("/users/profile/update")
	public ResponseEntity<?> updateProfile(
			@RequestPart(name = "image") MultipartFile multipartFile, 
			@AuthenticationPrincipal UserPrinciple userPrinciple) 
	{
		return new ResponseEntity<>(userService.updateProfileImage(multipartFile, userPrinciple.getId()), HttpStatus.OK);
	}
	
}
