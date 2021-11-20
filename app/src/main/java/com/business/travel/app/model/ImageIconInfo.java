package com.business.travel.app.model;

import lombok.Data;

/**
 * @author chenshang
 * 图标信息
 */
@Data
public class ImageIconInfo {
	/**
	 * 名称
	 */
	private String iconName;
	/**
	 * 图标地址
	 */
	private String iconDownloadUrl;
	/**
	 * 是否被选中
	 */
	private boolean selected;
}
