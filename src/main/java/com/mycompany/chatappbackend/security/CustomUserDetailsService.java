package com.mycompany.chatappbackend.security;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.mycompany.chatappbackend.model.entity.User;
import com.mycompany.chatappbackend.service.UserService;

@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	@Autowired
	private UserService userService;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userService.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email));
		Set<GrantedAuthority> authorities = Set.of(SecurityUtils.convertToAuthority(user.getRole().name()));
		return UserPrinciple.builder()
				.user(user)
				.id(user.getId())
				.username(user.getName())
				.email(user.getEmail())
				.password(user.getPassword())
				.authorities(authorities)
				.build();
	}

}
