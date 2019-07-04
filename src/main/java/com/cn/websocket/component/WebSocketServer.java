package com.cn.websocket.component;

import com.cn.websocket.codec.ConnectHandler;
import com.cn.websocket.codec.ExceptionHandle;
import com.cn.websocket.codec.TextWebSocketFrameHandler;
import com.cn.websocket.codec.WebSocketOutHandler;
import com.cn.websocket.listener.ConnectListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Slf4j
public class WebSocketServer {

    private String websocketHost;

    private int websocketPort;

    private boolean openProxyProtocol;
    
    @Value("${websocket.ioThreadNum:4}")
    private int websocketIoThreadNum;

    @Value("${websocket.backlog:1024}")
    private int websocketBacklog;
    
    @Value("${websocket.tcpWriteBufferHighMark:512}")
    private int tcpWriteBufferHighMark;
    
    @Value("${websocket.tcpWriteBufferHighMark:256}")
    private int tcpWriteBufferLowMark;
    
    @Value("${websocket.readIdleTime:60}")
    private int readIdleTime;
    
	private Channel websocketChannel;

	private EventLoopGroup websocketBossGroup;

	private EventLoopGroup websocketWorkerGroup;

	@Autowired
	private TextWebSocketFrameHandler textWebSocketFrameHandler;
	
	@Autowired
	private WebSocketOutHandler webSocketOutHandler;
	
    @Autowired
    private ExceptionHandle exceptionHandler;
    
	private ConnectHandler connectHandler = new ConnectHandler();
	
	public WebSocketServer(String ip, int port) {
    	this(ip, port, false);
    }
    
    public WebSocketServer(String ip, int port, boolean openProxyProtocol) {
    	this.websocketHost = ip;
    	this.websocketPort = port;
    	this.openProxyProtocol = openProxyProtocol;
    }
	
	@PostConstruct
    public void start() throws InterruptedException {
    	log.info("begin to start webstock server");
    	startServer();
    }
	
	public void registerListener(ConnectListener listener) {
    	connectHandler.registerListener(listener);
    }
	
	private void startServer() throws InterruptedException {
		//初始化用于Acceptor的主"线程池"以及用于I/O工作的从"线程池"；
		websocketBossGroup = new NioEventLoopGroup();
		websocketWorkerGroup = new NioEventLoopGroup(websocketIoThreadNum);
		//初始化ServerBootstrap实例， 此实例是netty服务端应用开发的入口，也是本篇介绍的重点， 下面我们会深入分析；
		ServerBootstrap serverBootstrap = new ServerBootstrap();
		//通过ServerBootstrap的group方法，设置（1）中初始化的主从"线程池"；
		serverBootstrap.group(websocketBossGroup, websocketWorkerGroup)
				.channel(NioServerSocketChannel.class) //指定通道channel的类型，由于是服务端，故而是NioServerSocketChannel；
				.option(ChannelOption.SO_BACKLOG, websocketBacklog) //配置ServerSocketChannel的选项
				// ???childOption
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.TCP_NODELAY, true) //配置子通道也就是SocketChannel的选项
				.childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, 
                		new WriteBufferWaterMark(tcpWriteBufferLowMark * 1024, tcpWriteBufferHighMark * 1024))
				.childHandler(new ChannelInitializer<SocketChannel>() {
					@Override
					protected void initChannel(SocketChannel socketChannel) throws Exception { //设置子通道也就是SocketChannel的处理器， 其内部是实际业务开发的"主战场"（此处不详述，后面的系列会进行深入分析）
						ChannelPipeline pipeline = socketChannel.pipeline();
						pipeline.addLast("idleHandler", new IdleStateHandler(readIdleTime, 0, 0, TimeUnit.SECONDS));
						if(openProxyProtocol) {
                    		pipeline.addLast("proxyHandler", new HAProxyMessageDecoder());
                    	}
						pipeline.addLast(connectHandler);
						pipeline.addLast(new HttpServerCodec());
						pipeline.addLast(new HttpObjectAggregator(10240));
						pipeline.addLast(new ChunkedWriteHandler());
						pipeline.addLast(webSocketOutHandler);
						pipeline.addLast(new WebSocketServerProtocolHandler("/ws"));
						pipeline.addLast(textWebSocketFrameHandler);
                        pipeline.addLast("exceptionHandler", exceptionHandler);
					}
				});
		websocketChannel = serverBootstrap.bind(websocketHost, websocketPort).sync().channel(); //绑定并侦听某个端口
		log.info("启动成功：websocket server listening on port " + websocketHost + ":" + websocketPort + " and ready for connections...");
	}

	@PreDestroy
    public void stop() {
        log.info("destroy server resources");
        if (websocketChannel == null) {
            log.error("server channel is null");
        }
        websocketChannel.closeFuture().syncUninterruptibly();
        websocketBossGroup.shutdownGracefully();
        websocketWorkerGroup.shutdownGracefully();
    }
	
}
