package com.cn.websocket.TcpClientTest;

import com.cn.websocket.TcpClientTest.threads.KlinePushQueue;
import com.cn.websocket.TcpClientTest.threads.QuotePushQueue;
import com.cn.websocket.component.TcpClient;
import com.cn.websocket.entity.KlineDTO;
import com.cn.websocket.entity.QuoteDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;

@Slf4j
public class QuoteCenterClient extends TcpClient {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private QuotePushQueue quotePushQueue;
	
	@Autowired
	private KlinePushQueue klinePushQueue;

	public QuoteCenterClient(String ip, int port) {
		super(ip, port);
	}

	@Override
	protected void processData(String cmd, Integer code, String msg, Integer requestId, Object data) {
		try {
			switch(cmd) {
			case "/heartbeat":
				break;
			case "/register2":
				if(code == 0) {
					log.info("quotecenter {}:{} register succ", getIp(), getPort());
				} else {
					log.info("quotecenter {}:{} register fail, after 1s try again.", getIp(), getPort());
					Thread.sleep(1000);
					register();
				}
				break;
			case "/push/quote": {
					QuoteDTO quote = objectMapper.readValue(objectMapper.writeValueAsBytes(data), QuoteDTO.class);
					quotePushQueue.addMessage(quote);
				}
				break;
			case "/push/kline": {
					KlineDTO kline = objectMapper.readValue(objectMapper.writeValueAsBytes(data), KlineDTO.class);
					klinePushQueue.addMessage(kline);
				}
				break;
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	@Override
	protected void heartbeat() {
		sendRequest("/heartbeat", null, new HashMap<>());
	}
	
	@Override
	protected void connectedCallback() {
		register();
	}
	
	private void register() {
		sendRequest("/register2", null, new HashMap<>());
	}

}
