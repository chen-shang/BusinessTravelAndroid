package com.business.travel.app.enums;

import com.business.travel.app.R;

/**
 * @author chenshang
 */
public enum IconEnum {
	bill_icon_bonus(R.drawable.bill_icon_bonus, "奖金"),
	bill_icon_business(R.drawable.bill_icon_business, "开会"),
	bill_icon_clothes(R.drawable.bill_icon_clothes, "衣物"),
	bill_icon_daily(R.drawable.bill_icon_daily, "日会"),
	bill_icon_donate(R.drawable.bill_icon_donate, "饮料"),
	bill_icon_entertainment(R.drawable.bill_icon_entertainment, ""),
	bill_icon_food(R.drawable.bill_icon_food, "早餐"),
	bill_icon_fuel(R.drawable.bill_icon_fuel, "午餐"),
	bill_icon_houserent(R.drawable.bill_icon_houserent, "晚餐"),
	bill_icon_intrest(R.drawable.bill_icon_intrest, "宵夜"),
	bill_icon_makeup(R.drawable.bill_icon_makeup, "加油"),
	bill_icon_medicine(R.drawable.bill_icon_medicine, "酒水"),
	bill_icon_other(R.drawable.bill_icon_other, "其他"),
	bill_icon_phone(R.drawable.bill_icon_phone, "电话"),
	bill_icon_salary(R.drawable.bill_icon_salary, "工资"),
	bill_icon_shopping(R.drawable.bill_icon_shopping, "购物"),
	bill_icon_smoke_wine(R.drawable.bill_icon_smoke_wine, "烟酒"),
	bill_icon_study(R.drawable.bill_icon_study, "学习"),
	bill_icon_tour(R.drawable.bill_icon_tour, "教程"),
	bill_icon_traffic(R.drawable.bill_icon_traffic, "交通");

	private final int resourceId;
	private final String description;

	IconEnum(int resourceId, String description) {
		this.resourceId = resourceId;
		this.description = description;
	}

	public int getResourceId() {
		return resourceId;
	}

	public String getDescription() {
		return description;
	}
}
