package com.cn.websocket.threads;

import com.cn.websocket.entity.*;
import com.cn.websocket.util.CommonUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class CommandMessageQueue extends MessageQueue<ImmutablePair<Channel, byte[]>> {

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	@Qualifier("coreThreadExecutor")
	private StandardThreadExecutor executor;
	
	@PostConstruct
	public void init() {
		executor.start();
		start();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute(ImmutablePair<Channel, byte[]> pair) {
		Channel channel = pair.left;
		try {
			String requestStr = new String(pair.right, "UTF-8");
			Map<String, Object> paramMap = objectMapper.readValue(requestStr, Map.class);
			String cmd = (String) paramMap.get("cmd");
			Command command = null;
			try {
				command = SpringContext.getBean(cmd + "Command", Command.class);
			} catch (Exception e) {
				channel.writeAndFlush(CommonUtil.buildResponseData(cmd, RestStatus.ERROR_NO_COMMAND,
						(Integer) paramMap.get("requestId")));
			}
			if (command != null) {
				Map<String, Object> param = null;
				if(paramMap.containsKey("param")) {
					param = (Map<String, Object>)paramMap.get("param");
				} else {
					param = new HashMap<>();
				}
				Request request = new Request((String) paramMap.get("cmd"), (String)paramMap.get("sessionId"),
						(Integer) paramMap.get("requestId"), param, pair.left);
				executor.execute(new CommandRunnable(command, request));
			} else {
				channel.writeAndFlush(CommonUtil.buildResponseData((String) paramMap.get("cmd"), RestStatus.ERROR_NO_COMMAND,
						(Integer) paramMap.get("requestId")));
			}
		} catch (Exception e) {
			channel.writeAndFlush(CommonUtil.buildResponseData("", RestStatus.ERROR_PARAM_FORMAT, Integer.MAX_VALUE));
			log.error("", e);
		}
		
	}

}
