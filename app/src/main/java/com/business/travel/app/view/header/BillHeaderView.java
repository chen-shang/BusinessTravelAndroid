package com.business.travel.app.view.header;

import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.business.travel.app.R;

/**
 * 账单顶部信息
 */
public class BillHeaderView extends BaseHeadView {
	public TextView uIBillFragmentTextViewIncome;
	public TextView uIBillFragmentTextViewPay;
	public TextView startTime;
	public TextView endTime;
	public TextView durationDay;

	public BillHeaderView(LayoutInflater layoutInflater) {
		headView = layoutInflater.inflate(R.layout.bill_list_header_view, null, false);
		headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		uIBillFragmentTextViewIncome = headView.findViewById(R.id.UIBillFragmentTextViewIncome);
		uIBillFragmentTextViewPay = headView.findViewById(R.id.UIBillFragmentTextViewPay);
		startTime = headView.findViewById(R.id.startTime);
		endTime = headView.findViewById(R.id.endTime);
		durationDay = headView.findViewById(R.id.durationDay);
	}

	/**
	 * 重置所有属性
	 */
	public void reset() {
		uIBillFragmentTextViewIncome.setText(null);
		uIBillFragmentTextViewPay.setText(null);
		startTime.setText(null);
		endTime.setText(null);
		durationDay.setText(null);
	}
}
