package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenshang
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Associate extends BaseEntity {
    /**
     * 主键id
     */
    @PrimaryKey(autoGenerate = true)
    private Long id;
    /**
     * 同行人姓名
     */
    private String name;
    /**
     * 性别: 0=男 1=女
     */
    private Integer gender;
    /**
     * 头像
     */
    private String avatar;
}
