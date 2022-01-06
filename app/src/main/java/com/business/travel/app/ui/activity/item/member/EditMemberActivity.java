package com.business.travel.app.ui.activity.item.member;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

import android.content.Intent;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.R;
import com.business.travel.app.databinding.ActivityEditMemberBinding;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.ui.activity.item.AddItemActivity;
import com.business.travel.app.ui.activity.item.EditItemRecyclerViewAdapter;
import com.business.travel.app.ui.base.BaseRecyclerViewOnItemMoveListener;
import com.business.travel.app.ui.base.ColorStatusBarActivity;
import com.business.travel.app.utils.ImageIconUtil;
import com.business.travel.app.utils.Try;
import com.business.travel.app.view.EmptyHeaderView;
import com.business.travel.vo.enums.ItemTypeEnum;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import static com.yanzhenjie.recyclerview.SwipeRecyclerView.RIGHT_DIRECTION;

/**
 * 编辑人员页面
 */
public class EditMemberActivity extends ColorStatusBarActivity<ActivityEditMemberBinding> {

	/**
	 * 人员图标列表
	 */
	private final List<ImageIconInfo> memberIconList = new ArrayList<>();
	/**
	 * 人员图标列表适配器
	 */
	private EditItemRecyclerViewAdapter editConsumptionRecyclerViewAdapter;
	//注入service
	private MemberService memberService;
	/**
	 * 列表为空时候显示的内容,用headView实现该效果
	 */
	private EmptyHeaderView emptyHeaderView;

	@Override
	protected void inject() {
		memberService = new MemberService(this);
		emptyHeaderView = new EmptyHeaderView(getLayoutInflater());
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		/**
		 * 注册列表
		 */
		registerSwipeRecyclerView();
		//注册添加按钮操作事件
		registerConsumerItemButtonAddItem();
	}

	@Override
	protected void onResume() {
		super.onResume();
		Try.of(this::refresh);
	}

	private void registerSwipeRecyclerView() {
		LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setLayoutManager(layoutManager);
		editConsumptionRecyclerViewAdapter = new EditItemRecyclerViewAdapter(memberIconList, this);

		//长按移动排序
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setOnItemMoveListener(new BaseRecyclerViewOnItemMoveListener<>(memberIconList, editConsumptionRecyclerViewAdapter).onItemMove(
				(consumptionItems, fromPosition, toPosition) -> IntStream.range(fromPosition, toPosition).forEachOrdered(sortId -> memberService.updateMemberSort(consumptionItems.get(sortId).getId(), (long)sortId))));

		//添加分隔线
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.addItemDecoration(new DefaultItemDecoration(ColorUtils.getColor(R.color.black_300)));
		//添加删除按钮
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setSwipeMenuCreator((leftMenu, rightMenu, position) -> {
			SwipeMenuItem deleteItem = new SwipeMenuItem(this).setBackgroundColor(ColorUtils.getColor(R.color.red_0)).setImage(R.drawable.ic_base_delete_white).setHeight(LayoutParams.MATCH_PARENT).setWidth(150);
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
				editConsumptionRecyclerViewAdapter.notifyItemRemoved(adapterPosition);
				editConsumptionRecyclerViewAdapter.notifyItemRangeChanged(adapterPosition, memberIconList.size() - adapterPosition);

				//先删除该元素
				memberService.softDeleteMember(imageIconInfo.getId());
				checkEmpty();
			}
		});

		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setAdapter(editConsumptionRecyclerViewAdapter);
	}

	private void registerConsumerItemButtonAddItem() {
		viewBinding.DragFloatActionButton.setOnClickListener(v -> {
			Intent intent = new Intent(this, AddItemActivity.class);
			intent.putExtra("itemType", ItemTypeEnum.MEMBER.name());
			startActivity(intent);
		});
	}

	private void refresh() {
		List<ImageIconInfo> newLeastMemberIconList = memberService.queryAllMembersIconInfo();
		if (CollectionUtils.isEmpty(newLeastMemberIconList)) {
			emptyHeaderView.addTo(viewBinding.UIAssociateSwipeRecyclerViewConsumerItem);
		}

		//先尝试比较一下list 是否改变
		if (!ImageIconUtil.dataChange(newLeastMemberIconList, memberIconList)) {
			return;
		}

		memberIconList.clear();
		memberIconList.addAll(newLeastMemberIconList);
		editConsumptionRecyclerViewAdapter.notifyDataSetChanged();
		checkEmpty();
	}

	/**
	 * 检查数据是否为空
	 */
	private void checkEmpty() {
		if (CollectionUtils.isNotEmpty(memberIconList)) {
			emptyHeaderView.removeFrom(viewBinding.UIAssociateSwipeRecyclerViewConsumerItem);
		}

		if (CollectionUtils.isEmpty(memberIconList) && viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.getHeaderCount() == 0) {
			emptyHeaderView.addTo(viewBinding.UIAssociateSwipeRecyclerViewConsumerItem);
		}
	}
}