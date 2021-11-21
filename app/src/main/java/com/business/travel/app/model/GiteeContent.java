package com.business.travel.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

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
	//private String url;
	//private Object size;
	//@JsonProperty("html_url")
	//private String htmlUrl;
}
