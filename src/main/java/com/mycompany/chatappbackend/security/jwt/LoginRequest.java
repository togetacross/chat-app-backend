package com.mycompany.chatappbackend.security.jwt;

import lombok.Data;

@Data
public class LoginRequest {
	
	private String email;
	private String password;
}
