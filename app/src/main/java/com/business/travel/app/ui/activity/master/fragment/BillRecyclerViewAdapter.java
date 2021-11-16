package com.business.travel.app.ui.activity.master.fragment;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.ui.activity.master.fragment.BillRecyclerViewAdapter.BillRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class BillRecyclerViewAdapter extends BaseRecyclerViewAdapter<BillRecyclerViewAdapterViewHolder, String> {
	final List<Bill> bills = new ArrayList<>();
	private final Project project;

	public BillRecyclerViewAdapter(Project project, List<String> dataList, BaseActivity<? extends ViewBinding> baseActivity) {
		super(dataList, baseActivity);
		this.project = project;
	}

	@NonNull
	@NotNull
	@Override
	public BillRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bill, parent, false);
		return new BillRecyclerViewAdapterViewHolder(view) {
		};
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull BillRecyclerViewAdapterViewHolder holder, int position) {
		String datetime = dataList.get(position);
		holder.dateTextView.setText(datetime);

		//根据project和日期获取当天的账单
		final BillDao billDao = AppDatabase.getInstance(activity).billDao();
		bills.clear();
		bills.addAll(billDao.selectByProjectIdAndConsumeDate(project.getId(), datetime));
		mock();
		holder.swipeRecyclerView.setLayoutManager(new LinearLayoutManager(activity.getApplicationContext()));
		holder.swipeRecyclerView.setAdapter(new BillItemRecyclerViewAdapter(bills, activity));
	}

	private void mock() {
		for (int i = 0; i < 10; i++) {
			Bill bill = new Bill();
			bills.add(bill);
		}

	}

	static class BillRecyclerViewAdapterViewHolder extends ViewHolder {

		@BindView(R.id.UI_MasterActivity_Bill_Date)
		public TextView dateTextView;
		@BindView(R.id.s1)
		public SwipeRecyclerView swipeRecyclerView;

		public BillRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
