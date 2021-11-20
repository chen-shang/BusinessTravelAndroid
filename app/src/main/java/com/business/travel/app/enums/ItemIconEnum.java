package com.business.travel.app.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.business.travel.app.R;

public enum ItemIconEnum {

	ItemIconEdit("", R.drawable.bill_icon_add, "编辑"),
	ItemIconJiangJin("https://gitee.com/chen-shang/business-travel-resource/raw/master/收入/奖金.svg", R.drawable.ic_spending_jiangjin, "奖金"),
	ItemIconGongZi("https://gitee.com/chen-shang/business-travel-resource/raw/master/收入/工资.svg", R.drawable.ic_spending_gongzi, "工资"),
	ItemIconLiJin("https://gitee.com/chen-shang/business-travel-resource/raw/master/收入/礼金.svg", R.drawable.ic_spending_lijin, "礼金"),
	ItemIconHongBao("https://gitee.com/chen-shang/business-travel-resource/raw/master/收入/红包.svg", R.drawable.ic_spending_hongbao, "红包"),
	ItemIconZhuanZhang("https://gitee.com/chen-shang/business-travel-resource/raw/master/收入/转账.svg", R.drawable.ic_spending_zhuanzhang, "转账"),
	ItemIconYinHangKa("https://gitee.com/chen-shang/business-travel-resource/raw/master/收入/银行卡.svg", R.drawable.ic_spending_yinhangka, "银行卡"),
	;

	private static final Map<String, ItemIconEnum> MAP = new HashMap<>();

	static {
		Arrays.stream(ItemIconEnum.values()).forEach(item -> MAP.put(item.getIconDownloadUrl(), item));
	}

	private final String iconDownloadUrl;
	private final int resourceId;
	private final String name;

	ItemIconEnum(String iconDownloadUrl, int resourceId, String name) {
		this.iconDownloadUrl = iconDownloadUrl;
		this.resourceId = resourceId;
		this.name = name;
	}

	public static ItemIconEnum ofUrl(String iconDownloadUrl) {
		return MAP.get(iconDownloadUrl);
	}

	public String getIconDownloadUrl() {
		return iconDownloadUrl;
	}

	public int getResourceId() {
		return resourceId;
	}

	public String getName() {
		return name;
	}
}
