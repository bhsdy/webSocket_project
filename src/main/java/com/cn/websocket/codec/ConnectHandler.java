package com.cn.websocket.codec;

import com.cn.websocket.listener.ConnectListener;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Sharable
public class ConnectHandler extends ChannelInboundHandlerAdapter {

	private ConnectListener connectListener;
	
	public void registerListener(ConnectListener connectListener) {
		this.connectListener = connectListener;
	}
	
	@Override
	public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
		super.channelRegistered(ctx);
		if(connectListener != null) {
			connectListener.onConnect(ctx.channel());
		}
	}
	
	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		// TODO Auto-generated method stub
		super.channelInactive(ctx);
		if(connectListener != null) {
			connectListener.onDisconnect(ctx.channel());
		}
	}
	
	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		super.userEventTriggered(ctx, evt);
		if(evt instanceof IdleStateEvent) {
			 IdleStateEvent event = (IdleStateEvent) evt;
			 switch (event.state()) {
				case READER_IDLE:
					if(ctx.channel().isActive()) {
						ctx.close();
						log.warn("channel {} has long time send data, close it", ctx.channel());
					}
					break;
				default:
					break;
			}
		}
	}
	
}
