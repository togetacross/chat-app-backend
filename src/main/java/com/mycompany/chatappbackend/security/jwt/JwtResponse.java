package com.mycompany.chatappbackend.security.jwt;

import com.mycompany.chatappbackend.model.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
	
	private Integer id;
	private String name;
	private String email;
	private String token;
	private Role role;
	
}
