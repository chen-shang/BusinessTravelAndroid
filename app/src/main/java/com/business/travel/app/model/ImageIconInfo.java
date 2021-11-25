package com.business.travel.app.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 图片图标信息
 */
@Data
public class ImageIconInfo {
	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 图标地址
	 */
	private String iconDownloadUrl;
	/**
	 * 消费项图标名称
	 */
	private String iconName;
	/**
	 * @see com.business.travel.app.enums.ItemTypeEnum
	 */
	private String itemType;
	/**
	 * 顺序id
	 */
	private Long sortId;
	/**
	 * 是否被选中
	 */
	private boolean selected;
}
