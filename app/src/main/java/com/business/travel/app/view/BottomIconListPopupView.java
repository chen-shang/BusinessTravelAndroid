package com.business.travel.app.view;

import java.util.List;

import android.content.Context;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import cn.mtjsoft.www.gridviewpager_recycleview.GridViewPager;
import com.business.travel.app.R;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.utils.GridViewPagerUtil;
import com.business.travel.app.utils.ImageLoadUtil;
import com.lxj.xpopup.core.BottomPopupView;
import org.jetbrains.annotations.NotNull;

public class BottomIconListPopupView extends BottomPopupView {
	/**
	 * 消费项图标信息列表
	 */
	private final List<ImageIconInfo> imageIconInfoList;

	public BottomIconListPopupView(@NonNull @NotNull Context context, List<ImageIconInfo> imageIconInfoList) {
		super(context);
		this.imageIconInfoList = imageIconInfoList;
	}

	@Override
	protected int getImplLayoutId() {
		return R.layout.bottom_icon_list_popup;
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		ContentBar contentBar = findViewById(R.id.topTitleBar);
		contentBar.contentBarTitle.setEnabled(false);
		GridViewPager gridViewPager = findViewById(R.id.GridViewPager_MemberIconList);
		GridViewPagerUtil.registerPageViewCommonProperty(gridViewPager)
		                 // 设置数据总数量
		                 .setDataAllCount(imageIconInfoList.size())
		                 // 设置每页行数 // 设置每页列数
		                 .setRowCount((int)Math.ceil(imageIconInfoList.size() / 5d)).setColumnCount(5)
		                 // 设置是否显示指示器
		                 .setPointIsShow(true)
		                 // 设置背景图片(此时设置的背景色无效，以背景图片为主)
		                 .setBackgroundImageLoader(bgImageView -> {
		                 })
		                 // 数据绑定
		                 .setImageTextLoaderInterface((imageView, textView, position) -> {
			                 // 自己进行数据的绑定，灵活度更高，不受任何限制
			                 bind(imageView, textView, imageIconInfoList.get(position));
		                 }).show();
	}

	private void bind(ImageView imageView, TextView textView, ImageIconInfo imageIconInfo) {
		//默认未选中状态
		imageView.setBackgroundResource(R.drawable.corners_shape_unselect);
		imageView.setImageResource(R.drawable.ic_base_placeholder);

		textView.setText(imageIconInfo.getName());

		int selectColor = ContextCompat.getColor(getContext(), R.color.red_2);
		ImageLoadUtil.loadImageToView(imageIconInfo.getIconDownloadUrl(), imageView, imageIconInfo.isSelected() ? selectColor : null);

		imageView.setOnClickListener(v -> {
			//否则,改变选中颜色
			imageIconInfo.setSelected(!imageIconInfo.isSelected());
			ImageLoadUtil.loadImageToView(imageIconInfo.getIconDownloadUrl(), imageView, imageIconInfo.isSelected() ? selectColor : null);
		});
	}
}
