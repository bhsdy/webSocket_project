package com.cn.websocket.entity;

import io.netty.channel.Channel;

public class ChannelContext {

	private static ThreadLocal<Channel> resource = new ThreadLocal<>();
	
	public static void set(Channel channel) {
		resource.set(channel);
	}
	
	public static Channel getCurrentChannel() {
		return resource.get();
	}

	public static void remove() {
		resource.remove();
	}
	
}
