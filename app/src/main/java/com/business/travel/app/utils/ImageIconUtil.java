package com.business.travel.app.utils;

import com.business.travel.app.model.ImageIconInfo;

import java.util.List;

public class ImageIconUtil {

    /**
     * 判断list对象 是否有改变
     *
     * @param newList
     * @param oldList
     * @return
     */
    public static boolean dataChange(List<ImageIconInfo> newList, List<ImageIconInfo> oldList) {
        if (newList.size() != oldList.size()) {
            return true;
        }
        //主要对比的是id，只要对应的id变了就认为两个列表不同
        for (int i = 0; i < newList.size(); i++) {
            if (!newList.get(i).getId().equals(oldList.get(i).getId())) {
                return true;
            }
        }
        return false;
    }
}
