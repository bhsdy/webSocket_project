package com.cn.websocket.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CommandMapping {

	private String uri;
	
	private Object object;
	
	private Method method;
	
	private boolean singleExecute;
	
}
