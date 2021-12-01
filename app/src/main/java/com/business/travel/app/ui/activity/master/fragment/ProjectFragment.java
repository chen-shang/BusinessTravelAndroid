package com.business.travel.app.ui.activity.master.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.FragmentProjectBinding;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.master.MasterActivity;
import com.business.travel.app.ui.base.BaseFragment;
import com.business.travel.app.ui.base.ShareData;
import com.lxj.xpopup.XPopup.Builder;
import com.lxj.xpopup.impl.AttachListPopupView;
import com.lxj.xpopup.impl.InputConfirmPopupView;

/**
 * @author chenshang
 */
public class ProjectFragment extends BaseFragment<FragmentProjectBinding, ShareData> {

	/**
	 * 项目列表
	 */
	private final List<Project> projects = new ArrayList<>();
	/**
	 * 项目列表适配器
	 */
	private ProjectRecyclerViewAdapter projectListRecyclerViewAdapter;

	private ProjectService projectService;
	/**
	 * 列表为空时候显示的内容,用headView实现该效果
	 */
	private View headView;

	@Override
	protected void inject() {
		super.inject();
		projectService = new ProjectService(requireActivity());
	}

	@Override
	public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = super.onCreateView(inflater, container, savedInstanceState);
		headView = getLayoutInflater().inflate(R.layout.base_empty_list, null);
		headView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
		projectListRecyclerViewAdapter = new ProjectRecyclerViewAdapter(projects, (MasterActivity)requireActivity());
		viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.setAdapter(projectListRecyclerViewAdapter);

		//这个是添加新项目时候弹出的
		final InputConfirmPopupView projectNameInputPopupView = new Builder(getContext()).asInputConfirm(null, "请输入新项目名称", text -> {
			projectService.createIfNotExist(text);
			refreshProjectList();
		});
		//这个是点击右上角三个小圆点弹出的
		final AttachListPopupView attachListPopupView = new Builder(getContext()).atView(viewBinding.UIProjectFragmentImageViewOther).asAttachList(new String[] {"添加项目"}, new int[] {R.drawable.ic_base_sort}, (position, text) -> projectNameInputPopupView.show());
		viewBinding.UIProjectFragmentImageViewOther.setOnClickListener(v -> attachListPopupView.show());

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		// 每次进来的时候,都要刷新一下项目列表
		refreshProjectList();
	}

	/**
	 * 刷新项目列表
	 * TODO: 后续改成分页
	 */
	public void refreshProjectList() {
		//在查询全量
		final List<Project> allProjects = projectService.queryAll();
		if (CollectionUtils.isEmpty(allProjects)) {
			showEmptyHeader();
			return;
		}

		viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.removeHeaderView(headView);
		projects.clear();
		projects.addAll(allProjects);
		//通知adapter更新列表
		projectListRecyclerViewAdapter.notifyDataSetChanged();
	}

	private void showEmptyHeader() {
		projects.clear();
		projectListRecyclerViewAdapter.notifyDataSetChanged();
		if (viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.getHeaderCount() == 0) {
			viewBinding.UIProjectFragmentSwipeRecyclerViewProjectList.addHeaderView(headView);
		}
	}
}