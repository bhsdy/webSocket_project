package com.cn.websocket.component;

import com.cn.websocket.codec.ExceptionHandle;
import com.cn.websocket.util.GZipUtil;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public abstract class TcpClient {

	private final String ip;

	private final int port;
	
	private EventLoopGroup workerGroup;

	private Channel channel;
	
	private static ObjectMapper objectMapper;

	static {
		objectMapper = new ObjectMapper();
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		JavaTimeModule javaTimeModule = new JavaTimeModule();
		javaTimeModule.addSerializer(LocalDate.class, new LocalDateSerializer(DateTimeFormatter.ISO_DATE));
		javaTimeModule.addDeserializer(LocalDate.class, new LocalDateDeserializer(DateTimeFormatter.ISO_DATE));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
		javaTimeModule.addSerializer(LocalTime.class, new LocalTimeSerializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
		javaTimeModule.addDeserializer(LocalTime.class, new LocalTimeDeserializer(DateTimeFormatter.ofPattern("HH:mm:ss")));
		objectMapper.registerModule(javaTimeModule);
	}
	
	private AtomicInteger requestGenerator = new AtomicInteger();
	
	public TcpClient(String ip, int port) {
		this.ip = ip;
		this.port = port;
	}
	
	public void connect() throws InterruptedException {
		workerGroup = new NioEventLoopGroup();
		Bootstrap bootstrap = new Bootstrap();
		bootstrap.group(workerGroup);
		bootstrap.channel(NioSocketChannel.class);
		bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
		bootstrap.handler(new ChannelInitializer<SocketChannel>() {
			@Override
			public void initChannel(SocketChannel ch) throws Exception {
				initChannelPipeline(ch);
			}
		});
		ChannelFuture f = bootstrap.connect(ip, port);
		f.addListener(new ChannelFutureListener() {
			@Override
			public void operationComplete(ChannelFuture future) throws Exception {
				if(!future.isSuccess()) {
					log.info("connect {}:{} fail, try again", ip, port);
					EventLoop loop = future.channel().eventLoop();
					loop.schedule(new Runnable() {
						
						@Override
						public void run() {
							try {
								reconnect();
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
					}, 1, TimeUnit.SECONDS);
				} else {
					log.info("connect {}:{} succ", ip, port);
				}
			}
		});
		channel = f.sync().channel();
	}

	public void initChannelPipeline(SocketChannel ch) throws Exception {
		ChannelPipeline pipeline = ch.pipeline();
		pipeline.addLast(new IdleStateHandler(10, 5, 0, TimeUnit.SECONDS));
		pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4, false));
		pipeline.addLast(new ChannelInboundHandlerAdapter() {
			
			@Override
			public void channelActive(ChannelHandlerContext ctx) throws Exception {
				log.warn("channel {}:{} active, channel:{}", ip, port, ctx.channel().toString());
				channel = ctx.channel();
				connectedCallback();
				super.channelActive(ctx);
			}
			
			@Override
			public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
				ByteBuf buf = (ByteBuf) msg;
				byte[] data = new byte[buf.readableBytes()];
				buf.readBytes(data);
				buf.release();
				
				String str = GZipUtil.uncompressToString(data);
				ServerData serverData = objectMapper.readValue(str, ServerData.class);
				processData(serverData.cmd, serverData.code, serverData.msg, serverData.requestId, serverData.data);
			}
			
			@Override
			public void channelInactive(ChannelHandlerContext ctx) throws Exception {
				super.channelInactive(ctx);
				channelDisconnect(ctx);
			}
			
			@Override
			public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
				super.userEventTriggered(ctx, evt);
				if(evt instanceof IdleStateEvent) {
					 IdleStateEvent event = (IdleStateEvent) evt;
					 switch (event.state()) {
						case READER_IDLE:
							disconnect();
							break;
						case WRITER_IDLE:
							heartbeat();
							break;
						default:
							break;
					}
				}
			}
			
		});
		pipeline.addLast(new LengthFieldPrepender(4, false));
		pipeline.addLast(new MessageToMessageEncoder<Object>() {

			private ObjectMapper objectMapper = new ObjectMapper();
			
			@Override
			protected void encode(ChannelHandlerContext ctx, Object msg, List<Object> out) 
					throws Exception {
				if (msg == null) {
		            return;
		        }
				out.add(ByteBufUtil.encodeString(ctx.alloc(), CharBuffer.wrap(objectMapper.writeValueAsString(msg)), Charset.forName("UTF-8")));
			}
			
		});
		pipeline.addLast(new ExceptionHandle());
	}
	
	public String getIp() {
		return ip;
	}
	
	public int getPort() {
		return port;
	}
	
	protected void channelDisconnect(ChannelHandlerContext ctx) throws Exception {
		log.warn("channel {}:{} inactive, begin reconnect", ip, port);
		reconnect();
	}
	
	protected void reconnect() throws InterruptedException {
		disconnect();
		connect();
	}
	
	protected void connectedCallback() {
		
	}

	public void disconnect() {
		if(channel != null) {
			if(channel.isActive()) {
				channel.close();
			}
		}
		if(!workerGroup.isShutdown()) {
			workerGroup.shutdownGracefully();
		}
	}

	public void sendRequest(String cmd, String token, Object param) {
		sendRequest(this.channel, cmd, token, param);
	}
	
	public void sendRequest(Channel channel, String cmd, String token, Object param) {
		Map<String, Object> requestData = new HashMap<>();
		requestData.put("cmd", cmd);
		requestData.put("token", token);
		requestData.put("requestId", requestGenerator.incrementAndGet());
		requestData.put("param", param);
		if(channel != null && channel.isActive()) {
			channel.writeAndFlush(requestData);
		}
	}

	protected void heartbeat() {};
	
	protected abstract void processData(String cmd, Integer code, String msg, Integer requestId, Object data);
	
	@Data
	public static class ServerData {
		
		private String cmd;
		
		private Integer code;
		
		private String msg;
		
		private Integer requestId;
		
		private Object data;
		
	}
	
}
