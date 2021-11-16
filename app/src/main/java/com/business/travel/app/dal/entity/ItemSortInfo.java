package com.business.travel.app.dal.entity;

import androidx.room.PrimaryKey;
import lombok.Data;

/**
 * @author chenshang
 */
@Data
public class ItemSortInfo extends BaseEntity {
	@PrimaryKey(autoGenerate = true)
	private Long id;
	/**
	 * 记录的排序的类型
	 */
	private String type;
	/**
	 * 排序的ID
	 */
	private String sortIds;
}
