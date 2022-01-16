package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.business.travel.app.dal.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户信息表扩展表，预留
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity {
    @PrimaryKey(autoGenerate = true)
    private Long id;
    /**
     * 头像地址 可以是网络剧也可以是本地
     */
    private String icon;
    /**
     * 唯一编码，不同的用户有不同的编码
     */
    private String code;
    /**
     * 用户名字,逗号分隔
     */
    private String name;
    /**
     * 手机号
     */
    private String phone;
    /**
     * 年龄
     */
    private String age;
    /**
     * 同意隐私协议和用户条款
     */
    private Boolean agree;
}
