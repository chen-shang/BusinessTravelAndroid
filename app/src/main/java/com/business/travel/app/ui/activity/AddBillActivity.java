package com.business.travel.app.ui.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.business.travel.app.R;
import com.business.travel.app.databinding.ActivityAddBillBinding;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.base.BaseActivity;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 * 添加账单
 */
public class AddBillActivity extends BaseActivity<ActivityAddBillBinding> {

	private List<ImageIconInfo> iconList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();

		iconList = new ArrayList<>();
		for (int i = 0; i < 100; i++) {
			ImageIconInfo imageIconInfo = new ImageIconInfo();
			imageIconInfo.setResourceId(R.drawable.bill_diandongche);
			imageIconInfo.setName("早餐");
			imageIconInfo.setSelected(false);
			iconList.add(imageIconInfo);
		}

		LayoutManager layoutManager = new GridLayoutManager(this, 5);
		binding.UIAddBillActivitySwipeRecyclerViewBill.setLayoutManager(layoutManager);

		binding.UIAddBillActivitySwipeRecyclerViewBill.setAdapter(new Adapter() {
			@NonNull
			@NotNull
			@Override
			public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_item_icon, parent, false);
				return new ViewHolder(view) {
				};
			}

			@Override
			public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
				ImageView iconImageView = holder.itemView.findViewById(R.id.UI_ImageView_Icon);
				ImageIconInfo imageIconInfo = iconList.get(position);
				iconImageView.setImageResource(imageIconInfo.getResourceId());

				TextView descriptionTextView = holder.itemView.findViewById(R.id.UI_TextView_Description);
				descriptionTextView.setText(imageIconInfo.getName());
			}

			@Override
			public int getItemCount() {
				return iconList.size();
			}
		});
	}
}