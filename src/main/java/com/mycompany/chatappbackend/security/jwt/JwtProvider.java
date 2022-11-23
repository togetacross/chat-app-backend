package com.mycompany.chatappbackend.security.jwt;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
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

	public Authentication getAuthentication(HttpServletRequest request) {
		Claims claims = extractClaims(request);

		if (claims == null) {
			return null;
		}

		String email = claims.getSubject();
		Integer userId = claims.get("userId", Integer.class);
		String name = claims.get("name").toString();
		Set<GrantedAuthority> authorities = Arrays.stream(claims.get("roles").toString().split(","))
				.map(SecurityUtils::convertToAuthority).collect(Collectors.toSet());

		//UserDetails userDetails = UserPrinciple.builder().email(email).authorities(authorities).id(userId).build();
		UserDetails userDetails = UserPrinciple.builder()
				.username(name)
				.email(email)
				.authorities(authorities)
				.id(userId)
				.build();

		if (email == null) {
			return null;
		}

		return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
	}
	
	public boolean isValidTokenForSocket(String authHeader) {
		String token = SecurityUtils.extractAuthTokenFromToken(authHeader);
		
		if (token == null) {
			return false;
		}

		Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));

		Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		if (claims == null) {
			return false;
		}

		if (claims.getExpiration().before(new Date())) {
			return false;
		}

		return true;
	}

	public boolean isTokenValid(HttpServletRequest request) {
		Claims claims = extractClaims(request);

		if (claims == null) {
			return false;
		}

		if (claims.getExpiration().before(new Date())) {
			return false;
		}

		return true;
	}

	public Claims extractClaims(HttpServletRequest request) {
		String token = SecurityUtils.extractAuthTokenFromRequest(request);
		
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

	public Authentication getSocketAuthentication(String authHeader) {
		String token = SecurityUtils.extractAuthTokenFromToken(authHeader);
		
		if (token == null) {
			return null;
		}
		
		Key key = Keys.hmacShaKeyFor(JWT_SECRET.getBytes(StandardCharsets.UTF_8));
		
		Claims claims = Jwts.parserBuilder()
				.setSigningKey(key)
				.build()
				.parseClaimsJws(token)
				.getBody();
		
		if (claims == null) {
			return null;
		}
		
		if (claims.getExpiration().before(new Date())) {
			return null;
		}
		
		String email = claims.getSubject();
		Integer userId = claims.get("userId", Integer.class);
		String name = claims.get("name").toString();
		Set<GrantedAuthority> authorities = Arrays.stream(claims.get("roles").toString().split(","))
				.map(SecurityUtils::convertToAuthority).collect(Collectors.toSet());

		// changed name to id for socket registry
		UserDetails userDetails = UserPrinciple.builder()
				.username(userId.toString())
				.email(email)
				.authorities(authorities)
				.id(userId)
				.build();
		
		if (email == null) {
			return null;
		}
		return new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
	}

}
