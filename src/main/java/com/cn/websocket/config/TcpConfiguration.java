package com.cn.websocket.config;

import io.netty.handler.codec.LengthFieldPrepender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TcpConfiguration {

	@Bean
	public LengthFieldPrepender lengthFieldPrepender() {
		return new LengthFieldPrepender(4, false);
	}
	
}
