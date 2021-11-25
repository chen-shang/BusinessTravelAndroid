package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.business.travel.app.dal.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenshang
 * 人员表
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class Member extends BaseEntity {
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
	 * 图标地址
	 */
	private String iconDownloadUrl;
	/**
	 * 图标名称
	 */
	private String iconName;
	/**
	 * 顺序id
	 */
	private Long sortId;
}
