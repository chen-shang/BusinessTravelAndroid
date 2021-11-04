package com.business.travel.app.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import android.annotation.SuppressLint;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.business.travel.app.R;
import com.business.travel.app.databinding.ActivityAddBillBinding;
import com.business.travel.app.enums.IconEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.ShareData;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 * 添加账单
 */
public class AddBillActivity extends BaseActivity<ActivityAddBillBinding, ShareData> {

	private final List<ImageIconInfo> iconList = new ArrayList<>();
	private final List<ImageIconInfo> associateList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();

		Arrays.stream(IconEnum.values()).forEach(iconEnum -> {
			ImageIconInfo imageIconInfo = new ImageIconInfo();
			imageIconInfo.setResourceId(iconEnum.getResourceId());
			imageIconInfo.setName(iconEnum.getDescription());
			imageIconInfo.setSelected(false);
			iconList.add(imageIconInfo);
		});

		ImageIconInfo imageAddIconInfo = new ImageIconInfo();
		imageAddIconInfo.setResourceId(R.drawable.bill_icon_add);
		imageAddIconInfo.setName("添加");
		iconList.add(imageAddIconInfo);

		ImageIconInfo imageAddIconInfo2 = new ImageIconInfo();
		imageAddIconInfo2.setResourceId(R.drawable.bill_icon_add);
		imageAddIconInfo2.setName("添加");
		associateList.add(imageAddIconInfo2);

		LayoutManager layoutManager = new GridLayoutManager(this, 5);
		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setLayoutManager(layoutManager);
		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setAdapter(new AddBillAdapter(iconList));

		//同行人列表
		LayoutManager layoutManager2 = new GridLayoutManager(this, 5);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setLayoutManager(layoutManager2);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setAdapter(new AddBillAdapter(associateList));
	}
}