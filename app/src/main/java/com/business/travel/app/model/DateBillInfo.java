package com.business.travel.app.model;

import java.util.List;

import com.business.travel.app.dal.entity.Bill;
import lombok.Builder;
import lombok.Data;

@Data
public class DateBillInfo {
	private String date;
	private List<Bill> billList;
}
