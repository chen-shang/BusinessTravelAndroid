package com.business.travel.app.model;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.business.travel.app.R;
import com.business.travel.app.utils.ImageLoadUtil;
import lombok.Data;

/**
 * 图片图标信息
 * DDD领域模型
 */
@Data
public class ImageIconInfo {
	/**
	 * 主键ID
	 */
	private Long id;
	/**
	 * 名称
	 */
	private String name;
	/**
	 * 图标地址
	 */
	private String iconDownloadUrl;
	/**
	 * 消费项图标名称
	 */
	private String iconName;
	/**
	 * @see com.business.travel.vo.enums.ItemTypeEnum
	 */
	private String itemType;
	/**
	 * 顺序id
	 */
	private Long sortId;
	/**
	 * 是否被选中
	 */
	private boolean selected;

	/**
	 * 绑定图标、文字和点击行为
	 *
	 * @param imageView
	 * @param textView
	 */
	public void bind(ImageView imageView, TextView textView) {
		//初始化对应的图片和文字
		this.init(imageView, textView);

		//加载新的图片和问题
		textView.setText(name);
		ImageLoadUtil.loadImageToView(iconDownloadUrl, imageView);
		imageView.setBackgroundResource(isSelected() ? R.drawable.corners_shape_select : R.drawable.corners_shape_unselect);

		imageView.setOnClickListener(this::changeColor);
	}

	public void init(ImageView imageView, TextView textView) {
		textView.setText("");
		imageView.setImageResource(R.drawable.ic_base_placeholder);
	}

	public void changeColor(View imageView) {
		this.selected = !this.selected;
		imageView.setBackgroundResource(isSelected() ? R.drawable.corners_shape_select : R.drawable.corners_shape_unselect);
	}
}
