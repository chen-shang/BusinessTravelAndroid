package com.business.travel.app.ui.activity.bill;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.databinding.ActivityDetailBillBinding;
import com.business.travel.app.enums.ItemTypeEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.ImageLoadUtil;
import com.business.travel.utils.SplitUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

/**
 * 账单详情页面
 */
public class DetailBillActivity extends BaseActivity<ActivityDetailBillBinding> {

	/**
	 * 成员图标
	 */
	private final List<ImageIconInfo> memberIconList = new ArrayList<>();
	/**
	 * 成员列表适配器
	 */
	private ItemIconRecyclerViewAdapter memberRecyclerViewAdapter;
	/**
	 * 当前被选中的账单信息
	 */
	private Bill selectedBill;
	//注入service
	private BillService billService;
	private MemberService memberService;

	@Override
	protected void inject() {
		billService = new BillService(this);
		memberService = new MemberService(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long selectBillId = getIntent().getExtras().getLong("selectBillId");

		initSelectedBill(selectBillId);
		//注册返回按钮操作事件
		registerImageButtonBack();

		SwipeRecyclerView swipeRecyclerView = findViewById(R.id.UI_SwipeRecyclerView_ConsumerItem);
		LayoutManager layoutManager = new GridLayoutManager(this, 5);
		swipeRecyclerView.setLayoutManager(layoutManager);
		memberRecyclerViewAdapter = new ItemIconRecyclerViewAdapter(ItemTypeEnum.MEMBER, memberIconList, this);
		swipeRecyclerView.setAdapter(memberRecyclerViewAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();
		showBill(selectedBill);
	}

	private void showBill(Bill selectedBill) {
		ImageView imageView = findViewById(R.id.UI_ImageView_Icon);
		ImageLoadUtil.loadImageToView(selectedBill.getIconDownloadUrl(), imageView);
		TextView textView = findViewById(R.id.UI_TextView_Description);
		textView.setText(selectedBill.getConsumptionIds());

		String memberIds = selectedBill.getMemberIds();
		List<Long> longs = SplitUtil.trimToLongList(memberIds);
		final List<ImageIconInfo> imageIconInfos = memberService.queryAll(longs);
		memberIconList.clear();
		memberIconList.addAll(imageIconInfos);
		memberRecyclerViewAdapter.notifyDataSetChanged();
	}

	private void registerImageButtonBack() {
		//返回按钮点击后
		viewBinding.UIDetailBillImageButtonBack.setOnClickListener(v -> {
			this.finish();
		});
	}

	private void initSelectedBill(long selectBillId) {
		if (selectBillId <= 0) {
			return;
		}
		selectedBill = billService.queryBillById(selectBillId);
	}
}