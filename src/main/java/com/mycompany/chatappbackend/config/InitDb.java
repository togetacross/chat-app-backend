package com.mycompany.chatappbackend.config;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.mycompany.chatappbackend.model.dto.ChatRoom.NewChatRoomDTO;
import com.mycompany.chatappbackend.model.dto.Message.MessageDTO;
import com.mycompany.chatappbackend.model.entity.Role;
import com.mycompany.chatappbackend.model.entity.User;
import com.mycompany.chatappbackend.model.entity.UserProfile;
import com.mycompany.chatappbackend.repository.UserRepository;
import com.mycompany.chatappbackend.security.jwt.JwtResponse;
import com.mycompany.chatappbackend.security.jwt.SignUpRequest;
import com.mycompany.chatappbackend.service.ChatRoomService;
import com.mycompany.chatappbackend.service.MessageService;
import com.mycompany.chatappbackend.service.UserService;

//@Configuration
public class InitDb implements CommandLineRunner {
/*
	@Autowired
	private UserRepository userRepository;

	@Autowired
	private ChatRoomService chatRoomService;

	@Autowired
	private MessageService messageService;

	@Autowired
	private PasswordEncoder passwordEncoder;*/

	// @Transactional
	@Override
	public void run(String... args) throws Exception {
	/*	initUsers();
		initConversations();
		initMessages(1, new Integer[] { 1, 2, 3, 5, 6 }, "GROUP");
		initMessages(2, new Integer[] { 1, 2, 3, 5, 6 }, "GROUP");
		initMessages(3, new Integer[] { 1, 2, 3, 5, 6 }, "GROUP");
		initMessages(12, new Integer[] { 1, 2 }, "GROUP");
		initMessages(13, new Integer[] { 1, 3}, "GROUP");*/
	}
/*
	public void addUser(SignUpRequest signUpRequest, byte[] image) {
		User newUser = new User();
		newUser.setName(signUpRequest.getName());
		newUser.setEmail(signUpRequest.getEmail());
		newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
		newUser.setRole(Role.USER);
		UserProfile userProfile = new UserProfile(newUser);
		userProfile.setName(signUpRequest.getName());
		userProfile.setImage(image);
		newUser.setUserProfile(userProfile);
		userRepository.save(newUser);
	}

	private void initUsers() {
		List<SignUpRequest> userList = Arrays.asList(
				new SignUpRequest("Rio", "asd@asd.hu", "1234"),
				new SignUpRequest("Tibi", "lionhearttibor@gmail.com", "1234"),
				new SignUpRequest("Tio", "asd@asd11.hu", "1234"),
				new SignUpRequest("Tiadora", "asd@asd23.hu", "1234"),
				new SignUpRequest("Tikituki", "asd@asd44.hu", "1234"),
				new SignUpRequest("Rella", "asd@asd33.hu", "1234"),
				new SignUpRequest("Roka", "asd@asd22.hu", "1234"),
				new SignUpRequest("Riololo", "asd@asd2.hu", "1234"),
				new SignUpRequest("Riokokoa", "asd@asd3.hu", "1234"),
				new SignUpRequest("RioLulu", "asd@asd4.hu", "1234"),
				new SignUpRequest("Riopula", "asd@asd5.hu", "1234"),
				new SignUpRequest("Riosadd", "asd@asd6.hu", "1234")
				);
	
		userList.forEach(u -> addUser(u, generateProfileImage()));
	}

	private byte[] generateProfileImage() {
		Integer profilNumber = generateRandomNumberFromArray(new Integer[] { 1, 2, 3, 4 });
		byte[] image = null;
		Resource resource = new ClassPathResource("profile" + profilNumber + ".png");
		try {
			image = resource.getInputStream().readAllBytes();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	private void initConversations() {
		List<Integer> userIds_1 = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6 });
		List<Integer> userIds_2 = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8 });
		List<Integer> userIds_3 = Arrays.asList(new Integer[] { 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11 });

		List<Integer> private_1 = Arrays.asList(new Integer[] { 1, 2 });
		List<Integer> private_2 = Arrays.asList(new Integer[] { 1, 3 });

		List<NewChatRoomDTO> conversationList = Arrays.asList(
				new NewChatRoomDTO("A-Team", userIds_1, "GROUP"),
				new NewChatRoomDTO("B-Team", userIds_2, "GROUP"), 
				new NewChatRoomDTO("Team All", userIds_3, "GROUP"),
				new NewChatRoomDTO("C-Team", userIds_1, "GROUP"), 
				new NewChatRoomDTO("D-Team", userIds_2, "GROUP"),
				new NewChatRoomDTO("E-Team All", userIds_3, "GROUP"), 
				new NewChatRoomDTO("F-Team", userIds_1, "GROUP"),
				new NewChatRoomDTO("G-Team", userIds_1, "GROUP"), 
				new NewChatRoomDTO("H-Team", userIds_2, "GROUP"),
				new NewChatRoomDTO("I-Team", userIds_2, "GROUP"), 
				new NewChatRoomDTO("J-Team", userIds_1, "GROUP"),
				new NewChatRoomDTO(null, private_1, "PRIVATE"), 
				new NewChatRoomDTO(null, private_2, "PRIVATE"));

		conversationList.forEach(conversation -> chatRoomService.createChatRoom(null, conversation, 1));
	}

	private void initMessages(Integer roomId, Integer [] userIds, String type) {

		String[] words = new String[] { "Hey", "Hy", "How are you?", "Fine thanks and you?", "What's this?", "Why not?",
				"This is a long text for conversation...", "Any good news comming soon..",
				"Application testing is successefull please wait 1-2 weak to production.",
				"We can go to another room, couse its full of idiots.", "Random sentence, nothing ideaa..",
				"One more intresting thing for this conversation.", "This is a bug shit absolutly.",
				"Last words, last never more..." };

		for (int i = 0; i < 1000; i++) {
			messageService.saveMessage(null,
					new MessageDTO(roomId, generateRandomNumberFromArray(userIds), type,
							generateMessageText(words)));
		}
	}

	private String generateMessageText(String[] arr) {
		return arr[new Random().nextInt(arr.length)];
	}

	private Integer generateRandomNumberFromArray(Integer[] arr) {
		return arr[new Random().nextInt(arr.length)];
	}

 */
}
