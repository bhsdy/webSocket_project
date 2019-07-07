package com.cn.websocket.security.provider;

import com.cn.websocket.exception.JwtAuthenticationException;
import com.cn.websocket.security.JWTAuthenticatedUserPrincipal;
import com.cn.websocket.security.JwtAuthenticationToken;
import com.cn.websocket.server.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.util.Assert;

/**
 * Class that verifies the JWT token and when valid, it will set the userdetails
 * in the authentication object
 */
public class JwtAuthenticationProvider implements AuthenticationProvider {

	private JwtService jwtService;

	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		Assert.isInstanceOf(JwtAuthenticationToken.class, authentication, "Only JwtAuthenticationToken is supported");

		if (authentication.getCredentials() == null) {
			throw new JwtAuthenticationException("Bad jwt credentials");
		}

		Claims claims = jwtService.verify(authentication.getCredentials().toString());
		JWTAuthenticatedUserPrincipal principal = new JWTAuthenticatedUserPrincipal(claims);
		JwtAuthenticationToken result = new JwtAuthenticationToken(principal,
				authentication.getCredentials().toString(), null);
		result.setDetails(authentication.getDetails());
		return result;
	}

	public boolean supports(Class<?> authentication) {
		return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
	}

	public JwtService getJwtService() {
		return jwtService;
	}

	public void setJwtService(JwtService jwtService) {
		this.jwtService = jwtService;
	}
	
}
