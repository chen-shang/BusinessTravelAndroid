package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenshang
 */
@Entity(indices = {@Index(value = {"name", "icon", "type"}, unique = true)})
@Data
@EqualsAndHashCode(callSuper = false)
public class ConsumerItem extends BaseEntity {
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
    /**
     * 消费项目类型 0:支出 1:收入
     */
    private Integer type;
}
