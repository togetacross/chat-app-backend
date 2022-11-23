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
import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.chatappbackend.model.entity.AttachmentType;

@Service
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
		} catch (Exception e) {// need separate
			e.printStackTrace();
			return loadDefaultImage();
		}
		return file;
	}
	
	public InputStream getResource(String name, String subPath) throws FileNotFoundException {
		return new FileInputStream(filesStorage + "/" + subPath + "/" + name);
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
	
	public byte[] getFileInByteArray(String name, String subPath) throws IOException {
		String encodedImageName = encodeToUrl(name);
		return Files.readAllBytes(Paths.get(filesStorage + "/" + subPath + "/" + encodedImageName));		
	}
	
	public void uploadFile(MultipartFile file, String subPath, String fileName) throws IOException {
		String encodedImageName = encodeToUrl(fileName);
		Files.createDirectories(Paths.get(filesStorage + "/" + subPath));
		Files.copy(file.getInputStream(), 
				Paths.get(filesStorage + "/" + subPath , encodedImageName),
				StandardCopyOption.REPLACE_EXISTING);
	}
	
	public void updloadImage(MultipartFile file, String subPath, String fileName) throws IOException {
		BufferedImage resizedImage = resizeImage(file);
		String encodedImageName = encodeToUrl(fileName);
		Files.createDirectories(Paths.get(filesStorage + "/" + subPath));
		File imageFile = new File(filesStorage + "/" + subPath + "/" + encodedImageName);
		String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
		ImageIO.write(resizedImage, extension, imageFile);
	}
	
	private String encodeToUrl(String name) throws UnsupportedEncodingException {
		return URLEncoder.encode(name, StandardCharsets.UTF_8.toString());
	}
	
	private BufferedImage resizeImage(MultipartFile file) throws IOException {
		BufferedImage img = ImageIO.read(file.getInputStream());
		BufferedImage thumbImg = Scalr.resize(img, Scalr.Method.BALANCED, WIDTH);	
		return thumbImg;
	}
	
	public byte[] loadDefaultImage() {
		byte[] image = null;
		Resource resource = new ClassPathResource("group.png");
		try {
			image = resource.getInputStream().readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}
}
