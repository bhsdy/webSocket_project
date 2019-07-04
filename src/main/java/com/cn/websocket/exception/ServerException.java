package com.cn.websocket.exception;

import com.cn.websocket.entity.RestStatus;

public class ServerException extends RuntimeException {

	private static final long serialVersionUID = 4401720241960993783L;

	private final int code;
	
	public ServerException(RestStatus status) {
		super(status.getMsg());
		this.code = status.getCode();
	}
	
	public ServerException(int code, String msg) {
		super(msg);
		this.code = code;
	}

	public int getCode() {
		return code;
	}
	
}
