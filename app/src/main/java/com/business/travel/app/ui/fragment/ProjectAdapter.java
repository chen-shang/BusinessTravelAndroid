package com.business.travel.app.ui.fragment;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.ui.fragment.ProjectAdapter.ProjectAdapterHolder;
import com.business.travel.utils.JacksonUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 * 项目页面下拉列表
 */
public class ProjectAdapter extends RecyclerView.Adapter<ProjectAdapterHolder> {
	private final List<Project> list;

	public ProjectAdapter(List<Project> list) {this.list = list;}

	@NonNull
	@NotNull
	@Override
	public ProjectAdapterHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_item, parent, false);
		return new ProjectAdapterHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull ProjectAdapterHolder holder, int position) {
		Project project = list.get(position);
		holder.textViewName.setText(project.getName());
		holder.cardView.setOnClickListener(v -> {
			ToastUtils.showShort(JacksonUtil.toString(project));
		});
	}

	@Override
	public int getItemCount() {
		return list.size();
	}

	class ProjectAdapterHolder extends ViewHolder {
		@BindView(R.id.text_view_name)
		public TextView textViewName;
		@BindView(R.id.TextView_time)
		public TextView statementShow;
		@BindView(R.id.card_view)
		public CardView cardView;

		public ProjectAdapterHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			ButterKnife.bind(this, itemView);
		}
	}
}
