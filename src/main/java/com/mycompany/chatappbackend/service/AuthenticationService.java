package com.mycompany.chatappbackend.service;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.mycompany.chatappbackend.security.UserPrinciple;
import com.mycompany.chatappbackend.security.jwt.JwtProvider;
import com.mycompany.chatappbackend.security.jwt.JwtResponse;
import com.mycompany.chatappbackend.security.jwt.LoginRequest;

@Service
public class AuthenticationService {

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtProvider jwtProvider;
	
	@Autowired
	private ModelMapper modelMapper;
	
	public JwtResponse signInAndReturnJWT(LoginRequest loginRequest) {		
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
		UserPrinciple userPrinciple = (UserPrinciple) authentication.getPrincipal();
		JwtResponse jwtRespone = modelMapper.map(userPrinciple.getUser(), JwtResponse.class);
		jwtRespone.setToken(jwtProvider.generateToken(userPrinciple));
		return jwtRespone;
	}
	
}
