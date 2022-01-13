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

public class BottomIconListPopupView extends BottomPopupView {
	/**
	 * 默认展示6列
	 */
	private static final int COLUMN_COUNT = 6;
	/**
	 * 消费项图标信息列表
	 */
	private final List<ImageIconInfo> imageIconInfoList;
	/**
	 * 当点击确认以后执行的行为
	 */
	public Runnable onConfirm;

	public BottomIconListPopupView(@NonNull @NotNull Context context, List<ImageIconInfo> imageIconInfoList) {
		super(context);
		this.imageIconInfoList = imageIconInfoList;
	}

	@Override
	protected int getImplLayoutId() {
		return R.layout.bottom_icon_list_popup;
	}

	public BottomIconListPopupView onConfirm(Runnable onConfirm) {
		this.onConfirm = onConfirm;
		return this;
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
		//右侧确认点击后
		contentBar.contentBarRightIcon.setOnClickListener(v -> {
			//取消弹框,先取消弹框,在执行动作
			this.dismiss();
			//执行确认事件
			onConfirm.run();
		});
		//弹框图标初始化
		GridViewPager gridViewPager = findViewById(R.id.GridViewPager_MemberIconList);
		GridViewPagerUtil.registerPageViewCommonProperty(gridViewPager)
		                 // 设置数据总数量
		                 .setDataAllCount(imageIconInfoList.size())
		                 // 设置每页行数
		                 .setRowCount((int)Math.ceil(imageIconInfoList.size() / (double)COLUMN_COUNT))
		                 // 设置每页列数
		                 .setColumnCount(COLUMN_COUNT)
		                 // 设置是否显示指示器
		                 .setPointIsShow(true)
		                 // 设置背景图片(此时设置的背景色无效，以背景图片为主)
		                 .setBackgroundImageLoader(bgImageView -> {
		                 })
		                 // 数据绑定
		                 .setImageTextLoaderInterface((imageView, textView, position) -> {
			                 // 自己进行数据的绑定，灵活度更高，不受任何限制
			                 ImageIconInfo imageIconInfo = imageIconInfoList.get(position);
			                 // 绑定图标、文字和点击行为
			                 imageIconInfo.bind(imageView, textView);
		                 }).show();
	}
}
