package com.business.travel.app.dal.entity.base;

import androidx.room.ColumnInfo;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.vo.enums.DeleteEnum;
import lombok.Data;

/**
 * @author chenshang
 */
@Data
public class BaseEntity {

	/**
	 * 项目创建时间,yyyy-MM-dd HH:mm:ss
	 */
	private Long createTime;
	/**
	 * 项目最后修改时间,yyyy-MM-dd HH:mm:ss
	 */
	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	private Long modifyTime = DateTimeUtil.timestamp();
	/**
	 * 软删除标志
	 */
	private Integer isDeleted = DeleteEnum.NOT_DELETE.getCode();
}
