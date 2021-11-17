package com.business.travel.app.dal.entity;

import java.util.Optional;

import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;
import com.business.travel.app.dal.entity.base.BaseEntity;
import com.business.travel.utils.DateTimeUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.apache.commons.lang3.StringUtils;

/**
 * @author chenshang
 * 差旅项目表
 */
@Entity(indices = {@Index(value = "name", unique = true)})
@Data
@EqualsAndHashCode(callSuper = false)
public class Project extends BaseEntity {
	@PrimaryKey(autoGenerate = true)
	private Long id;
	/**
	 * 项目名称
	 */
	private String name;
	/**
	 * 项目开始时间,yyyy-MM-dd HH:mm:ss
	 */
	private String startTime;
	/**
	 * 项目结束时间,yyyy-MM-dd HH:mm:ss
	 */
	private String endTime;
	/**
	 * 备注
	 */
	private String remark;

	public String getProductTime() {
		String startTime = Optional.ofNullable(this.getStartTime())
				.filter(StringUtils::isNotBlank)
				.map(DateTimeUtil::parseDate)
				.map(datetime -> DateTimeUtil.format(datetime, "MM月dd日"))
				.orElse("");

		String endTime = Optional.ofNullable(this.getEndTime())
				.filter(StringUtils::isNotBlank)
				.map(DateTimeUtil::parseDate)
				.map(datetime -> DateTimeUtil.format(datetime, "MM月dd日"))
				.orElse("");
		String productTime = startTime;
		if (StringUtils.isNotBlank(endTime)) {
			productTime = productTime + " - " + endTime;
		}
		return productTime;
	}
}
