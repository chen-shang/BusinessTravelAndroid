package com.business.travel.app.model;

import java.util.List;

import lombok.Data;

/**
 * 消费项信息
 */
@Data
public class ItemIconInfo {
	/**
	 * 文件路径
	 */
	private String path;
	/**
	 * 图标信息
	 */
	private List<ImageIconInfo> imageIconInfos;
}
