package com.cn.websocket.codec;

import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Sharable
public class ExceptionHandle extends ChannelInboundHandlerAdapter {

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.out.println("执行异常关闭连接");
		log.warn("channel id: {} has exception to close, exception msg: {}", ctx.channel().id().asShortText(),
				cause.getMessage());
		log.error("", cause);
		ctx.close();
	}

}
