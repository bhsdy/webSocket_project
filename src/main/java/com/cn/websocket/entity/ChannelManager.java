package com.cn.websocket.entity;

import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ChannelManager {

	private Map<String, Set<Channel>> symbolChannelSetMap = new ConcurrentHashMap<>();
	
	private Map<Channel, Set<String>> channelSymoblSetMap = new ConcurrentHashMap<>();

	private Map<Channel, String> klineChannelSymbolMap = new ConcurrentHashMap<>();
	
	private Map<String, Set<Channel>> klineSymbolChannelSetMap = new ConcurrentHashMap<>();
	
	@Autowired
	private ChannelCache channelCache;
	
	public List<Channel> getQuoteAllChannelList() {
		return new ArrayList<>(new ArrayList<>(channelSymoblSetMap.keySet()));
	}
	
	public List<Channel> getKlineAllChannelList() {
		return new ArrayList<>(new ArrayList<>(klineChannelSymbolMap.keySet()));
	}
	
	public List<Channel> getQuoteChannelList(String symbol) {
		Set<Channel> channelSet = symbolChannelSetMap.get(symbol);
		if(channelSet != null) {
			return new ArrayList<>(channelSet);
		} else {
			return new ArrayList<>();
		}
	}
	
	public List<Channel> getKlineChannelList(String symbol) {
		Set<Channel> channelSet = klineSymbolChannelSetMap.get(symbol);
		if(channelSet != null) {
			return new ArrayList<>(channelSet);
		} else {
			return new ArrayList<>();
		}
	}
	
	public void add(Channel channel) {
		channelSymoblSetMap.put(channel, new HashSet<>());
		log.warn("channel add: {}, {}", channel.id().toString(), channelCache.getChannelRealIp(channel));
	}
	
	public void remove(Channel channel) {
		synchronized (channel) {
			Set<String> symbolSet = channelSymoblSetMap.remove(channel);
			for(String symbol : symbolSet) {
				synchronized (symbol.intern()) {
					Set<Channel> channelSet = symbolChannelSetMap.get(symbol);
					channelSet.remove(channel);
				}
			}	
			unsubscribeKline(channel);
		}
		log.warn("channel remove: {}, {}", channel.id().toString(), channelCache.getChannelRealIp(channel));
	}
	
	public void subscribeQuote(Channel channel, String symbol) {
		if(!channel.isActive()) {
			log.warn("channel subscribeQuote not active: {}, {}, {}", channel.id().toString(), channelCache.getChannelRealIp(channel), symbol);
			return;
		}
		synchronized(channel) {
			synchronized (symbol.intern()) {
				Set<String> symbolSet = channelSymoblSetMap.get(channel);
				if(symbolSet == null) {
					symbolSet = new HashSet<>();
					channelSymoblSetMap.put(channel, symbolSet);
				}
				if(symbol.equals("All")) {
					for(String tmpSymbol : symbolSet) {
						synchronized (tmpSymbol.intern()) {
							Set<Channel> tmpSymbolChannelSet = symbolChannelSetMap.get(tmpSymbol);
							if(tmpSymbolChannelSet != null) {
								tmpSymbolChannelSet.remove(channel);
							}
						}
					}
					symbolSet.clear();
				}
				symbolSet.add(symbol);
				Set<Channel> channelSet = symbolChannelSetMap.get(symbol);
				if(channelSet == null) {
					channelSet = new HashSet<>();
					symbolChannelSetMap.put(symbol, channelSet);
				}
				channelSet.add(channel);
			}
		}
		log.warn("channel subscribeQuote: {}, {}, {}", channel.id().toString(), channelCache.getChannelRealIp(channel), symbol);
	}
	
	public void unsubscribeQuote(Channel channel, String symbol) {
		synchronized(channel) {
			synchronized (symbol.intern()) {
				Set<Channel> channelSet = symbolChannelSetMap.get(symbol);
				if(channelSet != null) {
					channelSet.remove(channel);
				}
				Set<String> symbolSet = channelSymoblSetMap.get(channel);
				if(symbolSet != null) {
					symbolSet.remove(symbol);
				}
			}
		}
		log.warn("channel unsubscribeQuote: {}, {}, {}", channel.id().toString(), channelCache.getChannelRealIp(channel), symbol);
	}
	
	public void subscribeKline(Channel channel, String symbol) {
		if(!channel.isActive()) {
			log.warn("channel subscribeKline not active: {}, {}", channel.id().toString(), channelCache.getChannelRealIp(channel), symbol);
			return;
		}
		synchronized (channel) {
			unsubscribeKline(channel);
			klineChannelSymbolMap.put(channel, symbol);
			synchronized (symbol.intern()) {
				Set<Channel> channelSet = klineSymbolChannelSetMap.get(symbol);
				if(channelSet == null) {
					channelSet = new HashSet<>();
					klineSymbolChannelSetMap.put(symbol, channelSet);
				}
				channelSet.add(channel);
			}
		}
		log.warn("channel subscribeKline: {}, {}, {}", channel.id().toString(), channelCache.getChannelRealIp(channel), symbol);
	}
	
	public void unsubscribeKline(Channel channel) {
		synchronized (channel) {
			String symbol = klineChannelSymbolMap.remove(channel);
			if(symbol != null) {
				Set<Channel> channelSet = klineSymbolChannelSetMap.get(symbol);
				if(channelSet != null) {
					channelSet.remove(channel);
				}
			}
		}
		log.warn("channel unsubscribeKline: {}, {}", channel.id().toString(), channelCache.getChannelRealIp(channel));
	}
	
	public void clear() {
		symbolChannelSetMap.clear();
		channelSymoblSetMap.clear();
		klineChannelSymbolMap.clear();
		klineSymbolChannelSetMap.clear();
	}
	
} 
