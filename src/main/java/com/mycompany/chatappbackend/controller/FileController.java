package com.mycompany.chatappbackend.controller;

import java.io.IOException;
import javax.servlet.ServletContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mycompany.chatappbackend.security.UserPrinciple;
import com.mycompany.chatappbackend.service.FileService;

@RestController
@RequestMapping("/chatapp")
public class FileController {

	@Autowired
	private FileService fileService;
	
	@Autowired
	private ServletContext servletContext;
	
	@PreAuthorize("@convesrationRoleService.hasRole(#principle.getId, #roomId, 'USER')")
	@GetMapping("/files/download")
	public ResponseEntity<?> download(
			@RequestParam("roomId") String roomId,
			@RequestParam("fileName") String fileName,
			@AuthenticationPrincipal UserPrinciple principle
			) throws IOException 
	{
		
		MediaType mediaType = fileService.getMediaTypeForFileName(this.servletContext, fileName);
		
		byte[] fileInByte = fileService.getFileInByteArray(fileName, roomId);
		ByteArrayResource resource = new ByteArrayResource(fileInByte);	
		
		HttpHeaders headers=new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION,"attachment; filename=\"" + fileName + "\"");
        headers.add("Access-Control-Expose-Headers",HttpHeaders.CONTENT_DISPOSITION + "," + HttpHeaders.CONTENT_LENGTH);
		
		return ResponseEntity.ok()
	               .headers(headers)
	               .contentType(mediaType)
	               .contentLength(fileInByte.length) 
	               .body(resource);
	}
}
