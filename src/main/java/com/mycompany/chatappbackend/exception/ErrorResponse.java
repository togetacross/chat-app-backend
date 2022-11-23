package com.mycompany.chatappbackend.exception;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ErrorResponse {
	
	private String message;
	private Map<String, String> details;
}
