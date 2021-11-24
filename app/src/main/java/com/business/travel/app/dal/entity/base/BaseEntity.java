package com.business.travel.app.dal.entity.base;

import java.util.Date;

import androidx.room.ColumnInfo;
import com.business.travel.app.enums.DeleteEnum;
import com.business.travel.utils.DateTimeUtil;
import lombok.Data;

/**
 * @author chenshang
 */
@Data
public class BaseEntity {

	/**
	 * 项目创建时间,yyyy-MM-dd HH:mm:ss
	 */
	private String createTime;
	/**
	 * 项目最后修改时间,yyyy-MM-dd HH:mm:ss
	 */
	@ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
	private String modifyTime = DateTimeUtil.format(new Date());
	/**
	 * 软删除标志
	 */
	private Integer isDeleted = DeleteEnum.NOT_DELETE.getCode();
}
