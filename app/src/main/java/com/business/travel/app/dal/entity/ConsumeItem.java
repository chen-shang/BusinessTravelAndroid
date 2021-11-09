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
public class ConsumeItem extends BaseEntity {
    /**
     * 主键id
     */
    @PrimaryKey(autoGenerate = true)
    private Long id;
    /**
     * 消费项名称
     */
    private String name;
    /**
     * 消费项图标
     */
    private String icon;
}
