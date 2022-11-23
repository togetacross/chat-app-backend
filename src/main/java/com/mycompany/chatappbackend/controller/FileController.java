package com.mycompany.chatappbackend.controller;

import java.io.IOException;
import java.io.InputStream;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.engine.jdbc.StreamUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.chatappbackend.service.FileService;

@RestController
public class FileController {

	@Autowired
	private FileService fileService;
	
	@GetMapping("/files/{roomId}/{fileName}")
	public void loadFile(
			@PathVariable("fileName") String fileName,
			@PathVariable("roomId") String roomId,
			HttpServletResponse response
			) throws IOException 
	{
		
		System.out.println(fileName + " - " + roomId);
		InputStream inpusStream = fileService.getResource(fileName, roomId);
		response.setContentType(MediaType.IMAGE_JPEG_VALUE);
		StreamUtils.copy(inpusStream, response.getOutputStream());
	}
}
