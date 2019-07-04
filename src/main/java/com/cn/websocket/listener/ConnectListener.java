package com.cn.websocket.listener;

import io.netty.channel.Channel;

public interface ConnectListener {

	public void onConnect(Channel channel);
	
	public void onDisconnect(Channel channel);
	
}
