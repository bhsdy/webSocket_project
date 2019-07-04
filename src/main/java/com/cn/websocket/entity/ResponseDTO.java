package com.cn.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ResponseDTO<T> implements Serializable {

	private static final long serialVersionUID = 5681745130660852894L;

	private int code;
	
	private String msg = "";
	
	private T data;
	
}