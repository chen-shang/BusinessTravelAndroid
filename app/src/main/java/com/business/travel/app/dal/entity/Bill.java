package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.business.travel.app.dal.entity.base.BaseEntity;
import com.business.travel.app.enums.ConsumptionTypeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenshang
 * 差旅账单单据表
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Bill extends BaseEntity {
	@PrimaryKey(autoGenerate = true)
	private Long id;
	/**
	 * 账单图标
	 */
	private String iconDownloadUrl;
	/**
	 * 账单名字,逗号分隔
	 */
	private String name;
	/**
	 * 账单金额,单位分
	 */
	private Long amount;
	/**
	 * 消费类型
	 *
	 * @see ConsumptionTypeEnum
	 */
	private String consumptionType;
	/**
	 * 消费时间,yyyy-MM-dd
	 */
	private String consumeDate;
	/**
	 * 消费人员id,逗号分割
	 */
	private String memberIds;
	/**
	 * 备注
	 */
	private String remark;
	/**
	 * 所属的项目名称
	 */
	private Long projectId;
}
