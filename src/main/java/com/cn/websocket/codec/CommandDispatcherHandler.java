package com.cn.websocket.codec;

import com.cn.websocket.entity.SpringContext;
import com.cn.websocket.threads.CommandMessageQueue;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Component;

@Component
@Sharable
public class CommandDispatcherHandler extends ChannelInboundHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		System.out.println("webSocket执行---CommandDispatcherHandler");
		ByteBuf buf = (ByteBuf) msg;
		byte[] data = new byte[buf.readableBytes()];
		buf.readBytes(data);
		buf.release();
		ImmutablePair<Channel, byte[]> pair = new ImmutablePair<>(ctx.channel(), data);
		SpringContext.getBean("commandMessageQueue", CommandMessageQueue.class).addMessage(pair);
	}
	
}
