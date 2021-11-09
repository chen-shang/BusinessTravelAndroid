package com.business.travel.app.dal.entity;

import lombok.Data;

/**
 * @author chenshang
 */
@Data
public class BaseEntity {

    /**
     * 项目创建时间,yyyy-MM-dd HH:mm:ss
     */
    private String createTime;
    /**
     * 项目最后修改时间,yyyy-MM-dd HH:mm:ss
     */
    private String modifyTime;
}
