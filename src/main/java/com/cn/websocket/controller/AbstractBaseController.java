package com.cn.websocket.controller;

import com.cn.websocket.entity.*;
import com.cn.websocket.exception.ServerException;
import com.cn.websocket.security.JWTAuthenticatedUserPrincipal;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.net.InetSocketAddress;

@Slf4j
public abstract class AbstractBaseController {

	@Autowired
	protected HttpServletRequest request;

	public ResponseEntity<RestError> ok() {
		return ResponseEntity.ok(RestError.builder().restStatus(RestStatus.SUCCESS).build());
	}

	public <T> ResponseEntity<ResponseDTO<T>> ok(T data) {
		return ResponseEntity.ok(new ResponseDTO<T>(0, "", data));
	}

	public Long getCurrentUserId() {
		return getCurrentAuthenticatedUser().getUid();
	}
	
	public Long getUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof JWTAuthenticatedUserPrincipal) {
				JWTAuthenticatedUserPrincipal authenticatedUser = (JWTAuthenticatedUserPrincipal) principal;
				return authenticatedUser.getUid();
			}
		}
		return null;
	}
	
	protected String getIpAddress() {
		String ip = null;
		Channel channel = ChannelContext.getCurrentChannel();
		if(channel != null) {
			InetSocketAddress address = (InetSocketAddress)channel.remoteAddress();
			return address.getAddress().getHostAddress();
		} else {
			if (request != null) {
				String ipAddresses = request.getHeader("X-Forwarded-For"); // X-Forwarded-For：Squid 服务代理
				if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// Proxy-Client-IP：apache 服务代理
					ipAddresses = request.getHeader("Proxy-Client-IP");
				}
				if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// WL-Proxy-Client-IP：weblogic 服务代理
					ipAddresses = request.getHeader("WL-Proxy-Client-IP");
				}
				if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// HTTP_CLIENT_IP：有些代理服务器
					ipAddresses = request.getHeader("HTTP_CLIENT_IP");
				}
				if (ipAddresses == null || ipAddresses.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// X-Real-IP：nginx服务代理
					ipAddresses = request.getHeader("X-Real-IP");
				}
				if (ipAddresses != null && ipAddresses.length() != 0) {// 有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
					ip = ipAddresses.split(",")[0];
				}
				if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ipAddresses)) {// 还是不能获取到，最后再通过request.getRemoteAddr();获取
					ip = request.getRemoteAddr();  
				}
			} 
		}
		return ip;
	}

	public JWTAuthenticatedUserPrincipal getCurrentAuthenticatedUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			Object principal = authentication.getPrincipal();
			if (principal instanceof JWTAuthenticatedUserPrincipal) {
				JWTAuthenticatedUserPrincipal authenticatedUser = (JWTAuthenticatedUserPrincipal) principal;
				return authenticatedUser;
			}
			if (authentication.getClass().getSimpleName().indexOf("Anonymous") < 0) {
				log.error("Unknown authentication encountered, ignore it. " + authentication);
			}
		}
		throw new ServerException(RestStatus.ERROR_UNAUTHORIZED);
	}

}
