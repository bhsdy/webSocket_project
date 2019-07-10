package com.cn.websocket.entity;

import com.cn.websocket.entity.enumEntiy.AccountEnum;
import com.cn.websocket.util.LongToStringConverter;
import com.cn.websocket.util.StringToLongConverter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.netty.channel.Channel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class PushElementDTO<T> {

	private String cmd;
	
	@JsonSerialize(using = LongToStringConverter.class)
	@JsonDeserialize(using = StringToLongConverter.class)
	private Long userId;
	
	@JsonIgnore
	private String brokerSymbol;

	private AccountEnum accountType;

	@JsonIgnore
	private boolean pushApp;

	private boolean pushAllUser;
	
	@JsonIgnore
	private Channel channel;
	
	private T data;
	
	public PushElementDTO(String cmd, Long userId, String brokerSymbol, AccountEnum accountType, boolean pushApp, T data) {
		this(cmd, userId, brokerSymbol, accountType, pushApp, false, null, data);
	}
	
	public PushElementDTO(String cmd, Long userId, String brokerSymbol, AccountEnum accountType, boolean pushApp, boolean pushAllUser, T data) {
		this(cmd, userId, brokerSymbol, accountType, pushApp, pushAllUser, null, data);
	}
	
}
