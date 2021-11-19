package com.business.travel.app.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GiteeContent {

	@JsonProperty("download_url")
	private String downloadUrl;
	@JsonProperty("html_url")
	private String htmlUrl;
	private String name;
	private String path;
	private String sha;
	private Object size;
	private String type;
	private String url;
}
