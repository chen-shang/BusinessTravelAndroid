package com.business.travel.app.view;

import android.view.View;
import android.widget.TextView;
import com.business.travel.app.R;

/**
 * 项目顶部信息
 */
public class ProjectHeaderView {
	public TextView projectIncome;
	public TextView projectPay;
	public TextView projectCount;
	public TextView endTime;
	public TextView durationDay;

	public static ProjectHeaderView init(View listHeadView) {
		ProjectHeaderView holder = new ProjectHeaderView();
		holder.projectIncome = listHeadView.findViewById(R.id.UIProjectFragmentTextViewIncome);
		holder.projectPay = listHeadView.findViewById(R.id.UIProjectFragmentTextViewPay);
		holder.projectCount = listHeadView.findViewById(R.id.projectCount);

		//holder.startTime = listHeadView.findViewById(R.id.startTime);
		//holder.endTime = listHeadView.findViewById(R.id.endTime);
		holder.durationDay = listHeadView.findViewById(R.id.durationDay);
		return holder;
	}
}
