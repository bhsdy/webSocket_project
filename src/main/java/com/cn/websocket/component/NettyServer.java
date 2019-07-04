package com.cn.websocket.component;

import com.cn.websocket.codec.*;
import com.cn.websocket.listener.ConnectListener;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.haproxy.HAProxyMessageDecoder;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.concurrent.TimeUnit;

@Slf4j
public class NettyServer {

    private String host;

    private int port;
	
    private boolean openProxyProtocol;
    
    @Value("${tcp.ioThreadNum:4}")
    private int ioThreadNum;

    @Value("${tcp.backlog:1024}")
    private int backlog;
    
    @Value("${tcp.tcpWriteBufferHighMark:512}")
    private int tcpWriteBufferHighMark;
    
    @Value("${tcp.tcpWriteBufferHighMark:256}")
    private int tcpWriteBufferLowMark;
    
    @Value("${tcp.readIdleTime:60}")
    private int readIdleTime;
    
    @Autowired
    private GZipEncoder gZipEncoder;
    
    @Autowired
    private JsonEncoder jsonEncoder;
    
    @Autowired
    private LengthFieldPrepender lengthFieldPrepender;
    
    @Autowired
    private ExceptionHandle exceptionHandler;
    
    private ConnectHandler connectHandler = new ConnectHandler();
    
    @Autowired
    private DispatcherHandler dispatcherHandler;
    
    private Channel channel;
    
    private EventLoopGroup bossGroup;
    
    private EventLoopGroup workerGroup;
    
    public NettyServer(String ip, int port) {
    	this(ip, port, false);
    }
    
    public NettyServer(String ip, int port, boolean openProxyProtocol) {
    	this.host = ip;
    	this.port = port;
    	this.openProxyProtocol = openProxyProtocol;
    }
    
    public void registerListener(ConnectListener listener) {
    	connectHandler.registerListener(listener);
    }
    
    @PostConstruct
    public void start() throws InterruptedException {
    	log.info("begin to start rpc server");
    	startServer();
    }
    
    private void startServer() throws InterruptedException {
    	bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup(ioThreadNum);
    	ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, backlog)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, 
                		new WriteBufferWaterMark(tcpWriteBufferLowMark * 1024, tcpWriteBufferHighMark * 1024))
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                    	ChannelPipeline pipeline = socketChannel.pipeline();
                    	pipeline.addLast("idleHandler", new IdleStateHandler(readIdleTime, 0, 0, TimeUnit.SECONDS));
                    	if(openProxyProtocol) {
                    		pipeline.addLast("proxyHandler", new HAProxyMessageDecoder());
                    	}
                    	pipeline.addLast("connectHandler", connectHandler);
                        pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        pipeline.addLast("dispatcherHandler", dispatcherHandler);
                        pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
                        pipeline.addLast("gzipEncoder", gZipEncoder);
                        pipeline.addLast("jsonEncoder", jsonEncoder);
                        pipeline.addLast("exceptionHandler", exceptionHandler);
                    }
                });
        channel = serverBootstrap.bind(host,port).sync().channel();
        log.info("启动成功：NettyRPC server listening on port " + host + ":" + port + " and ready for connections...");
    }
    
    
    @PreDestroy
    public void stop() {
        log.info("destroy server resources");
        if (channel == null) {
            log.error("server channel is null");
        }
        channel.closeFuture().syncUninterruptibly();
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
    
}
