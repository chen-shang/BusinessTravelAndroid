package com.business.travel.app.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.business.travel.app.R;
import com.business.travel.app.databinding.ActivityAddBillBinding;
import com.business.travel.app.enums.IconEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.base.BaseActivity;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 * 添加账单
 */
public class AddBillActivity extends BaseActivity<ActivityAddBillBinding> {

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

		for (int i = 0; i < 9; i++) {
			ImageIconInfo imageIconInfo = new ImageIconInfo();
			if (i % 2 == 0) {
				imageIconInfo.setResourceId(R.drawable.bill_icon_man);
			} else {
				imageIconInfo.setResourceId(R.drawable.bill_icon_woman);
			}
			imageIconInfo.setName("张" + i);
			imageIconInfo.setSelected(false);
			associateList.add(imageIconInfo);
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
				ImageIconInfo imageIconInfo = iconList.get(position);

				ImageView iconImageView = holder.itemView.findViewById(R.id.UI_ImageView_Icon);
				iconImageView.setImageResource(imageIconInfo.getResourceId());

				TextView descriptionTextView = holder.itemView.findViewById(R.id.UI_TextView_Description);
				descriptionTextView.setText(imageIconInfo.getName());

				iconImageView.setOnClickListener(v -> {
					if (imageIconInfo.isSelected()) {
						int color = ContextCompat.getColor(getApplicationContext(), R.color.black_100);
						iconImageView.setImageDrawable(changeToColor(imageIconInfo.getResourceId(), color));
						descriptionTextView.setTextColor(color);
						imageIconInfo.setSelected(false);
					} else {
						int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_800);
						iconImageView.setImageDrawable(changeToColor(imageIconInfo.getResourceId(), color));
						descriptionTextView.setTextColor(color);
						imageIconInfo.setSelected(true);
					}
				});
			}

			private Drawable changeToColor(int resourceId, int color) {
				Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), resourceId);
				Drawable wrap = DrawableCompat.wrap(drawable);
				DrawableCompat.setTint(wrap, color);
				return drawable;
			}

			@Override
			public int getItemCount() {
				return iconList.size();
			}
		});

		//同行人列表
		LayoutManager layoutManager2 = new GridLayoutManager(this, 5);
		binding.UIAddBillActivitySwipeRecyclerViewAssociate.setLayoutManager(layoutManager2);
		binding.UIAddBillActivitySwipeRecyclerViewAssociate.setAdapter(new Adapter() {
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
				ImageIconInfo imageIconInfo = associateList.get(position);

				ImageView iconImageView = holder.itemView.findViewById(R.id.UI_ImageView_Icon);
				iconImageView.setImageResource(imageIconInfo.getResourceId());

				TextView descriptionTextView = holder.itemView.findViewById(R.id.UI_TextView_Description);
				descriptionTextView.setText(imageIconInfo.getName());

				iconImageView.setOnClickListener(v -> {
					if (imageIconInfo.isSelected()) {
						int color = ContextCompat.getColor(getApplicationContext(), R.color.black_100);
						iconImageView.setImageDrawable(changeToColor(imageIconInfo.getResourceId(), color));
						descriptionTextView.setTextColor(color);
						imageIconInfo.setSelected(false);
					} else {
						int color = ContextCompat.getColor(getApplicationContext(), R.color.teal_800);
						iconImageView.setImageDrawable(changeToColor(imageIconInfo.getResourceId(), color));
						descriptionTextView.setTextColor(color);
						imageIconInfo.setSelected(true);
					}
				});
			}

			private Drawable changeToColor(int resourceId, int color) {
				Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), resourceId);
				Drawable wrap = DrawableCompat.wrap(drawable);
				DrawableCompat.setTint(wrap, color);
				return drawable;
			}

			@Override
			public int getItemCount() {
				return associateList.size();
			}
		});
	}
}