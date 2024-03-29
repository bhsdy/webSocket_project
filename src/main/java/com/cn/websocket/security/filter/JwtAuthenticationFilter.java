package com.cn.websocket.security.filter;

import com.cn.websocket.security.JwtAuthenticationToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

/**
 * Filter responsible to intercept the JWT in the HTTP header and attempt an
 * authentication. It delegates the authentication to the authentication manager
 */
public class JwtAuthenticationFilter extends GenericFilterBean {

	@Value("${token.cookie}")
	private String AUTH_COOKIE;

	@Value("${token.header}")
	private String AUTH_HEADER;

	@Autowired
	private AuthenticationManager authenticationManager;

	@Autowired
	private AuthenticationEntryPoint unauthorizedEntryPoint;

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
			throws IOException, ServletException {
		final HttpServletRequest request = (HttpServletRequest) req;
		final HttpServletResponse response = (HttpServletResponse) res;
		if (request.getMethod().equals("OPTIONS")) {
			// CORS request
			chain.doFilter(request, response);
			return;
		}

		Optional<String> token = getToken(request);
		if (token.isPresent()) {
			try {
				Authentication authentication = authenticationManager.authenticate(new JwtAuthenticationToken(token.get()));
				SecurityContextHolder.getContext().setAuthentication(authentication);
			} catch(AuthenticationException e) {
				SecurityContextHolder.clearContext();
				unauthorizedEntryPoint.commence(request, response, e);
				return;
			}
		}
		chain.doFilter(request, response);
	}

	private Optional<String> getToken(HttpServletRequest request) {
		Optional<String> token = Optional.empty();
		token = getTokenByCookie(request, AUTH_COOKIE);
		if (token.isPresent()) {
			return token;
		}
		token = getTokenByHeader(request, AUTH_HEADER);
		if (token.isPresent()) {
			return token;
		}
		return token;
	}

	/**
	 * Find a specific HTTP cookie in a request.
	 *
	 * @param request
	 *            The HTTP request object.
	 * @param name
	 *            The cookie name to look for.
	 * @return The cookie, or <code>null</code> if not found.
	 */
	public static Optional<String> getTokenByCookie(HttpServletRequest request, String name) {
		if (request.getCookies() == null) {
			return Optional.empty();
		}

		for (int i = 0; i < request.getCookies().length; i++) {
			if (request.getCookies()[i].getName().equals(name)) {
				Cookie authCookie = request.getCookies()[i];
				if (authCookie != null) {
					return Optional.of(authCookie.getValue());
				}
			}
		}

		return Optional.empty();
	}

	/**
	 * Looks at the authorization bearer and extracts the JWT
	 */
	public static Optional<String> getTokenByHeader(HttpServletRequest httpRequest, String name) {
		final String authorizationHeader = httpRequest.getHeader(name);
		if (StringUtils.isEmpty(authorizationHeader)) {
			return Optional.empty();
		}
		return Optional.of(authorizationHeader);
	}
}
