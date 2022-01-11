package com.business.travel.app.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.business.travel.app.R;

/**
 * 本地图标映射
 */
public enum ItemIconEnum {
	ItemIconEdit("https://gitee.com/api/v5/repos/chen-shang/business-travel-resource/contents/icon/COMMON/设置.svg", R.drawable.ic_base_setting, "编辑"),
	ItemIconMe("https://gitee.com/api/v5/repos/chen-shang/business-travel-resource/contents/icon/COMMON/我.svg", R.drawable.ic_me, "我"),
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
