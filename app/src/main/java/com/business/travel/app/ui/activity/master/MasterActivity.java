package com.business.travel.app.ui.activity.master;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;
import com.blankj.utilcode.util.ActivityUtils;
import com.business.travel.app.R;
import com.business.travel.app.databinding.ActivityMasterBinding;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.ui.activity.bill.AddBillActivity;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.utils.LogToast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class MasterActivity extends BaseActivity<ActivityMasterBinding> {

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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
				startActivity(new Intent(this, AddBillActivity.class));
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