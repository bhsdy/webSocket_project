package com.cn.websocket.entity;

import io.netty.channel.Channel;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;
import java.util.*;

@Component
public class ChannelCache {

	private Set<Channel> channelSet = new HashSet<>();
	
	private Map<Channel, InetSocketAddress> channelRealAddressMap = new HashMap<>();
	
	public synchronized void addChannel(Channel channel) {
		channelSet.add(channel);
	}
	
	public Collection<Channel> getAllChannels() {
		return channelSet;
	}
	
	public synchronized void removeChannel(Channel channel) {
		channelSet.remove(channel);
		channelRealAddressMap.remove(channel);
		channel.close();
	}
	
	public InetSocketAddress getChannelRealIp(Channel channel) {
		return channelRealAddressMap.get(channel);
	}
	
	public void setChannelRealAddress(Channel channel, InetSocketAddress socketAddress) {
		channelRealAddressMap.put(channel, socketAddress);
	}
	
}
