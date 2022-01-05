package com.business.travel.app.view;

import android.view.View;
import android.widget.TextView;
import com.business.travel.app.R;

/**
 * 账单顶部信息
 */
public class BillHeaderView {
	public TextView uIBillFragmentTextViewIncome;
	public TextView uIBillFragmentTextViewPay;
	public TextView startTime;
	public TextView endTime;
	public TextView durationDay;

	public static BillHeaderView init(View listHeadView) {
		BillHeaderView holder = new BillHeaderView();
		holder.uIBillFragmentTextViewIncome = listHeadView.findViewById(R.id.UIBillFragmentTextViewIncome);
		holder.uIBillFragmentTextViewPay = listHeadView.findViewById(R.id.UIBillFragmentTextViewPay);
		holder.startTime = listHeadView.findViewById(R.id.startTime);
		holder.endTime = listHeadView.findViewById(R.id.endTime);
		holder.durationDay = listHeadView.findViewById(R.id.durationDay);
		return holder;
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
