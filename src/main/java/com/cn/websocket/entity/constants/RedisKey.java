package com.cn.websocket.entity.constants;

import java.text.MessageFormat;

public class RedisKey {

	public final static String SPLIT = ":";

	public final static String FUTURES_KEY = "futures" + SPLIT;

	public final static String FUTURES_SYSTEM_KEY = FUTURES_KEY + "system" + SPLIT;

	public final static String FUTURES_SYSTEM_LIMIT_KEY = FUTURES_SYSTEM_KEY + "limit" + SPLIT;

	public final static String FUTURES_SYSTEM_LIMIT_IP_KEY = FUTURES_SYSTEM_LIMIT_KEY + "ip" + SPLIT;

	public final static String FUTURES_SYSTEM_LIMIT_USER_KEY = FUTURES_SYSTEM_LIMIT_KEY + "user" + SPLIT;

	public final static String FUTURES_SYSTEM_AMPQ_RETRY_COUNT_KEY = FUTURES_SYSTEM_KEY + "ampq" + SPLIT;

	public final static String FUTURES_SYSTEM_IMG_CODE_KEY = FUTURES_SYSTEM_KEY + "imgcode" + SPLIT;

	public final static String FUTURES_SYSTEM_VERIFYCODE = FUTURES_SYSTEM_KEY + "sms" + SPLIT + "verify:{0}";

	public final static String FUTURES_NOTICE_ALL = FUTURES_KEY + "notice" + SPLIT + "{0}" + SPLIT + "{1}" + SPLIT + "all";

	public final static String FUTURES_NOTICE_UNREAD = FUTURES_KEY + "notice" + SPLIT + "{0}" + SPLIT + "{1}" + SPLIT + "unread";

	public final static String FUTURES_RESOURCE_KEY = FUTURES_KEY + "resource";

	public final static String FUTURES_RESOURCE_COMMODITY_KEY = FUTURES_RESOURCE_KEY + SPLIT + "commodity:{0}";

	public final static String FUTURES_RESOURCE_CONTRACT_KEY = FUTURES_RESOURCE_KEY + SPLIT + "contract:{0}";

	public final static String FUTURES_RESOURCE_CURRENCY_KEY = FUTURES_RESOURCE_KEY + SPLIT + "currency:{0}";

	public final static String FUTURES_RESOURCE_STRATEGY_KEY = FUTURES_RESOURCE_KEY + SPLIT + "strategy:{0}";
	/** TICK数据 "futures:trade:quote:品种合约" */
	public final static String FUTURES_TRADE_QUOTE_KEY = FUTURES_KEY + "quote" + SPLIT + "{0}";

	public final static String FUTURES_USER_KEY = FUTURES_KEY + "user" + SPLIT;

	public final static String FUTURES_USER_TOKEN_KEY = FUTURES_USER_KEY + "token" + SPLIT;
	
	public final static String FUTURES_USER_PERMISSION_KEY = FUTURES_USER_KEY + "permission" + SPLIT;

	public final static String FUTURES_USER_PERMISSION_INTERFACE_KEY = FUTURES_USER_PERMISSION_KEY + "interface" + SPLIT;

	public final static String FUTURES_USER_PERMISSION_USER_KEY = FUTURES_USER_PERMISSION_KEY + "user" + SPLIT;

	public final static String FUTURES_USER_SERVER_IP_KEY = FUTURES_USER_KEY + "loginServer";

	public final static String FUTURES_USER_DEPOSIT_WARN_KEY = FUTURES_USER_KEY + "deposit"+ SPLIT + "warn" + SPLIT + "{0}" + SPLIT + "{1}";
	
	public final static String FUTURES_USER_OPTIONAL_CONTRACT_KEY = FUTURES_USER_KEY + SPLIT + "optionalContract:{0}";

	public final static String FUTURES_QUOTE_KEY = FUTURES_KEY + "quote";
	
	public final static String FUTURES_QUOTE_TICK_KEY = FUTURES_QUOTE_KEY + SPLIT + "tick" + SPLIT + "{0}";
	
	public final static String FUTURES_QUOTE_KLINE_KEY = FUTURES_QUOTE_KEY + SPLIT + "kline" + SPLIT + "{0}" + SPLIT + "{1}";
	
	public final static String FUTURES_QUOTE_MINUTE_KEY = FUTURES_QUOTE_KEY + SPLIT + "minute" + SPLIT + "{0}" + SPLIT + "{1}";
	
	public static String getKey(String key, Object... args) {
		return MessageFormat.format(key, args);
	}

}
