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
import com.business.travel.app.dal.dao.ConsumptionItemDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.ConsumptionItem;
import com.business.travel.app.databinding.ActivityEditConsumptionItemBinding;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class EditConsumptionItemActivity extends BaseActivity<ActivityEditConsumptionItemBinding> {
	List<ConsumptionItem> consumptionItemList = new ArrayList<>();
	private ConsumptionItemDao consumerItemDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();
		consumerItemDao = AppDatabase.getInstance(this).consumptionItemDao();
		final List<ConsumptionItem> consumptionItems = consumerItemDao.selectAll();
		consumptionItemList.addAll(consumptionItems);
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
				final ConsumptionItem consumptionItem = consumptionItemList.get(position);
				final String iconPath = consumptionItem.getIconPath();
				final String iconName = consumptionItem.getIconName();
				//CompletableFutureUtil.runAsync(() -> {
				//	final InputStream inputStream = BusinessTravelResourceApi.getIcon(iconPath, iconName);
				//	Sharp.loadInputStream(inputStream).into(imageView);
				//});
			}

			@Override
			public int getItemCount() {
				return consumptionItemList.size();
			}
		};
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setAdapter(adapter);
		//长按移动排序
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		viewBinding.UIConsumerItemSwipeRecyclerViewConsumerItem.setOnItemMoveListener(new BaseRecyclerViewOnItemMoveListener<>(consumptionItemList, adapter));

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
			ConsumptionItem consumptionItem = new ConsumptionItem();
			consumptionItem.setId(0L);
			consumptionItem.setName(i + "");
			consumptionItem.setIconPath("");
			consumptionItem.setIconName("");
			consumptionItem.setType(0);
			consumptionItem.setCreateTime("");
			consumptionItem.setModifyTime("");
			consumptionItemList.add(consumptionItem);
		}
	}
}