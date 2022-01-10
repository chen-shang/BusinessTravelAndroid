package com.business.travel.app.model;

import lombok.Data;

@Data
public class BillAddModel {
	/**
	 * 消费日期
	 */
	private Long consumeDate;

	/**
	 * 消费类型
	 *
	 * @see com.business.travel.vo.enums.ConsumptionTypeEnum
	 */
	private String consumptionType;
}
