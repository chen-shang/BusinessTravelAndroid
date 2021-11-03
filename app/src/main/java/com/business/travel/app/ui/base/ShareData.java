package com.business.travel.app.ui.base;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author chenshang
 */
@Data
@NoArgsConstructor
public class ShareData {
	private Map<Object, Object> shareMap = new ConcurrentHashMap<>();
}
