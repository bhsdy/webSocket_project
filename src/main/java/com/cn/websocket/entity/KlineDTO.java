package com.cn.websocket.entity;

import com.cn.websocket.entity.enumEntiy.KlineEnum;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.math.BigDecimal;

@Data
public class KlineDTO {

	/**
	 * id
	 */
	@Id
	private long id;
	
	/**
	 * 品种
	 */
	private String commodityNo;
	
	/**
	 * 合约
	 */
	private String contractNo;
	
	/**
	 * 时间
	 */
	private long time;
	
	private String timeStr;
	
	/**
	 * k线类型
	 */
	private KlineEnum type;
	
	/**
	 * 开盘价
	 */
	private BigDecimal open = BigDecimal.ZERO;
	
	/**
	 * 收盘价
	 */
	private BigDecimal close = BigDecimal.ZERO;
	
	/**
	 * 最高价
	 */
	private BigDecimal high = BigDecimal.ZERO;
	
	/**
	 * 最低价
	 */
	private BigDecimal low = BigDecimal.ZERO;
	
	/**
	 * 成交金额
	 */
	private BigDecimal amount = BigDecimal.ZERO;

	/**
	 * 成交量
	 */
	private long volume;
	
	/**
	 * 总成交量
	 */
	private long totalVolume;
	
	/**
	 * 总成交金额
	 */
	private BigDecimal totalAmount = BigDecimal.ZERO;
	
}
