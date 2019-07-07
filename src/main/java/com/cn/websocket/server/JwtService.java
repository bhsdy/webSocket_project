package com.cn.websocket.server;

import com.cn.websocket.entity.enumEntiy.AuthorityEnum;
import com.cn.websocket.entity.enumEntiy.PlatformEnum;
import com.cn.websocket.exception.JwtAuthenticationException;
import com.cn.websocket.security.JWTAuthenticatedUserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class JwtService {

	@Value(value = "${token.secret}")
	protected String secret;

	@Value(value = "${token.expiredSeconds}")
	protected Integer expirySeconds;

	/**
	 * 签发Token
	 * @param platform
	 * @param uid
	 * @return
	 */
	public String sign(PlatformEnum platform, long uid, AuthorityEnum authority) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("platform", platform.toString());
		claims.put("uid", uid);
		claims.put("authority", authority);
		LocalDateTime now = LocalDateTime.now();
		return Jwts.builder().setClaims(claims).setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(now.plusSeconds(expirySeconds).atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public String sign(PlatformEnum platform, String providerUserId, long uid, Set<String> roles) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("platform", platform);
		claims.put("uid", String.valueOf(uid));
		LocalDateTime now = LocalDateTime.now();
		return Jwts.builder().setClaims(claims).setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(now.plusSeconds(expirySeconds).atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	public String resign(JWTAuthenticatedUserPrincipal authenticatedUser) {
		Map<String, Object> claims = new HashMap<String, Object>();
		claims.put("platform", authenticatedUser.getPlatform());
		claims.put("uid", String.valueOf(authenticatedUser.getUid()));
		claims.put("authority", authenticatedUser.getAuthority());
		LocalDateTime now = LocalDateTime.now();
		return Jwts.builder().setClaims(claims).setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(now.atZone(ZoneId.systemDefault()).toInstant()))
				.setExpiration(Date.from(now.plusSeconds(expirySeconds).atZone(ZoneId.systemDefault()).toInstant()))
				.signWith(SignatureAlgorithm.HS512, secret).compact();
	}

	/**
	 * 验证Token
	 * 
	 * @param jwt
	 * @return
	 * @throws AuthenticationException
	 */
	public Claims verify(String jwt) throws AuthenticationException {
		return verify(jwt, this.secret);
	}
	
	public Claims verify(String jwt, String secret) throws AuthenticationException {
		try { 
			return Jwts.parser().setSigningKey(secret).parseClaimsJws(jwt).getBody();
		} catch (Exception e) {
			throw new JwtAuthenticationException(e);
		}
	}
	
}
