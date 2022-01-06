package com.business.travel.app.view;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;
import com.business.travel.app.R;

/**
 * 项目顶部信息
 */
public class ProjectHeaderView extends HeadView {
	public TextView projectIncome;
	public TextView projectPay;
	public TextView projectCount;
	public TextView endTime;
	public TextView durationDay;

	@SuppressLint("InflateParams")
	public ProjectHeaderView(LayoutInflater layoutInflater) {
		headView = layoutInflater.inflate(R.layout.project_list_header_view, null);
		headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		projectIncome = headView.findViewById(R.id.UIProjectFragmentTextViewIncome);
		projectPay = headView.findViewById(R.id.UIProjectFragmentTextViewPay);
		projectCount = headView.findViewById(R.id.projectCount);
		durationDay = headView.findViewById(R.id.durationDay);
	}
}
