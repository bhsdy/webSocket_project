package com.cn.websocket.entity;

public class Response<T> {

	private final T body;
	
	private final RestStatus status;
	
	public Response(T body, RestStatus status) {
		this.body = body;
		this.status = status;
	}
	
	public Response(T body) {
		this(body, RestStatus.SUCCESS);
	}
	
	public Response(RestStatus status) {
		this(null, status);
	}

	public T getBody() {
		return body;
	}

	public RestStatus getStatus() {
		return status;
	}
	
	public static Response<?> ok() {
		return new Response<>(null, RestStatus.SUCCESS);
	}
	
	public static Response<?> ok(Object object) {
		return new Response<>(object, RestStatus.SUCCESS);
	}
	
}
