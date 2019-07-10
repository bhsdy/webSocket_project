package com.cn.websocket.entity.enumEntiy;

import java.util.concurrent.TimeUnit;

public enum KlineEnum {

	MINUTE(1, TimeUnit.MINUTES),
	
	MINUTE3(3, TimeUnit.MINUTES),

	MINUTE5(5, TimeUnit.MINUTES),
	
	MINUTE15(15, TimeUnit.MINUTES),
	
	MINUTE30(30, TimeUnit.MINUTES),
	
	HOUR(1, TimeUnit.HOURS),
	
	HOUR2(2, TimeUnit.HOURS),
	
	HOUR4(4, TimeUnit.HOURS),
	
	DAY(1, TimeUnit.DAYS);

	private int amount;
	
	private TimeUnit timeUnit;
	
	private KlineEnum(int amount, TimeUnit timeUnit) {
		this.amount = amount;
		this.timeUnit = timeUnit;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}
	
}
