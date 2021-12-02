package com.business.travel.app.utils;

import java.util.List;

import com.business.travel.app.model.ImageIconInfo;

public class ImageIconUtil {

	public static boolean dataChange(List<ImageIconInfo> newList, List<ImageIconInfo> oldList) {
		if (newList.size() != oldList.size()) {
			return true;
		}
		for (int i = 0; i < newList.size(); i++) {
			if (!newList.get(i).getId().equals(oldList.get(i).getId())) {
				return true;
			}
		}
		return false;
	}
}
