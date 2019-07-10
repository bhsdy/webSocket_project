package com.cn.websocket.entity;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Map;

public class Request {

	private final String cmd;

	private final int requestId;

	private final String sessionId;
	
	private final Map<String, Object> parmaMap;

	private final Channel channel;

	public Request(String cmd, String sessionId, int requestId, Map<String, Object> paramMap, Channel channel) {
		this.cmd = cmd;
		this.sessionId = sessionId;
		this.requestId = requestId;
		this.parmaMap = Collections.unmodifiableMap(paramMap);
		this.channel = channel;
	}

	public String getCmd() {
		return cmd;
	}

	public int getRequestId() {
		return requestId;
	}

	public Map<String, Object> getParmaMap() {
		return parmaMap;
	}

	public Channel getChannel() {
		return channel;
	}

	public String getHost() {
		return ((InetSocketAddress) (channel.remoteAddress())).getHostName();
	}

	public String getSessionId() {
		return sessionId;
	}

}
