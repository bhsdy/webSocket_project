package com.cn.websocket.codec;

import com.cn.websocket.util.GZipUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Sharable
public class GZipEncoder extends MessageToMessageEncoder<String> {

	@Override
	protected void encode(ChannelHandlerContext ctx, String message, List<Object> out) throws Exception {
		if (message == null) {
            return;
        }
        out.add(Unpooled.wrappedBuffer(GZipUtil.compress(message)));
	}
	
}
