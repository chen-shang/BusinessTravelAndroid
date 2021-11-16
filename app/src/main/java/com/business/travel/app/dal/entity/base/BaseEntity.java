package com.business.travel.app.dal.entity.base;

import androidx.room.ColumnInfo;
import lombok.Data;

/**
 * @author chenshang
 */
@Data
public class BaseEntity {

	/**
	 * 项目创建时间,yyyy-MM-dd HH:mm:ss
	 */
	@ColumnInfo(name = "create_time")
	private String createTime;
	/**
	 * 项目最后修改时间,yyyy-MM-dd HH:mm:ss
	 */
	@ColumnInfo(name = "modify_time", defaultValue = "CURRENT_TIMESTAMP")
	private String modifyTime;
	/**
	 * 软删除标志
	 */
	@ColumnInfo(name = "is_deleted")
	private Integer isDeleted = 1;
}
