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
public class ConsumptionItem extends BaseEntity {
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
	 * 消费项图标大类
	 */
	private String iconPath;
	/**
	 * 消费项图标名称
	 */
	private String iconName;
	/**
	 * 消费项目类型 0:支出 1:收入
	 */
	private Integer type;
}
