package com.business.travel.app.ui;

import java.util.Objects;

import javax.validation.constraints.NotNull;

import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.databinding.ActivityMasterBinding;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.ui.base.BaseActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * @author chenshang
 */
public class MasterActivity extends BaseActivity<ActivityMasterBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        binding = ActivityMasterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView bottomNavigationView = findViewById(R.id.nav_view);
        binding.viewPager.setAdapter(new FragmentStateAdapter(this) {
            @RequiresApi(api = VERSION_CODES.N)
            @NonNull
            @NotNull
            @Override
            public Fragment createFragment(int position) {
                return MasterFragmentPositionEnum.of(position).getFragment();
            }

            @Override
            public int getItemCount() {
                return MasterFragmentPositionEnum.values().length;
            }
        });
        //默认第一页面展示DASHBOARD_FRAGMENT
        binding.viewPager.setCurrentItem(MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getPosition());

        //中间按钮点击功能
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingActionButton);
        floatingActionButton.setOnClickListener(view -> {
            int position = binding.viewPager.getCurrentItem();
            if (MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getPosition() != position) {
                binding.viewPager.setCurrentItem(MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getPosition());
            } else {
                //TODO
                ToastUtils.showLong("请录入账单");
            }
        });
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            switch (itemId) {
                case R.id.navigation_project:
                    binding.viewPager.setCurrentItem(MasterFragmentPositionEnum.PROJECT_FRAGMENT.getPosition());
                    break;
                case R.id.navigation_dashboard:
                    binding.viewPager.setCurrentItem(MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getPosition());
                    break;
                case R.id.navigation_my:
                    binding.viewPager.setCurrentItem(MasterFragmentPositionEnum.MY_FRAGMENT.getPosition());
                    break;
                default:
                    //do nothing
            }
            return true;
        });
        binding.viewPager.registerOnPageChangeCallback(new OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
                bottomNavigationView.getMenu().getItem(position).setChecked(true);
            }
        });
    }
}