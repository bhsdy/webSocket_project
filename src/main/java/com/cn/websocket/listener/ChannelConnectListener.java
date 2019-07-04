package com.cn.websocket.listener;

import com.cn.websocket.entity.ChannelCache;
import com.cn.websocket.entity.ChannelManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ChannelConnectListener implements ConnectListener {

	@Autowired
	private ChannelManager channelManager;
	
	@Autowired
	private ChannelCache channelCache;
	
	@Override
	public void onConnect(Channel channel) {
		channelManager.add(channel);
		channelCache.addChannel(channel);
	}
	
	@Override
	public void onDisconnect(Channel channel) {
		log.warn("channel id:{} disconnect", channel.id().asShortText());
		channelManager.remove(channel);
		channelCache.removeChannel(channel);
	}

}
