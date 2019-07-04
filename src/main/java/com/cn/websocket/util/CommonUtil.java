package com.cn.websocket.util;

import com.cn.websocket.entity.ResponseDTO;
import com.cn.websocket.entity.RestError;
import com.cn.websocket.entity.RestStatus;
import com.cn.websocket.exception.ServerException;

import java.util.HashMap;
import java.util.Map;

public class CommonUtil {

	public static Map<String, Object> buildResponseData(String cmd, ResponseDTO<?> responseDTO, int requestId) {
		Map<String, Object> map = new HashMap<>();
		map.put("code", responseDTO.getCode());
		map.put("msg", responseDTO.getMsg());
		map.put("cmd", cmd);
		map.put("requestId", requestId);
		map.put("data", responseDTO.getData());
		return map;
	}
	
	public static Map<String, Object> buildResponseData(String cmd, RestError restError, int requestId) {
		Map<String, Object> map = new HashMap<>();
		map.put("code", restError.getCode());
		map.put("msg", restError.getMsg());
		map.put("cmd", cmd);
		map.put("requestId", requestId);
		return map;
	}
	
	public static Map<String, Object> buildResponseData(String cmd, RestStatus status, Object responseObject, int requestId) {
		Map<String, Object> map = new HashMap<>();
		map.put("code", status.getCode());
		map.put("msg", status.getMsg());
		map.put("cmd", cmd);
		map.put("requestId", requestId);
		if (responseObject != null) {
			map.put("data", responseObject);
		}
		return map;
	}

	public static Map<String, Object> buildResponseData(String cmd, RestStatus status, int requestId) {
		return buildResponseData(cmd, status, null, requestId);
	}

	public static Map<String, Object> buildResponseData(String cmd, ServerException e, int requestId) {
		Map<String, Object> map = new HashMap<>();
		map.put("code", e.getCode());
		map.put("msg", e.getMessage());
		map.put("requestId", requestId);
		map.put("cmd", cmd);
		return map;
	}
	
}
