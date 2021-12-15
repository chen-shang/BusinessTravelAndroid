package com.business.travel.app.ui.activity.bill;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.widget.ImageView;
import androidx.recyclerview.widget.GridLayoutManager;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.databinding.ActivityDetailBillBinding;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.ImageLoadUtil;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.app.utils.Try;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.SplitUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.business.travel.vo.enums.ItemTypeEnum;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

/**
 * 账单详情页面
 */
public class DetailBillActivity extends BaseActivity<ActivityDetailBillBinding> {

	/**
	 * 成员图标
	 */
	private final List<ImageIconInfo> memberIconList = new ArrayList<>();
	private final List<ImageIconInfo> consumptionIconList = new ArrayList<>();

	/**
	 * 成员列表适配器
	 */
	private ItemIconRecyclerViewAdapter memberRecyclerViewAdapter;
	private ItemIconRecyclerViewAdapter memberRecyclerViewAdapter2;

	/**
	 * 当前被选中的账单信息
	 */
	private Bill selectedBill;
	//注入service
	private BillService billService;
	private MemberService memberService;
	private ConsumptionService consumptionService;

	@Override
	protected void inject() {
		billService = new BillService(this);
		memberService = new MemberService(this);
		consumptionService = new ConsumptionService(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		long selectBillId = getIntent().getExtras().getLong("selectBillId");

		initSelectedBill(selectBillId);
		//注册返回按钮操作事件
		registerImageButtonBack();

		SwipeRecyclerView swipeRecyclerView = findViewById(R.id.UI_SwipeRecyclerView_Member);
		swipeRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
		memberRecyclerViewAdapter = new ItemIconRecyclerViewAdapter(ItemTypeEnum.MEMBER, memberIconList, this);
		swipeRecyclerView.setAdapter(memberRecyclerViewAdapter);

		SwipeRecyclerView swipeRecyclerView2 = findViewById(R.id.UI_SwipeRecyclerView_Consuption);
		swipeRecyclerView2.setLayoutManager(new GridLayoutManager(this, 5));
		memberRecyclerViewAdapter2 = new ItemIconRecyclerViewAdapter(ItemTypeEnum.CONSUMPTION, consumptionIconList, this);
		swipeRecyclerView2.setAdapter(memberRecyclerViewAdapter2);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Try.of(() -> showBill(selectedBill));
	}

	private void showBill(Bill bill) {
		ImageView imageView = findViewById(R.id.UI_ImageView_Icon);
		ImageLoadUtil.loadImageToView(bill.getIconDownloadUrl(), imageView);

		String consumptionIds = bill.getConsumptionIds();
		final List<Long> ids = SplitUtil.trimToLongList(consumptionIds);
		List<ImageIconInfo> consumptions = consumptionService.queryByIds(ids);
		consumptionIconList.clear();
		consumptionIconList.addAll(consumptions);
		memberRecyclerViewAdapter2.notifyDataSetChanged();

		String memberIds = bill.getMemberIds();
		List<Long> longs = SplitUtil.trimToLongList(memberIds);
		final List<ImageIconInfo> imageIconInfos = memberService.queryAll(longs);
		memberIconList.clear();
		memberIconList.addAll(imageIconInfos);
		memberRecyclerViewAdapter.notifyDataSetChanged();

		viewBinding.ammount.setText(MoneyUtil.toYuanString(bill.getAmount()));

		viewBinding.time.setText(DateTimeUtil.format(bill.getConsumeDate(), "yyyy-MM-dd"));

		viewBinding.remark.setText(bill.getRemark());

		String consumptionType = bill.getConsumptionType();
		viewBinding.consumerType.setText(ConsumptionTypeEnum.valueOf(consumptionType).getMsg());
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