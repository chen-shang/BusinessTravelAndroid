package com.business.travel.app.ui.activity.bill;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.databinding.ActivityDetailBillBinding;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.ui.base.ColorStatusBarActivity;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.app.utils.Try;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.SplitUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.business.travel.vo.enums.ItemTypeEnum;
import com.business.travel.vo.enums.WeekEnum;

/**
 * 账单详情页面
 */
public class DetailBillActivity extends ColorStatusBarActivity<ActivityDetailBillBinding> {

	/**
	 * 成员图标
	 */
	private final List<ImageIconInfo> memberIconList = new ArrayList<>();
	/**
	 * 消费项图标列表
	 */
	private final List<ImageIconInfo> consumptionIconList = new ArrayList<>();

	/**
	 * 成员列表适配器
	 */
	private ItemIconRecyclerViewAdapter memberIconRecyclerViewAdapter;
	/**
	 * 消费项图标适配器
	 */
	private ItemIconRecyclerViewAdapter consumptionIconRecyclerViewAdapter;

	/**
	 * 当前被选中的账单信息
	 */
	private Long selectBillId;

	//注入service
	private BillService billService;
	private MemberService memberService;
	private ConsumptionService consumptionService;

	@Override
	protected void inject() {
		billService = new BillService(this);
		memberService = new MemberService(this);
		consumptionService = new ConsumptionService(this);

		selectBillId = getIntent().getExtras().getLong("selectBillId");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		viewBinding.UISwipeRecyclerViewMember.setLayoutManager(new GridLayoutManager(this, 6));
		memberIconRecyclerViewAdapter = new ItemIconRecyclerViewAdapter(ItemTypeEnum.MEMBER, memberIconList, this);
		viewBinding.UISwipeRecyclerViewMember.setAdapter(memberIconRecyclerViewAdapter);

		viewBinding.UISwipeRecyclerViewConsuption.setLayoutManager(new GridLayoutManager(this, 6));
		consumptionIconRecyclerViewAdapter = new ItemIconRecyclerViewAdapter(ItemTypeEnum.CONSUMPTION, consumptionIconList, this);
		viewBinding.UISwipeRecyclerViewConsuption.setAdapter(consumptionIconRecyclerViewAdapter);
	}

	@Override
	protected void onStart() {
		super.onStart();

		if (selectBillId <= 0) {
			return;
		}

		Try.of(() -> showBill(billService.queryBillById(selectBillId)));
	}

	private void showBill(Bill bill) {

		String consumptionIds = bill.getConsumptionIds();
		List<Long> consumptionIdList = SplitUtil.trimToLongList(consumptionIds);
		List<ImageIconInfo> consumptions = consumptionService.queryByIds(consumptionIdList);
		consumptionIconList.clear();
		consumptionIconList.addAll(consumptions);
		consumptionIconRecyclerViewAdapter.notifyDataSetChanged();

		String memberIds = bill.getMemberIds();
		List<Long> memberIdList = SplitUtil.trimToLongList(memberIds);
		List<ImageIconInfo> imageIconInfos = memberService.queryAll(memberIdList);
		memberIconList.clear();
		memberIconList.addAll(imageIconInfos);
		memberIconRecyclerViewAdapter.notifyDataSetChanged();

		viewBinding.ammount.setText(MoneyUtil.toYuanString(bill.getAmount()));

		int code = DateTimeUtil.toLocalDateTime(bill.getConsumeDate()).getDayOfWeek().getValue();
		WeekEnum weekEnum = WeekEnum.ofCode(code);
		viewBinding.time.setText(DateTimeUtil.format(bill.getConsumeDate(), "yyyy-MM-dd") + " " + weekEnum.getMsg());

		viewBinding.remark.setText(bill.getRemark());

		String consumptionType = bill.getConsumptionType();
		viewBinding.consumerType.setText(ConsumptionTypeEnum.valueOf(consumptionType).getMsg());
	}

}