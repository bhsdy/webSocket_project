package com.cn.websocket.codec;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Sharable
public class TextWebSocketFrameHandler extends ChannelInboundHandlerAdapter {

//	@Autowired
//	private DispatcherMessageQueue queue;
	
    @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    	TextWebSocketFrame frame = (TextWebSocketFrame)msg;
    	try {
			ImmutablePair<Channel, byte[]> pair = new ImmutablePair<>(ctx.channel(), frame.text().getBytes());
//			queue.addMessage(pair);
    	} finally {
    		frame.release();
    	}
	}
    
}