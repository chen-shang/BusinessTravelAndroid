package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;

/**
 * @author chenshang
 * 差旅账单单据表
 */
@Entity
@Data
public class Bill {
	@PrimaryKey(autoGenerate = true)
	private Long id;
	/**
	 * 账单名字,逗号分隔
	 */
	private String name;
	/**
	 * 所属的项目名称
	 */
	private Long projectId;

	/**
	 * 账单金额,单位分
	 */
	private Double amount;
	/**
	 * 消费时间,yyyy-MM-dd
	 */
	private String consumeDate;
	/**
	 * 差旅同行人id,逗号分割
	 */
	private String associateId;

	/**
	 * 项目创建时间,yyyy-MM-dd HH:mm:ss
	 */
	private String createTime;
	/**
	 * 项目最后修改时间,yyyy-MM-dd HH:mm:ss
	 */
	private String modifyTime;
	/**
	 * 备注
	 */
	private String remark;
}
