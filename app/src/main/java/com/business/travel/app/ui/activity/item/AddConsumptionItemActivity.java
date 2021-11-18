package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.business.travel.app.R;
import com.business.travel.app.databinding.ActivityAddConsumptionItemBinding;
import com.business.travel.app.enums.IconEnum;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.activity.bill.IconRecyclerViewAdapter;
import com.business.travel.app.ui.base.BaseActivity;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class AddConsumptionItemActivity extends BaseActivity<ActivityAddConsumptionItemBinding> {

	private final List<String> typeList = new ArrayList<>();
	private final List<ImageIconInfo> iconList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mock();
		Objects.requireNonNull(getSupportActionBar()).hide();
		viewBinding.SwipeRecyclerView1.setLayoutManager(new LinearLayoutManager(this));
		viewBinding.SwipeRecyclerView1.setAdapter(new Adapter() {
			@NonNull
			@NotNull
			@Override
			public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_add_consumer_icon_item, parent, false);
				return new ViewHolder(view) {
				};
			}

			@Override
			public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
				final SwipeRecyclerView swipeRecyclerView = holder.itemView.findViewById(R.id.SwipeRecyclerView2);
				LayoutManager layoutManager = new GridLayoutManager(AddConsumptionItemActivity.this, 5);
				swipeRecyclerView.setLayoutManager(layoutManager);
				IconRecyclerViewAdapter billRecyclerViewAdapter = new IconRecyclerViewAdapter(ItemTypeEnum.CONSUMPTION, iconList, AddConsumptionItemActivity.this);
				swipeRecyclerView.setAdapter(billRecyclerViewAdapter);
			}

			@Override
			public int getItemCount() {
				return typeList.size();
			}
		});

	}

	private void mock() {
		for (int i = 0; i < 10; i++) {
			typeList.add(i + "");
		}
		Arrays.stream(IconEnum.values()).forEach(iconEnum -> {
			ImageIconInfo imageIconInfo = new ImageIconInfo();
			imageIconInfo.setResourceId(iconEnum.getResourceId());
			imageIconInfo.setName(iconEnum.getDescription());
			imageIconInfo.setSelected(false);
			iconList.add(imageIconInfo);
		});
	}
}