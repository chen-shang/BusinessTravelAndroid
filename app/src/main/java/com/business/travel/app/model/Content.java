package com.business.travel.app.model;

import javax.annotation.Generated;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import lombok.Data;

@Generated("net.hexar.json2pojo")
@Data
public class Content {

	@SerializedName("download_url")
	@JsonProperty("download_url")
	private String downloadUrl;
	@SerializedName("html_url")
	@JsonProperty("html_url")
	private String htmlUrl;
	@Expose
	private String name;
	@Expose
	private String path;
	@Expose
	private String sha;
	@Expose
	private Object size;
	@Expose
	private String type;
	@Expose
	private String url;
}
