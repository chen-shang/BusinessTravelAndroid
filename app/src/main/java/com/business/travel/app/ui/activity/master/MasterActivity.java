package com.business.travel.app.ui.activity.master;

import java.util.Objects;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;
import com.business.travel.app.R;
import com.business.travel.app.databinding.ActivityMasterBinding;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.ui.activity.bill.AddBillActivity;
import com.business.travel.app.ui.base.BaseActivity;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class MasterActivity extends BaseActivity<ActivityMasterBinding> {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();
		//初始化viewPage控件
		initViewPager();
		//初始化navView控件
		viewBinding.UIMasterActivityBottomNavigationView.setOnItemSelectedListener(this::buildBottomNavigationViewOnItemSelectedListener);
		//初始化中间按钮点击功能
		initFloatingActionButton();
	}

	/**
	 * 初始化中间按钮点击功能
	 */
	private void initFloatingActionButton() {
		viewBinding.UIMasterActivityFloatingActionButton.setOnClickListener(view -> {
			int position = viewBinding.UIMasterActivityViewPager2.getCurrentItem();
			if (MasterFragmentPositionEnum.BILL_FRAGMENT.getPosition() != position) {
				//如果当前不是 DASHBOARD_FRAGMENT 页面,当点击+号的时候显示DASHBOARD_FRAGMENT页面
				//当显示DASHBOARD_FRAGMENT页面的时候回自动执行上升和放大的动画
				viewBinding.UIMasterActivityViewPager2.setCurrentItem(MasterFragmentPositionEnum.BILL_FRAGMENT.getPosition());
			} else {
				//如果是DASHBOARD_FRAGMENT页面,当点击的时候则跳转到新增账单页面
				startActivity(new Intent(this, AddBillActivity.class));
			}
		});
	}

	/**
	 * 初始化viewPage控件
	 */
	private void initViewPager() {
		viewBinding.UIMasterActivityViewPager2.setAdapter(buildViewPagerFragmentStateAdapter());
		//默认第一页面展示DASHBOARD_FRAGMENT
		viewBinding.UIMasterActivityViewPager2.setCurrentItem(MasterFragmentPositionEnum.BILL_FRAGMENT.getPosition());
		viewBinding.UIMasterActivityViewPager2.registerOnPageChangeCallback(new OnPageChangeCallback() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
				super.onPageScrolled(position, positionOffset, positionOffsetPixels);
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