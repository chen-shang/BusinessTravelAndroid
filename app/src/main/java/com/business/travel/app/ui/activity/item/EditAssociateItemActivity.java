package com.business.travel.app.ui.activity.item;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.business.travel.app.dal.dao.AssociateItemDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.AssociateItem;
import com.business.travel.app.databinding.ActivityEditAssociateItemBinding;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;

public class EditAssociateItemActivity extends BaseActivity<ActivityEditAssociateItemBinding> {

	List<AssociateItem> associateItems = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setLayoutManager(layoutManager);
		EditAssociatetemRecyclerViewAdapter editConsumptionItemRecyclerViewAdapter = new EditAssociatetemRecyclerViewAdapter(associateItems, this);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setAdapter(editConsumptionItemRecyclerViewAdapter);

		//长按移动排序
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setOnItemMoveListener(
				new BaseRecyclerViewOnItemMoveListener<>(associateItems, editConsumptionItemRecyclerViewAdapter)
						.onItemMove((consumptionItems, fromPosition, toPosition) -> {
							//更新排序 todo 优化
							for (int i = 0; i < consumptionItems.size(); i++) {
								AssociateItem consumptionItem = consumptionItems.get(i);
								consumptionItem.setSortId((long)i);
								final AssociateItemDao associateItemDao = AppDatabase.getInstance(this).associateItemDao();
								associateItemDao.update(consumptionItem);
							}
						})
		);
		registerEditConsumptionActivityImageButtonBack();
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	/**
	 * 注册返回按钮操作事件
	 */
	private void registerEditConsumptionActivityImageButtonBack() {
		//返回按钮点击后
		viewBinding.UIEditAssociateActivityImageButtonBack.setOnClickListener(v -> {
			//记得保存一下顺序
			this.finish();
		});
	}

}