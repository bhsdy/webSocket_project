package com.cn.websocket.entity;



public interface Command {

	public Response<?> execute(Request request);
	
}
