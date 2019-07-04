package com.cn.websocket.codec;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Sharable
public class JsonEncoder extends MessageToMessageEncoder<Object> {

	@Autowired
	private ObjectMapper objectMapper;
	
	@Override
	protected void encode(ChannelHandlerContext ctx, Object object, List<Object> out) throws Exception {
		if (object == null) {
            return;
        }
		out.add(objectMapper.writeValueAsString(object));
	}
	
}
