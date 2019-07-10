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
        //服务端要建立两个group，一个负责接收客户端的连接，一个负责处理数据传输
        //连接处理group
    	bossGroup = new NioEventLoopGroup();
        //事件处理group
        workerGroup = new NioEventLoopGroup(ioThreadNum);
    	ServerBootstrap serverBootstrap = new ServerBootstrap();
        // 绑定处理group
        serverBootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                //保持连接数
                .option(ChannelOption.SO_BACKLOG, backlog)
                //保持连接
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                //有数据立即发送
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.WRITE_BUFFER_WATER_MARK, 
                		new WriteBufferWaterMark(tcpWriteBufferLowMark * 1024, tcpWriteBufferHighMark * 1024))
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        // 增加任务处理
                    	ChannelPipeline pipeline = socketChannel.pipeline();
                    	pipeline.addLast("idleHandler", new IdleStateHandler(readIdleTime, 0, 0, TimeUnit.SECONDS));
                    	if(openProxyProtocol) {
                    		pipeline.addLast("proxyHandler", new HAProxyMessageDecoder());
                    	}
                    	pipeline.addLast("connectHandler", connectHandler);
                        // 自定义长度解码器解决TCP黏包问题
                        // maxFrameLength 最大包字节大小，超出报异常
                        // lengthFieldOffset 长度字段的偏差
                        // lengthFieldLength 长度字段占的字节数
                        // lengthAdjustment 添加到长度字段的补偿值
                        // initialBytesToStrip 从解码帧中第一次去除的字节数
                        pipeline.addLast("decoder", new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                        pipeline.addLast("dispatcherHandler", dispatcherHandler);
                        // LengthFieldPrepender编码器，它可以计算当前待发送消息的二进制字节长度，将该长度添加到ByteBuf的缓冲区头中
                        pipeline.addLast("lengthFieldPrepender", lengthFieldPrepender);
                        // 序列化工具
                        pipeline.addLast("gzipEncoder", gZipEncoder);
                        pipeline.addLast("jsonEncoder", jsonEncoder);
                        pipeline.addLast("exceptionHandler", exceptionHandler);
                    }
                });
        //等待服务监听端口关闭,就是由于这里会将线程阻塞，导致无法发送信息，所以我这里开了线程
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
