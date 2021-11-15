package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.ConsumerItemDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.ConsumerItem;
import com.business.travel.app.databinding.ActivityConsumerItemBinding;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class ConsumerItemActivity extends BaseActivity<ActivityConsumerItemBinding> {
	List<ConsumerItem> consumerItemList = new ArrayList<>();
	private ConsumerItemDao consumerItemDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();
		consumerItemDao = AppDatabase.getInstance(this).consumerItemDao();
		final List<ConsumerItem> consumerItems = consumerItemDao.selectAll();
		consumerItemList.addAll(consumerItems);
		mock();

		LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLayoutManager(layoutManager);

		final Adapter adapter = new Adapter() {
			@NonNull
			@NotNull
			@Override
			public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_consumer_item, parent, false);
				return new ViewHolder(view) {
				};
			}

			@Override
			public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
				ImageView imageView = holder.itemView.findViewById(R.id.bill_icon);
				final ConsumerItem consumerItem = consumerItemList.get(position);
				final String iconPath = consumerItem.getIconPath();
				final String iconName = consumerItem.getIconName();
				//CompletableFutureUtil.runAsync(() -> {
				//	final InputStream inputStream = BusinessTravelResourceApi.getIcon(iconPath, iconName);
				//	Sharp.loadInputStream(inputStream).into(imageView);
				//});
			}

			@Override
			public int getItemCount() {
				return consumerItemList.size();
			}
		};
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setAdapter(adapter);
		//长按移动排序
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setOnItemMoveListener(new BaseRecyclerViewOnItemMoveListener<>(consumerItemList, adapter));

		//支出按钮的背景
		GradientDrawable gradientDrawableExpense = (GradientDrawable)viewBinding.UIConsumerItemTextViewExpense.getBackground();
		//收入按钮的背景
		GradientDrawable gradientDrawableIncome = (GradientDrawable)viewBinding.UIConsumerItemTextViewIncome.getBackground();
		viewBinding.UIConsumerItemTextViewExpense.setOnClickListener(v -> {
			viewBinding.UIConsumerItemTextViewExpense.setTextColor(ColorUtils.getColor(R.color.teal_800));
			gradientDrawableExpense.setColor(ColorUtils.getColor(R.color.white));

			viewBinding.UIConsumerItemTextViewIncome.setTextColor(ColorUtils.getColor(R.color.white));
			gradientDrawableIncome.setColor(ColorUtils.getColor(R.color.teal_800));

		});

		viewBinding.UIConsumerItemTextViewIncome.setOnClickListener(v -> {
			viewBinding.UIConsumerItemTextViewIncome.setTextColor(ColorUtils.getColor(R.color.teal_800));
			gradientDrawableIncome.setColor(ColorUtils.getColor(R.color.white));

			viewBinding.UIConsumerItemTextViewExpense.setTextColor(ColorUtils.getColor(R.color.white));
			gradientDrawableExpense.setColor(ColorUtils.getColor(R.color.teal_800));
		});
	}

	private void mock() {
		for (int i = 0; i < 100; i++) {
			ConsumerItem consumerItem = new ConsumerItem();
			consumerItem.setId(0L);
			consumerItem.setName(i + "");
			consumerItem.setIconPath("");
			consumerItem.setIconName("");
			consumerItem.setType(0);
			consumerItem.setCreateTime("");
			consumerItem.setModifyTime("");
			consumerItemList.add(consumerItem);
		}
	}
}