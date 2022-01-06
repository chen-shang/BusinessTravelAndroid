package com.business.travel.app.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.business.travel.app.R;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;

/**
 * 账单顶部信息
 */
public class BillHeaderView {
	public TextView uIBillFragmentTextViewIncome;
	public TextView uIBillFragmentTextViewPay;
	public TextView startTime;
	public TextView endTime;
	public TextView durationDay;

	//账单页面顶部视图
	private final View headView;

	public BillHeaderView(LayoutInflater layoutInflater) {
		headView = layoutInflater.inflate(R.layout.bill_list_header_view, null, false);
		headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		uIBillFragmentTextViewIncome = headView.findViewById(R.id.UIBillFragmentTextViewIncome);
		uIBillFragmentTextViewPay = headView.findViewById(R.id.UIBillFragmentTextViewPay);
		startTime = headView.findViewById(R.id.startTime);
		endTime = headView.findViewById(R.id.endTime);
		durationDay = headView.findViewById(R.id.durationDay);
	}

	public void addTo(SwipeRecyclerView swipeRecyclerView) {
		if (swipeRecyclerView.getHeaderCount() < 2) {
			swipeRecyclerView.addHeaderView(headView);
		}
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
