package com.business.travel.app.ui.activity.master;

import java.time.LocalDate;
import java.util.Optional;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;
import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.BarUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityMasterBinding;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.enums.OperateTypeEnum;
import com.business.travel.app.model.BillAddModel;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.activity.bill.AddBillActivity;
import com.business.travel.app.ui.activity.bill.AddBillActivity.IntentKey;
import com.business.travel.app.ui.activity.bill.fragment.BillFragment;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.LogToast;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class MasterActivity extends BaseActivity<ActivityMasterBinding> {

	private final BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();

	private ProjectService projectService;

	/**
	 * 上一次有返回动作的时间
	 */
	private long lastBackPressedTime;

	@Override
	public void onBackPressed() {
		//从广告页面跳转过来后不在支持跳转回去
		//连续两次返回直接退出应用回到主界面
		if (System.currentTimeMillis() - lastBackPressedTime < 1200) {
			this.finish();
			ActivityUtils.startHomeActivity();
		} else {
			lastBackPressedTime = System.currentTimeMillis();
			LogToast.infoShow("再按一次退出");
		}
	}

	@Override
	protected void inject() {
		super.inject();
		projectService = new ProjectService(this);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//因为主界面用的白色背景,因此标题需要反差才行
		BarUtils.setStatusBarLightMode(this, true);
		//初始化viewPage控件
		registerViewPager(viewBinding.UIMasterActivityViewPager2);
		//初始化navView控件
		viewBinding.UIMasterActivityBottomNavigationView.setOnItemSelectedListener(this::buildBottomNavigationViewOnItemSelectedListener);
		//初始化中间按钮点击功能
		registerFloatingActionButton(viewBinding.UIMasterActivityFloatingActionButton);
	}

	/**
	 * 初始化中间按钮点击功能
	 */
	private void registerFloatingActionButton(FloatingActionButton floatingActionButton) {
		ViewPager2 viewPager2 = viewBinding.UIMasterActivityViewPager2;

		floatingActionButton.setOnClickListener(view -> {
			int position = viewPager2.getCurrentItem();
			if (MasterFragmentPositionEnum.BILL_FRAGMENT.getPosition() != position) {
				//如果当前不是 DASHBOARD_FRAGMENT 页面,当点击+号的时候显示DASHBOARD_FRAGMENT页面
				//当显示DASHBOARD_FRAGMENT页面的时候回自动执行上升和放大的动画
				viewPager2.setCurrentItem(MasterFragmentPositionEnum.BILL_FRAGMENT.getPosition());
			} else {
				//如果是DASHBOARD_FRAGMENT页面,当点击的时候则跳转到新增账单页面
				Intent intent = new Intent(this, AddBillActivity.class);
				intent.putExtra(IntentKey.operateType, OperateTypeEnum.ADD);

				Project project = projectService.queryById(billFragment.getSelectedProjectId());
				String name = Optional.ofNullable(project).map(Project::getName).orElse("");
				BillAddModel billAddModel = new BillAddModel(name, DateTimeUtil.timestamp(LocalDate.now()), ConsumptionTypeEnum.SPENDING.name());
				intent.putExtra(IntentKey.billAddModel, JacksonUtil.toString(billAddModel));
				startActivity(intent);
			}
		});
	}

	/**
	 * 初始化viewPage控件
	 */
	private void registerViewPager(ViewPager2 viewPager2) {
		viewPager2.setAdapter(buildViewPagerFragmentStateAdapter());
		//默认第一页面展示DASHBOARD_FRAGMENT
		viewPager2.setCurrentItem(MasterFragmentPositionEnum.BILL_FRAGMENT.getPosition());
		viewPager2.registerOnPageChangeCallback(new OnPageChangeCallback() {
			@Override
			public void onPageSelected(int position) {
				super.onPageSelected(position);
				viewBinding.UIMasterActivityBottomNavigationView.getMenu().getItem(position).setChecked(true);
			}
		});
	}

	/**
	 * 底部栏点击后的动作
	 *
	 * @param item
	 * @return
	 */
	@SuppressLint("NonConstantResourceId")
	private boolean buildBottomNavigationViewOnItemSelectedListener(MenuItem item) {
		int itemId = item.getItemId();
		switch (itemId) {
			case R.id.navigation_project:
				viewBinding.UIMasterActivityViewPager2.setCurrentItem(MasterFragmentPositionEnum.PROJECT_FRAGMENT.getPosition());
				break;
			case R.id.navigation_dashboard:
				viewBinding.UIMasterActivityViewPager2.setCurrentItem(MasterFragmentPositionEnum.BILL_FRAGMENT.getPosition());
				break;
			case R.id.navigation_my:
				viewBinding.UIMasterActivityViewPager2.setCurrentItem(MasterFragmentPositionEnum.MY_FRAGMENT.getPosition());
				break;
			default:
				//do nothing
		}
		return true;
	}

	/**
	 * 主界面的viewpager加载的fragment页面适配器
	 *
	 * @return
	 */
	private FragmentStateAdapter buildViewPagerFragmentStateAdapter() {
		return new FragmentStateAdapter(this) {
			@NotNull
			@Override
			public Fragment createFragment(int position) {
				return MasterFragmentPositionEnum.of(position).getFragment();
			}

			@Override
			public int getItemCount() {
				return MasterFragmentPositionEnum.values().length;
			}
		};
	}
}