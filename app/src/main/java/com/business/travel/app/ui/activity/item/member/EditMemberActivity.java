package com.business.travel.app.ui.activity.item.member;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.dal.dao.AssociateItemDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Member;
import com.business.travel.app.databinding.ActivityEditMemberBinding;
import com.business.travel.app.enums.DeleteEnum;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.activity.item.AddItemActivity;
import com.business.travel.app.ui.activity.item.EditItemRecyclerViewAdapter;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;

public class EditMemberActivity extends BaseActivity<ActivityEditMemberBinding> {

	private final List<ImageIconInfo> associateImageIconList = new ArrayList<>();
	private EditItemRecyclerViewAdapter editConsumptionItemRecyclerViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setLayoutManager(layoutManager);
		editConsumptionItemRecyclerViewAdapter = new EditItemRecyclerViewAdapter(associateImageIconList, this);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setAdapter(editConsumptionItemRecyclerViewAdapter);
		final AssociateItemDao associateItemDao = AppDatabase.getInstance(this).associateItemDao();

		//长按移动排序
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setOnItemMoveListener(
				new BaseRecyclerViewOnItemMoveListener<>(associateImageIconList, editConsumptionItemRecyclerViewAdapter)
						.onItemMove((consumptionItems, fromPosition, toPosition) -> {
							//更新排序 todo 优化
							for (int i = 0; i < consumptionItems.size(); i++) {
								ImageIconInfo imageIconInfo = consumptionItems.get(i);

								Member member = new Member();
								member.setId(imageIconInfo.getId());
								member.setSortId((long)i);
								associateItemDao.update(member);
							}
						})
		);
		registerEditConsumptionActivityImageButtonBack();
		//注册添加按钮操作事件
		registerConsumerItemButtonAddItem();
	}

	private void registerConsumerItemButtonAddItem() {
		viewBinding.UIAssociateItemButtonAddItem.setOnClickListener(v -> {
			Intent intent = new Intent(this, AddItemActivity.class);
			intent.putExtra("itemType", ItemTypeEnum.ASSOCIATE.name());
			startActivity(intent);
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	private void refresh() {
		final AssociateItemDao associateItemDao = AppDatabase.getInstance(this).associateItemDao();
		final List<Member> members = associateItemDao.selectAll(DeleteEnum.NOT_DELETE.getCode());
		if (CollectionUtils.isEmpty(members)) {
			return;
		}
		final List<ImageIconInfo> newImages = members.stream().map(associateItem -> {
			ImageIconInfo imageIconInfo = new ImageIconInfo();
			imageIconInfo.setId(associateItem.getId());
			imageIconInfo.setName(associateItem.getName());
			imageIconInfo.setIconDownloadUrl(associateItem.getIconDownloadUrl());
			imageIconInfo.setIconName(associateItem.getIconName());
			imageIconInfo.setItemType(ItemTypeEnum.ASSOCIATE.name());
			imageIconInfo.setSortId(associateItem.getSortId());
			imageIconInfo.setSelected(false);
			return imageIconInfo;
		}).collect(Collectors.toList());
		associateImageIconList.clear();
		associateImageIconList.addAll(newImages);
		editConsumptionItemRecyclerViewAdapter.notifyDataSetChanged();
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