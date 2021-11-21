package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.business.travel.app.dal.entity.base.BaseEntity;
import com.business.travel.app.enums.ConsumptionTypeEnum;
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
	 * 图标地址
	 */
	private String iconDownloadUrl;
	/**
	 * 消费项图标名称
	 */
	private String iconName;
	/**
	 * 消费项目类型
	 *
	 * @see ConsumptionTypeEnum
	 */
	private String consumptionType;
}
