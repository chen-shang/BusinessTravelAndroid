package com.business.travel.app.view;

import java.util.List;

import android.content.Context;
import androidx.annotation.NonNull;
import cn.mtjsoft.www.gridviewpager_recycleview.GridViewPager;
import com.business.travel.app.R;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.utils.GridViewPagerUtil;
import com.lxj.xpopup.core.BottomPopupView;
import org.jetbrains.annotations.NotNull;

public class BottomProjectListPopupView extends BottomPopupView {
	/**
	 * 项目名称列表
	 */
	private final List<String> projectNameList;

	public BottomProjectListPopupView(@NonNull @NotNull Context context, List<String> projectNameList) {
		super(context);
		this.projectNameList = projectNameList;
	}

	@Override
	protected int getImplLayoutId() {
		return R.layout.bottom_icon_list_popup;
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		//弹框顶部栏
		ContentBar contentBar = findViewById(R.id.topTitleBar);
		//中间文字禁止编辑,这里去掉会导致键盘弹出
		contentBar.contentBarTitle.setEnabled(false);
		//左侧箭头点击取消弹框
		contentBar.contentBarLeftIcon.setOnClickListener(v -> this.dismiss());
	}
}
