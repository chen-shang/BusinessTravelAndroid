package com.business.travel.app.ui.activity.master.fragment;

import java.time.DayOfWeek;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import com.business.travel.app.R;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.ui.activity.master.fragment.BillRecyclerViewAdapter.BillRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.utils.DateTimeUtil;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class BillRecyclerViewAdapter extends BaseRecyclerViewAdapter<BillRecyclerViewAdapterViewHolder, String> {
	private final Project project;

	public BillRecyclerViewAdapter(Project project, List<String> dataList, BaseActivity<? extends ViewBinding> baseActivity) {
		super(dataList, baseActivity);
		this.project = project;
	}

	@NonNull
	@NotNull
	@Override
	public BillRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_dashboard_item, parent, false);
		return new BillRecyclerViewAdapterViewHolder(view) {
		};
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull BillRecyclerViewAdapterViewHolder holder, int position) {
		String datetime = dataList.get(position);

		TextView textView1 = holder.itemView.findViewById(R.id.UI_BillItem_TextView_Date);
		DayOfWeek dayOfWeek = DateTimeUtil.parseLocalDateTime(datetime, "yyyy-MM-dd").getDayOfWeek();
		textView1.setText(datetime + " " + dayOfWeek);
		TextView textView2 = holder.itemView.findViewById(R.id.UI_BillItem_TextView_PAY);
		SwipeRecyclerView recyclerView = holder.itemView.findViewById(R.id.UI_BillItem_SwipeRecyclerView_List);
		LayoutManager layoutManager2 = new LinearLayoutManager(activity.getApplicationContext(), RecyclerView.VERTICAL, false);
		recyclerView.setLayoutManager(layoutManager2);

		List<Bill> bills = AppDatabase.getInstance(activity).billDao().selectByProjectIdAndConsumeDate(project.getId(), datetime);
		Adapter adapter = new Adapter() {
			@NonNull
			@NotNull
			@Override
			public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_bill_item, parent, false);
				return new ViewHolder(view) {
				};
			}

			@Override
			public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
				ImageView billIcon = holder.itemView.findViewById(R.id.bill_icon);
				TextView textView = holder.itemView.findViewById(R.id.bill_info);
				TextView bill_info2 = holder.itemView.findViewById(R.id.bill_info2);
				Bill bill = bills.get(position);
				billIcon.setImageResource(R.drawable.bill_icon_daily);
				textView.setText(bill.getName());
				//根据同行人ID转换成名字 TODO
				bill_info2.setText(bill.getAssociateId());
				TextView bill_info3 = holder.itemView.findViewById(R.id.bill_info3);
				bill_info3.setText(String.valueOf(bill.getAmount()));
			}

			@Override
			public int getItemCount() {
				return bills.size();
			}
		};
		recyclerView.setAdapter(adapter);
	}

	static class BillRecyclerViewAdapterViewHolder extends ViewHolder {
		public BillRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
		}
	}
}
