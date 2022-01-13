package com.business.travel.app.view;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.business.travel.app.R;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.activity.item.AddItemRecyclerViewInnerAdapter;
import com.lxj.xpopup.core.BottomPopupView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

public class BottomIconListPopupView extends BottomPopupView {
	/**
	 * 消费项图标信息列表
	 */
	private final List<ImageIconInfo> imageIconInfoList = new ArrayList<>();

	public BottomIconListPopupView(@NonNull @NotNull Context context) {
		super(context);
	}

	@Override
	protected int getImplLayoutId() {
		return R.layout.bottom_icon_list_popup;
	}

	@Override
	protected void onCreate() {
		super.onCreate();
		SwipeRecyclerView imageIconInfoRecyclerView = findViewById(R.id.UI_AddItemRecyclerViewAdapter_SwipeRecyclerView);
		//接下来是对icon的处理
		LayoutManager layoutManager = new GridLayoutManager(getContext(), 5);
		imageIconInfoRecyclerView.setLayoutManager(layoutManager);
		AddItemRecyclerViewInnerAdapter billRecyclerViewAdapter = new AddItemRecyclerViewInnerAdapter(imageIconInfoList, null);
		imageIconInfoRecyclerView.setAdapter(billRecyclerViewAdapter);
	}
}
