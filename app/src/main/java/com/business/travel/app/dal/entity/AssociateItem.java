package com.business.travel.app.dal.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import com.business.travel.app.dal.entity.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author chenshang
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = false)
public class AssociateItem extends BaseEntity {
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
	 * 消费项图标大类
	 */
	private String iconPath;
	/**
	 * 消费项图标名称
	 */
	private String iconName;
}
