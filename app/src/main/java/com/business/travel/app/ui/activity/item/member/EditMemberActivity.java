package com.business.travel.app.ui.activity.item.member;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.business.travel.app.R;
import com.business.travel.app.databinding.ActivityEditMemberBinding;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.ui.activity.item.AddItemActivity;
import com.business.travel.app.ui.activity.item.EditItemRecyclerViewAdapter;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import static com.yanzhenjie.recyclerview.SwipeRecyclerView.RIGHT_DIRECTION;

public class EditMemberActivity extends BaseActivity<ActivityEditMemberBinding> {

	private final MemberService memberService = new MemberService(this);
	private final List<ImageIconInfo> memberIconList = new ArrayList<>();
	private EditItemRecyclerViewAdapter editConsumptionRecyclerViewAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 注册列表
		 */
		registerSwipeRecyclerView();
		/**
		 * 注册返回按钮
		 */
		registerEditConsumptionActivityImageButtonBack();
		//注册添加按钮操作事件
		registerConsumerItemButtonAddItem();
	}

	private void registerSwipeRecyclerView() {
		LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setLayoutManager(layoutManager);
		editConsumptionRecyclerViewAdapter = new EditItemRecyclerViewAdapter(memberIconList, this);

		//长按移动排序
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setOnItemMoveListener(
				new BaseRecyclerViewOnItemMoveListener<>(memberIconList, editConsumptionRecyclerViewAdapter).onItemMove(
						(consumptionItems, fromPosition, toPosition) -> IntStream.range(fromPosition, toPosition).forEachOrdered(sortId -> memberService.updateMemberSort(consumptionItems.get(sortId).getId(), (long)sortId))
				)
		);

		//添加分隔线
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.addItemDecoration(new DefaultItemDecoration(Color.GRAY));
		//添加删除按钮
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setSwipeMenuCreator((leftMenu, rightMenu, position) -> {
			SwipeMenuItem deleteItem = new SwipeMenuItem(this).setImage(R.drawable.ic_base_delete).setHeight(LayoutParams.WRAP_CONTENT).setWidth(LayoutParams.WRAP_CONTENT);
			rightMenu.addMenuItem(deleteItem);//设置右边的侧滑
		});
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setOnItemMenuClickListener((menuBridge, adapterPosition) -> {
			// 任何操作必须先关闭菜单，否则可能出现Item菜单打开状态错乱。
			menuBridge.closeMenu();
			// 左侧还是右侧菜单：0左1右
			int direction = menuBridge.getDirection();
			// 菜单在Item中的Position：
			int menuPosition = menuBridge.getPosition();
			//被删除的item
			ImageIconInfo imageIconInfo = memberIconList.get(adapterPosition);
			if (direction == RIGHT_DIRECTION && menuPosition == 0) {
				//移除元素
				memberIconList.remove(adapterPosition);
				editConsumptionRecyclerViewAdapter.notifyDataSetChanged();
				//先删除该元素
				memberService.softDeleteMember(imageIconInfo.getId());
			}
		});

		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setAdapter(editConsumptionRecyclerViewAdapter);
	}

	private void registerConsumerItemButtonAddItem() {
		viewBinding.UIAssociateItemButtonAddItem.setOnClickListener(v -> {
			Intent intent = new Intent(this, AddItemActivity.class);
			intent.putExtra("itemType", ItemTypeEnum.MEMBER.name());
			startActivity(intent);
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		refresh();
	}

	private void refresh() {
		List<ImageIconInfo> newLeastMemberIconList = memberService.queryAllMembersImageIconInfo();
		memberIconList.clear();
		memberIconList.addAll(newLeastMemberIconList);
		editConsumptionRecyclerViewAdapter.notifyDataSetChanged();
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