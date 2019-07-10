package com.cn.websocket.config;

import com.cn.websocket.component.NettyServer;
import com.cn.websocket.component.WebSocketServer;
import com.cn.websocket.listener.ChannelConnectListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InitConfig implements InitializingBean {
	
	@Value("${tcp.host:127.0.0.1}")
    private String tcpHost;

	@Value("${tcp.port:9000}")
    private int tcpPort;
	
	@Value("${tcp.openProxyProtocol:false}")
	private boolean tcpOpenProxyProtocol;
	
	@Value("${websocket.host:127.0.0.1}")
	private String websocketHost;

	@Value("${websocket.port:9001}")
	private int websocketPort;

	@Value("${websocket.openProxyProtocol:false}")
	private boolean websocketOpenProxyProtocol;
	
	@Bean
	public NettyServer nettyServer(@Autowired ChannelConnectListener listener) {
		NettyServer nettyServer = new NettyServer(tcpHost, tcpPort, tcpOpenProxyProtocol);
		nettyServer.registerListener(listener);
		return nettyServer;
	}
	
	@Bean
	public WebSocketServer webSocketServer(@Autowired ChannelConnectListener listener) {
		WebSocketServer webSocketServer = new WebSocketServer(websocketHost, websocketPort, websocketOpenProxyProtocol);
		webSocketServer.registerListener(listener);
		return webSocketServer;
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {

	}
	
}
