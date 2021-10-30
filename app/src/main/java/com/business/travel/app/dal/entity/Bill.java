package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * @author chenshang
 * 差旅账单单据表
 */
@Entity
public class Bill {
	@PrimaryKey(autoGenerate = true)
	private Long id;

	/**
	 * 账单名字
	 */
	private String name;
	/**
	 * 账单显示图标
	 */
	private Integer iconId;
	/**
	 * 账单金额,单位分
	 */
	private Long amount;
	/**
	 * 消费时间,yyyy-MM-dd HH:mm:ss
	 */
	private String consumeTime;
	/**
	 * 差旅同行人
	 */
	private String associate;

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
