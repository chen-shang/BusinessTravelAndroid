package com.business.travel.app.model;

import java.util.List;

import lombok.Data;

@Data
public class ItemIconInfo {
	private String path;
	private List<ImageIconInfo> imageIconInfos;
}
