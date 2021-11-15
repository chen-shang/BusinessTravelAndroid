package com.business.travel.app.ui.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenshang
 * 基本的数据共享模型
 * 每一个 activity 和 fragment 对象都对应一个shareData
 */
@Data
@NoArgsConstructor
public class ShareData {
	/**
	 * 共享数据的map
	 */
	private Map<Object, Object> shareMap = new ConcurrentHashMap<>();
}
