package com.mycompany.chatappbackend.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mycompany.chatappbackend.exception.ResourceNotFoundException;
import com.mycompany.chatappbackend.model.dto.User.UserDTO;
import com.mycompany.chatappbackend.model.dto.User.UserDetailsDTO;
import com.mycompany.chatappbackend.model.entity.Role;
import com.mycompany.chatappbackend.model.entity.User;
import com.mycompany.chatappbackend.model.entity.UserProfile;
import com.mycompany.chatappbackend.repository.UserRepository;
import com.mycompany.chatappbackend.security.jwt.JwtResponse;
import com.mycompany.chatappbackend.security.jwt.SignUpRequest;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private FileService fileService;

	@Autowired
	private ModelMapper modelMapper;

	public List<UserDTO> getByNameStartsWithAndNotIn(String prefix, List<Integer> userIds) {
		List<UserDTO> users = userRepository.findByNameStartingWithAndNotIn(prefix.trim(), userIds);
		if(users.isEmpty()) {
			throw new ResourceNotFoundException("Users not found!");
		}
		return users;
	}
	
	public List<UserDTO> getByNameStartsWith(String prefix) {
		List<UserDTO> users = userRepository.findByNameStartingWith(prefix.trim());
		if(users.isEmpty()) {
			throw new ResourceNotFoundException("Users not found!");
		}
		return users;
	}

	public JwtResponse saveUser(SignUpRequest signUpRequest) {
		User newUser = new User();
		newUser.setName(signUpRequest.getName());
		newUser.setEmail(signUpRequest.getEmail());
		newUser.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));
		newUser.setRole(Role.USER);
		
		UserProfile userProfile = new UserProfile(newUser);
		userProfile.setName(signUpRequest.getName());
		
		newUser.setUserProfile(userProfile);
		
		userRepository.save(newUser);
		JwtResponse jwtResponse = modelMapper.map(newUser, JwtResponse.class);
		return jwtResponse;
	}
	
	public User getReferenceById(int userId) {
		return userRepository.getReferenceById(userId);
	}
	
	public List<Integer> getAllReference() {
		return userRepository.findAllReference();
	}

	public Optional<User> findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	@Transactional 
	public void changeRole(Role newRole, String email) {
		userRepository.updateUserRole(email, newRole);
	}
	
	@Transactional
	public UserDTO updateProfileImage(MultipartFile file, Integer userId) {
		byte[] image = fileService.getMultipartFileInByte(file);
		User user = userRepository.findById(userId).get();
		user.getUserProfile().setImage(image);
		User savedUser = userRepository.save(user);
		return new UserDTO(savedUser.getId(), savedUser.getName(), savedUser.getUserProfile().getImage());
	}
	
	public Set<User> getUsersWhereIdIn(List<Integer> userIds) {
		return userRepository.findByIdIn(userIds);
	}
	
	public List<User> getUsersWhereIdInWithProfile(List<Integer> userIds) {
		return userRepository.findByIdInWithProfile(userIds);
	}
	
	public User getUserByIdWithProfile(Integer userId) {
		return userRepository.findByIdWithProfile(userId).get();
	}
	
	public long getUserCount() {
		return userRepository.count();
	}
	
	public List<UserDTO> getAllUser() {
		return userRepository.findAllWithProfile();
	}
	
	public UserDetailsDTO getUserDetails(Integer userId) {
		return userRepository.findByIdWithDetails(userId);
	}
}
