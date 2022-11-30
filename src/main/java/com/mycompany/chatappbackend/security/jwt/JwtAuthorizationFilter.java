package com.mycompany.chatappbackend.security.jwt;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import com.mycompany.chatappbackend.security.SecurityUtils;

public class JwtAuthorizationFilter extends OncePerRequestFilter{

	@Autowired
	private JwtProvider jwtProvider;
	
	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
	    return Boolean.TRUE.equals(request.getServletPath().startsWith("/ws-chat"));
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

		String token = SecurityUtils.extractAuthTokenFromRequest(request);		
		Authentication autchentication = jwtProvider.getAuthentication(token);				

		if(autchentication != null) {
			SecurityContextHolder.getContext().setAuthentication(autchentication);				
		}
		
		filterChain.doFilter(request, response);	
	}


	
}
