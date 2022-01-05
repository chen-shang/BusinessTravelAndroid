package com.business.travel.app.constant;

import com.business.travel.app.model.Config;
import lombok.Getter;
import lombok.Setter;

/**
 * app全局的配置都在这里设置
 */
public final class AppConfig {
	@Getter
	@Setter
	private static Config config;
}
