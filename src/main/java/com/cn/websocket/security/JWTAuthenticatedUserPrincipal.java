package com.cn.websocket.security;

import com.cn.websocket.entity.enumEntiy.AuthorityEnum;
import com.cn.websocket.entity.enumEntiy.PlatformEnum;
import io.jsonwebtoken.Claims;
import lombok.Getter;

import java.io.Serializable;
import java.security.Principal;
import java.util.Set;

public class JWTAuthenticatedUserPrincipal implements Principal, Serializable {

	private static final long serialVersionUID = 3328060852676195656L;
	@Getter
	private final PlatformEnum platform;
	@Getter
	private final long uid;
	@Getter
	private final Set<String> roles;
	@Getter
	private final AuthorityEnum authority;
	
	private final Claims claims;
	
	@SuppressWarnings("unchecked")
	public JWTAuthenticatedUserPrincipal(Claims claims) {
		this.platform = PlatformEnum.valueOf(claims.get("platform", String.class));
		this.uid = claims.get("uid", Long.class);
		this.roles = claims.get("roles", Set.class);
		this.authority = AuthorityEnum.valueOf(claims.get("authority", String.class));
		this.claims = claims;
	}

	@Override
	public String getName() {
		return platform + "|" + uid;
	}
	
	public <T> T get(String claimName, Class<T> requiredType) {
		return claims.get(claimName, requiredType);
	}

}
