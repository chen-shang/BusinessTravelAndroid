package com.business.travel.app.ui.base;

import android.content.Context;
import android.view.ViewGroup.LayoutParams;
import com.business.travel.app.R;
import com.yanzhenjie.recyclerview.SwipeMenu;
import com.yanzhenjie.recyclerview.SwipeMenuCreator;
import com.yanzhenjie.recyclerview.SwipeMenuItem;

public class BaseSwipeMenuCreator implements SwipeMenuCreator {

	private final Context context;

	public BaseSwipeMenuCreator(Context context) {this.context = context;}

	@Override
	public void onCreateMenu(SwipeMenu leftMenu, SwipeMenu rightMenu, int position) {
		SwipeMenuItem deleteItem = new SwipeMenuItem(context)
				.setImage(R.drawable.ic_base_delete)
				.setHeight(LayoutParams.WRAP_CONTENT)//设置高，这里使用match_parent，就是与item的高相同
				.setWidth(LayoutParams.WRAP_CONTENT);//设置宽
		rightMenu.addMenuItem(deleteItem);//设置右边的侧滑
		//右边删除,左边编辑
	}
}
