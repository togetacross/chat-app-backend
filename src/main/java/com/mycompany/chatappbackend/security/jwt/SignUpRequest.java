package com.mycompany.chatappbackend.security.jwt;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpRequest {

	@NotBlank(message = "Name is required!")
	@Size(min = 3, max = 40, message = "Name field must be 3-40 chars!")
	private String name;
	@NotBlank(message = "Email is required!")
	private String email;
	@NotBlank(message = "Password is required!")
	@Size(min = 3, max = 40, message = "Password field must be 3-40 chars!")
	private String password;
}

