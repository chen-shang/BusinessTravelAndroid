package com.business.travel.app.model;

import java.util.List;

import com.business.travel.app.dal.entity.Bill;
import lombok.Data;

@Data
public class DateBillInfo {
	/**
	 * 账单日期
	 */
	private Long date;
	/**
	 * 该日期下的账单
	 */
	private List<Bill> billList;

	public DateBillInfo(Long date, List<Bill> billList) {
		this.date = date;
		this.billList = billList;
	}
}
