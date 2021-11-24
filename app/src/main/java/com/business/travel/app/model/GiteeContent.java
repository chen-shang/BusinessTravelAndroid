package com.business.travel.app.model;

import java.util.List;

import com.business.travel.utils.SplitUtil;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * Gitee上资源接口返回结构
 */
@Data
public class GiteeContent {

	/**
	 * 下载链接
	 */
	@JsonProperty("download_url")
	private String downloadUrl;
	/**
	 * 文件名字
	 */
	private String name;
	/**
	 * 文件路径
	 */
	private String path;
	/**
	 * sha
	 */
	private String sha;
	/**
	 * 文件类型 dir、file
	 */
	private String type;

	/**
	 * 这几个字段暂时不用映射
	 * private String url;
	 * private Object size;
	 *
	 * @JsonProperty("html_url") private String htmlUrl;
	 */

	/**
	 * 获取节点顺序,没有顺序的放到最后
	 */
	public Integer getItemSort() {
		if (StringUtils.isBlank(name)) {
			return 1;
		}
		final List<String> list = SplitUtil.trimToStringList(name, "-");
		if (list.size() == 1) {
			return 1;
		}
		return list.stream().findFirst().map(Integer::valueOf).orElse(1);
	}
}
