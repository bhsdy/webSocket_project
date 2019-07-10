package com.cn.websocket.threads;

import com.cn.websocket.entity.Command;
import com.cn.websocket.entity.Request;
import com.cn.websocket.entity.Response;
import com.cn.websocket.entity.RestStatus;
import com.cn.websocket.exception.ServerException;
import com.cn.websocket.util.CommonUtil;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class CommandRunnable implements Runnable {

	private Command command;

	private Request request;

	public CommandRunnable(Command command, Request request) {
		this.command = command;
		this.request = request;
	}

	public void run() {
		Channel channel = request.getChannel();
		try {
			Response<?> response = command.execute(request);
			channel.writeAndFlush(CommonUtil.buildResponseData(request.getCmd(), response.getStatus(),
					response.getBody(), request.getRequestId()));
		} catch (ServerException e) {
			channel.writeAndFlush(CommonUtil.buildResponseData(request.getCmd(), e, request.getRequestId()));
		} catch (Exception e) {
			channel.writeAndFlush(
					CommonUtil.buildResponseData(request.getCmd(), RestStatus.ERROR_SYSTEM, request.getRequestId()));
			log.error("", e);
		}
	}

}
