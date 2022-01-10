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
import com.business.travel.app.view.header.EmptyHeaderView;
import com.business.travel.vo.enums.ItemTypeEnum;
import com.yanzhenjie.recyclerview.SwipeMenuItem;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import com.yanzhenjie.recyclerview.widget.DefaultItemDecoration;

import static com.yanzhenjie.recyclerview.SwipeRecyclerView.RIGHT_DIRECTION;

/**
 * 编辑人员页面
 */
public class EditMemberActivity extends ColorStatusBarActivity<ActivityEditMemberBinding> {

	/**
	 * 人员图标列表
	 */
	private final List<ImageIconInfo> imageIconInfoList = new ArrayList<>();
	/**
	 * 人员图标列表适配器
	 */
	private EditItemRecyclerViewAdapter editItemRecyclerViewAdapter;
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
		Try.of(() -> this.refreshIconListRecyclerView(viewBinding.UIAssociateSwipeRecyclerViewConsumerItem));
	}

	private void registerSwipeRecyclerView() {
		LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setLayoutManager(layoutManager);
		editItemRecyclerViewAdapter = new EditItemRecyclerViewAdapter(imageIconInfoList, this);

		//长按移动排序
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setLongPressDragEnabled(true);
		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setOnItemMoveListener(new BaseRecyclerViewOnItemMoveListener<>(imageIconInfoList, editItemRecyclerViewAdapter).onItemMove((itemList, fromPosition, toPosition) -> {
			IntStream.range(0, itemList.size()).forEachOrdered(sortId -> memberService.updateMemberSort(itemList.get(sortId).getId(), (long)sortId));
		}));

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
			ImageIconInfo imageIconInfo = imageIconInfoList.get(adapterPosition);
			if (direction == RIGHT_DIRECTION && menuPosition == 0) {
				//移除元素
				imageIconInfoList.remove(adapterPosition);
				editItemRecyclerViewAdapter.notifyItemRemoved(adapterPosition);
				editItemRecyclerViewAdapter.notifyItemRangeChanged(adapterPosition, imageIconInfoList.size() - adapterPosition);

				//先删除该元素
				memberService.softDeleteMember(imageIconInfo.getId());

				if (CollectionUtils.isEmpty(imageIconInfoList)) {
					clear(viewBinding.UIAssociateSwipeRecyclerViewConsumerItem);
				}
			}
		});

		viewBinding.UIAssociateSwipeRecyclerViewConsumerItem.setAdapter(editItemRecyclerViewAdapter);
	}

	private void registerConsumerItemButtonAddItem() {
		viewBinding.DragFloatActionButton.setOnClickListener(v -> {
			Intent intent = new Intent(this, AddItemActivity.class);
			intent.putExtra("itemType", ItemTypeEnum.MEMBER.name());
			startActivity(intent);
		});
	}

	private void refreshIconListRecyclerView(SwipeRecyclerView iconListRecyclerView) {
		List<ImageIconInfo> newIconList = memberService.queryAllMembersIconInfo();
		if (CollectionUtils.isEmpty(newIconList)) {
			clear(iconListRecyclerView);
			return;
		}

		//先尝试比较一下list 是否改变
		if (!ImageIconUtil.dataChange(newIconList, imageIconInfoList)) {
			//如果数据没有改变直接返回,不在刷新
			return;
		}

		//如果数据有变化
		imageIconInfoList.clear();
		imageIconInfoList.addAll(newIconList);

		if (CollectionUtils.isEmpty(imageIconInfoList)) {
			clear(iconListRecyclerView);
			return;
		}
		emptyHeaderView.removeFrom(iconListRecyclerView);
		editItemRecyclerViewAdapter.notifyDataSetChanged();
	}

	/**
	 * 清空列表展示并添加空页
	 *
	 * @param iconListRecyclerView
	 */
	private void clear(SwipeRecyclerView iconListRecyclerView) {
		//如果没有数据就添加空页
		imageIconInfoList.clear();
		editItemRecyclerViewAdapter.notifyDataSetChanged();
		emptyHeaderView.addTo(iconListRecyclerView);
	}
}