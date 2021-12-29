package com.business.travel.app.utils;

import cn.mtjsoft.www.gridviewpager_recycleview.GridViewPager;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.R;

public class GridViewPagerUtil {
	/**
	 * 初始化PageView公共属性
	 */
	public static GridViewPager registerPageViewCommonProperty(GridViewPager gridViewPager) {
		return gridViewPager
				// 设置背景色，默认白色
				.setGridViewPagerBackgroundColor(ColorUtils.getColor(R.color.white_1))
				// 设置item的纵向间距 // 设置上边距 // 设置下边距
				.setVerticalSpacing(10).setPagerMarginTop(10).setPagerMarginBottom(10)
				// 设置图片宽度 // 设置图片高度
				.setImageWidth(35).setImageHeight(35)
				// 设置文字与图片的间距
				.setTextImgMargin(5)
				// 设置文字大小
				.setTextSize(12)
				// 设置无限循环
				.setPageLoop(false)
				// 设置指示器与page的间距 // 设置指示器与底部的间距
				.setPointMarginPage(5).setPointMarginBottom(10)
				// 设置指示器的item宽度 // 设置指示器的item高度
				.setPointChildWidth(5).setPointChildHeight(5)
				// 设置指示器的item的间距
				.setPointChildMargin(5)
				// 指示器的item是否为圆形，默认圆形直径取宽高的最小值
				.setPointIsCircle(true)
				// 设置文字颜色
				.setTextColor(ColorUtils.getColor(R.color.black_100))
				// 指示器item未选中的颜色
				.setPointNormalColor(ColorUtils.getColor(R.color.black_100))
				// 指示器item选中的颜色
				.setPointSelectColor(ColorUtils.getColor(R.color.red_2))
				// 设置背景图片(此时设置的背景色无效，以背景图片为主)
				.setBackgroundImageLoader(bgImageView -> {
				})
				// Item点击
				.setGridItemClickListener(position -> {

				})
				// 设置Item长按
				.setGridItemLongClickListener(position -> {

				});
	}
}
