package com.mycompany.chatappbackend.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import com.mycompany.chatappbackend.security.SecurityUtils;
import com.mycompany.chatappbackend.security.UserPrinciple;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtProvider {

	@Value("${app.jwt.secret}")
	private String JWT_SECRET;

	@Value("${app.jwt.expiration-in-ms}")
	private Long JWT_EXPIRATION_IN_MS;

	public String generateToken(UserPrinciple auth) {

		String authorities = auth.getAuthorities()
					.stream()
						.map(GrantedAuthority::getAuthority)
						.collect(Collectors.joining(","));

		Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

		return Jwts.builder()
				.setSubject(auth.getEmail())
					.claim("roles", authorities)
					.claim("userId", auth.getId())
					.claim("name", auth.getUsername())
				.setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_IN_MS))
				.signWith(key, SignatureAlgorithm.HS512)
				.compact();
	}


	public Authentication getAuthentication(String token) {
		
		Claims claims = extractClaims(token);

		if (claims == null || claims.getExpiration().before(new Date())) {
			return null;
		}
		
		String email = claims.getSubject();
		Integer userId = claims.get("userId", Integer.class);
		//String name = claims.get("name").toString();
		Set<GrantedAuthority> authorities = Arrays.stream(claims.get("roles").toString().split(","))
				.map(SecurityUtils::convertToAuthority).collect(Collectors.toSet());

		UserDetails userDetails = UserPrinciple.builder()
				//.username(name)
				//Changed name to ID for Socket registry
				.username(userId.toString())
				.email(email)
				.authorities(authorities)
				.id(userId)
				.build();

		return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
	}
	
	
	public boolean isTokenValid(String token) {
		Claims claims = extractClaims(token);

		if (claims == null) {
			return false;
		}

		if (claims.getExpiration().before(new Date())) {
			return false;
		}

		return true;
	}

	public Claims extractClaims(String token) {
		
		if (token == null) {
			return null;
		}
		
		Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

		return Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	 
}
