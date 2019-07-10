package com.cn.websocket.entity;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class QuoteDTO {
	
	/**
	 * 品种代码
	 */
	private String commodityNo;
	/**
	 * 合约代码
	 */
	private String contractNo;
	private long time;
	private String timeStr;
	private String tradingDate;
	/**
	 * 今开价
	 */
	private BigDecimal open = BigDecimal.ZERO;
	/**
	 * 今收价
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
	 * 最新价
	 */
	private BigDecimal newPrice = BigDecimal.ZERO;
	/**
	 * 总成交量
	 */
	private long totalVolume;
	/** 
	 * 24小时成交量
	 */
	private int volume24;
	/**
	 * 持仓（未平仓合约仅期货有效）
	 */
	private long position;
	/**
	 * 昨日收盘价
	 */
	private BigDecimal preClose = BigDecimal.ZERO;
	/**
	 * 昨日结算价
	 */
	private BigDecimal preSettle = BigDecimal.ZERO;
	/**
	 * 今日结算价
	 */
	private BigDecimal settle = BigDecimal.ZERO;
	/**
	 * 总成交额
	 */
	private BigDecimal totalAmount = BigDecimal.ZERO;
	/**
	 * 买一价
	 */
	private BigDecimal bp1 = BigDecimal.ZERO;
	/**
	 * 买二价
	 */
	private BigDecimal bp2 = BigDecimal.ZERO;
	/**
	 * 买三价
	 */
	private BigDecimal bp3 = BigDecimal.ZERO;
	/**
	 * 买四价
	 */
	private BigDecimal bp4 = BigDecimal.ZERO;
	/**
	 * 买五价
	 */
	private BigDecimal bp5 = BigDecimal.ZERO;
	/**
	 * 卖一价
	 */
	private BigDecimal sp1 = BigDecimal.ZERO;
	/**
	 * 卖二价
	 */
	private BigDecimal sp2 = BigDecimal.ZERO;
	/**
	 * 卖三价
	 */
	private BigDecimal sp3 = BigDecimal.ZERO;
	/**
	 * 卖四价
	 */
	private BigDecimal sp4 = BigDecimal.ZERO;
	/**
	 * 卖五价
	 */
	private BigDecimal sp5 = BigDecimal.ZERO;
	/**
	 * 买一量
	 */
	private int bv1;
	/**
	 * 买二量
	 */
	private int bv2;
	/**
	 * 买三量
	 */
	private int bv3;
	/**
	 * 买四量
	 */
	private int bv4;
	/**
	 * 买五量
	 */
	private int bv5;
	/**
	 * 卖一量
	 */
	private int sv1;
	/**
	 * 卖二量
	 */
	private int sv2;
	/**
	 * 卖三量
	 */
	private int sv3;
	
	/**
	 * 卖四量
	 */
	private int sv4;
	
	/**
	 * 卖五量
	 */
	private int sv5;

}
