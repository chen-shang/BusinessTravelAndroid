package com.business.travel.app.model;

import lombok.Data;

@Data
public class Config {
    /**
     * 图标缓存的时间
     */
    private Long iconTtl;

    /**
     * 日志开关
     */
    private Boolean logSwitch;

    /**
     * 用户须知
     */
    private String userNotice;
}
