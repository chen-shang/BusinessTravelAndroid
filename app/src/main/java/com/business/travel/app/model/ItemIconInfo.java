package com.business.travel.app.model;

import java.util.List;

import lombok.Data;

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

	@Data
	public static class ImageIconInfo {
		/**
		 * 名称
		 */
		private String name;
		/**
		 * 图标地址
		 */
		private String iconDownloadUrl;
		/**
		 * 是否被选中
		 */
		private boolean selected;
	}
}
