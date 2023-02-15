package com.mycompany.chatappbackend.service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.servlet.ServletContext;
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import com.mycompany.chatappbackend.model.entity.AttachmentType;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class FileService {

	@Value("${files_storage.dir}")
	private String filesStorage;
	
	private static final int WIDTH = 800;
	private static final String [] imageExtensionArr = new String[] {"jpg", "png"};
	private static final String [] videoExtensionArr = new String[] {"mkv", "mov", "avi"};	
	
	
	public byte[] getMultipartFileInByte(MultipartFile multipartFile) {
		byte[] file = null;
		try {
			file = multipartFile.getBytes();
		} catch (NullPointerException | IOException e) {
			log.warn(e.getMessage());
		}
		return file;
	}
	
	public InputStream getResource(String name, String subPath) throws FileNotFoundException, UnsupportedEncodingException {
		String formattedName = name.replaceAll("\\s", "");
		String encodedImageName = encodeToUrl(formattedName);
		return new FileInputStream(filesStorage + "/" + subPath + "/" + encodedImageName);
	}
	
	public MediaType getMediaTypeForFileName(ServletContext servletContext, String fileName) {
	    String mineType = servletContext.getMimeType(fileName);
	      try {
	           MediaType mediaType = MediaType.parseMediaType(mineType);
	           return mediaType;
	      } catch (Exception e) {
	           return MediaType.APPLICATION_OCTET_STREAM;
	      }
	 }
	
	
	public AttachmentType getFileType(String name) {
		String fileExtension = name.substring(name.lastIndexOf(".") + 1);		
		if(Arrays.asList(imageExtensionArr).contains(fileExtension)) {
			return AttachmentType.IMAGE;
		} else if (Arrays.asList(videoExtensionArr).contains(fileExtension)) {
			return AttachmentType.VIDEO;			
		} else {
			return AttachmentType.DOCUMENT;						
		}		
	}
	
	public byte[] getFileInByteArray(String name, String subPath) {
		try {
			String encodedImageName = encodeToUrl(name != null ? name : "");
			return Files.readAllBytes(Paths.get(filesStorage + "/" + subPath + "/" + encodedImageName));					
		} catch (IOException e) {
			log.warn(e.getMessage());
		}
		return null;
	}
	

	public void updload(MultipartFile file, String subPath, String fileName) throws IOException {
		String encodedImageName = encodeToUrl(fileName);
		Files.createDirectories(Paths.get(filesStorage + "/" + subPath));
		
		if(getFileType(fileName).equals(AttachmentType.IMAGE)) {
			File imageFile = new File(filesStorage + "/" + subPath + "/" + encodedImageName);
			String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
			BufferedImage resizedImage = resizeImage(file);
			ImageIO.write(resizedImage, extension, imageFile);			
		} else {
			Files.copy(
					file.getInputStream(), 
					Paths.get(filesStorage + "/" + subPath , encodedImageName),
					StandardCopyOption.REPLACE_EXISTING
					);			
		}
	}
	
	private String encodeToUrl(String name) throws UnsupportedEncodingException {
		return URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
	}
	
	private BufferedImage resizeImage(MultipartFile file) throws IOException {
		BufferedImage img = ImageIO.read(file.getInputStream());
		BufferedImage thumbImg = Scalr.resize(img, Scalr.Method.BALANCED, WIDTH);	
		return thumbImg;
	}
}
