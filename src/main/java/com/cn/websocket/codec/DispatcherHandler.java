package com.cn.websocket.codec;

import com.cn.websocket.entity.ChannelCache;
import com.cn.websocket.threads.DispatcherMessageQueue;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.haproxy.HAProxyMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.InetSocketAddress;

@Slf4j
@Component
@Sharable
public class DispatcherHandler extends ChannelInboundHandlerAdapter {

	@Autowired
	private DispatcherMessageQueue queue;

	@Autowired
	private ChannelCache channelCache;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("接收客户端数据");
		byte[] data = null;
		if (msg instanceof HAProxyMessage) {
			HAProxyMessage message = (HAProxyMessage) msg;
			channelCache.setChannelRealAddress(ctx.channel(),
					InetSocketAddress.createUnresolved(message.sourceAddress(), message.sourcePort()));
			log.warn("source: {}:{}, destination: {}:{}, version:{}", message.sourceAddress(), message.sourcePort(),
					message.destinationAddress(), message.destinationPort(), message.proxiedProtocol().name());
		} else {
			ByteBuf buf = (ByteBuf) msg;
			data = new byte[buf.readableBytes()];
			buf.readBytes(data);
			buf.release();
			ImmutablePair<Channel, byte[]> pair = new ImmutablePair<>(ctx.channel(), data);
			queue.addMessage(pair);

		}
	}

}
