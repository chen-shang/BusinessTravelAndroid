package com.business.travel.app.ui.activity.item.consumption;

import android.os.Bundle;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.databinding.ActivityDetailConsumptionBinding;
import com.business.travel.app.service.BillService;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.LogToast;
import com.business.travel.utils.JacksonUtil;

public class DetailConsumptionActivity extends BaseActivity<ActivityDetailConsumptionBinding> {

	private final BillService billService = new BillService(this);
	private Bill selectedBill;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		long selectBillId = getIntent().getExtras().getLong("selectBillId");
		initSelectedBill(selectBillId);
	}

	private void initSelectedBill(long selectBillId) {
		if (selectBillId <= 0) {
			return;
		}

		selectedBill = billService.queryBillById(selectBillId);

		LogToast.infoShow(JacksonUtil.toPrettyString(selectedBill));
	}
}