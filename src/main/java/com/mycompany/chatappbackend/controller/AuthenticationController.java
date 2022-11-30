package com.mycompany.chatappbackend.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.chatappbackend.security.jwt.LoginRequest;
import com.mycompany.chatappbackend.security.jwt.SignUpRequest;
import com.mycompany.chatappbackend.service.AuthenticationService;
import com.mycompany.chatappbackend.service.UserService;

@RestController
@RequestMapping("/chatapp/auth")
public class AuthenticationController {

	@Autowired
	private UserService userService;

	@Autowired
	private AuthenticationService authenticationService;

	@PostMapping("/sign-in")
	public ResponseEntity<?> signIn(@RequestBody LoginRequest loginRequest) {
		return new ResponseEntity<>(authenticationService.signInAndReturnJWT(loginRequest), HttpStatus.OK);
	}
	
	@PostMapping("/sign-up")
	public ResponseEntity<?> signUp(@Valid @RequestBody SignUpRequest signUpRequest) {
		if(userService.findByEmail(signUpRequest.getEmail()).isPresent()) {
			return new ResponseEntity<>(HttpStatus.CONFLICT);
		}
		return new ResponseEntity<>(userService.saveUser(signUpRequest), HttpStatus.CREATED);
	}
	
	
}
