package com.cn.websocket.codec;

import com.cn.websocket.util.GZipUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Sharable
public class WebSocketOutHandler extends ChannelOutboundHandlerAdapter {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
		System.out.println("webSocket开始连接执行---WebSocketOutHandler");
		if(!ctx.channel().isActive()) {
			log.warn("channel id:{} is not active but still write", ctx.channel().id().asShortText());
		}
		if(msg instanceof DefaultFullHttpResponse) {
			super.write(ctx, msg, promise);
			return;
		}
		super.write(ctx, new BinaryWebSocketFrame(Unpooled.wrappedBuffer(GZipUtil.compress(objectMapper.writeValueAsString(msg)))), promise);
	}
	
}
