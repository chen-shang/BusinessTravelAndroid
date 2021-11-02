package com.business.travel.app.model;

import lombok.Data;

/**
 * @author chenshang
 * 图标信息
 */
@Data
public class ImageIconInfo {
	/**
	 * 资源id
	 */
	private int resourceId;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 是否被选中
	 */
	private boolean selected;
}
