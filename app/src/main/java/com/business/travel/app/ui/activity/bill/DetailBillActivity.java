package com.business.travel.app.ui.activity.bill;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.databinding.ActivityDetailBillBinding;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.ui.base.ColorStatusBarActivity;
import com.business.travel.app.utils.GridViewPagerUtil;
import com.business.travel.app.utils.ImageLoadUtil;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.app.utils.Try;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.SplitUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.business.travel.vo.enums.WeekEnum;

/**
 * 账单详情页面
 */
public class DetailBillActivity extends ColorStatusBarActivity<ActivityDetailBillBinding> {

	/**
	 * 默认展示6列
	 */
	private static final int COLUMN_COUNT = 6;
	/**
	 * 成员图标
	 */
	private final List<ImageIconInfo> memberIconList = new ArrayList<>();
	/**
	 * 消费项图标列表
	 */
	private final List<ImageIconInfo> consumptionIconList = new ArrayList<>();
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
		//注册消费项列表
		registerConsumptionPageView();
		//注册人员列表
		registerMemberPageView();
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (selectBillId == null || selectBillId <= 0) {
			return;
		}
		Try.of(() -> showBill(billService.queryBillById(selectBillId)));
	}

	private void registerMemberPageView() {
		GridViewPagerUtil.registerPageViewCommonProperty(viewBinding.GridViewPagerMemberIconList)
		                 // 设置数据总数量
		                 .setDataAllCount(memberIconList.size())
		                 // 设置每页行数 // 设置每页列数
		                 .setColumnCount(COLUMN_COUNT)
		                 // 数据绑定
		                 .setImageTextLoaderInterface((imageView, textView, position) -> {
			                 // 自己进行数据的绑定，灵活度更高，不受任何限制
			                 bind(imageView, textView, memberIconList.get(position));
		                 });
	}

	private void registerConsumptionPageView() {
		GridViewPagerUtil.registerPageViewCommonProperty(viewBinding.GridViewPagerConsumptionIconList)
		                 // 设置数据总数量
		                 .setDataAllCount(consumptionIconList.size())
		                 // 设置每页行数 // 设置每页列数
		                 .setColumnCount(COLUMN_COUNT)
		                 // 数据绑定
		                 .setImageTextLoaderInterface((imageView, textView, position) -> {
			                 // 自己进行数据的绑定，灵活度更高，不受任何限制
			                 bind(imageView, textView, consumptionIconList.get(position));
		                 });
	}

	@SuppressLint("SetTextI18n")
	private void showBill(Bill bill) {

		//消费项列表
		String consumptionIds = bill.getConsumptionIds();
		List<Long> consumptionIdList = SplitUtil.trimToLongList(consumptionIds);
		List<ImageIconInfo> consumptions = consumptionService.queryByIds(consumptionIdList);
		consumptionIconList.clear();
		consumptionIconList.addAll(consumptions);
		viewBinding.GridViewPagerConsumptionIconList.setDataAllCount(consumptionIconList.size()).setRowCount((int)Math.ceil((consumptionIconList.size() / (double)COLUMN_COUNT))).show();

		//人员列表
		String memberIds = bill.getMemberIds();
		List<Long> memberIdList = SplitUtil.trimToLongList(memberIds);
		List<ImageIconInfo> imageIconInfos = memberService.queryAll(memberIdList);
		memberIconList.clear();
		memberIconList.addAll(imageIconInfos);
		viewBinding.GridViewPagerMemberIconList.setDataAllCount(memberIconList.size()).setRowCount((int)Math.ceil((memberIconList.size() / (double)COLUMN_COUNT))).show();

		//消费金额
		viewBinding.ammount.setText(MoneyUtil.toYuanString(bill.getAmount()));

		//消费时间
		String date = DateTimeUtil.format(bill.getConsumeDate(), "yyyy-MM-dd");
		WeekEnum weekEnum = WeekEnum.ofCode(DateTimeUtil.toLocalDateTime(bill.getConsumeDate()).getDayOfWeek().getValue());
		viewBinding.time.setText(date + " " + weekEnum.getMsg());
		//消费类型
		String consumptionType = bill.getConsumptionType();
		viewBinding.consumerType.setText(ConsumptionTypeEnum.valueOf(consumptionType).getMsg());

		//备注
		viewBinding.remark.setText(bill.getRemark());
	}

	private void bind(ImageView imageView, TextView textView, ImageIconInfo imageIconInfo) {
		imageView.setBackgroundResource(R.drawable.corners_shape_unselect);
		imageView.setImageResource(R.drawable.ic_base_placeholder);
		
		textView.setText(imageIconInfo.getName());
		ImageLoadUtil.loadImageToView(imageIconInfo.getIconDownloadUrl(), imageView);
	}
}