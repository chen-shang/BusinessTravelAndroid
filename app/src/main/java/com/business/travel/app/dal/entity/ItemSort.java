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
public class ItemSort extends BaseEntity {
	@PrimaryKey(autoGenerate = true)
	private Long id;
	/**
	 * 记录的排序的类型
	 *
	 * @see com.business.travel.app.enums.ItemTypeEnum
	 */
	private String itemType;
	/**
	 * 排序的ID
	 */
	private String sortIds;
}
