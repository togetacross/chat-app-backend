package com.mycompany.chatappbackend.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpRequest {

	private String name;
	private String email;
	private String password;
}

