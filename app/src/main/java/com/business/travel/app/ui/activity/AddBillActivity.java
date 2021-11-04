package com.business.travel.app.ui.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityAddBillBinding;
import com.business.travel.app.enums.IconEnum;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.fragment.DashBoardSharedData;
import com.business.travel.app.ui.fragment.DashboardFragment;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 * 添加账单
 */
public class AddBillActivity extends BaseActivity<ActivityAddBillBinding> {

	private final List<ImageIconInfo> iconList = new ArrayList<>();
	private final List<ImageIconInfo> associateList = new ArrayList<>();

	private BillDao billDao;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Objects.requireNonNull(getSupportActionBar()).hide();
		DashBoardSharedData dataBinding = ((DashboardFragment)MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getFragment()).getDataBinding();
		Project project = dataBinding.getProject();
		if (project != null) {
			viewBinding.UIAddBillActivityTextViewProjectName.setText(project.getName());
		}
		billDao = AppDatabase.getInstance(this).billDao();
		//todo 删除
		mock();

		LayoutManager layoutManager = new GridLayoutManager(this, 5);
		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setLayoutManager(layoutManager);
		viewBinding.UIAddBillActivitySwipeRecyclerViewBill.setAdapter(new IconRecyclerViewAdapter(iconList, this));

		//同行人列表
		LayoutManager layoutManager2 = new GridLayoutManager(this, 5);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setLayoutManager(layoutManager2);
		viewBinding.UIAddBillActivitySwipeRecyclerViewAssociate.setAdapter(new IconRecyclerViewAdapter(associateList, this));

		GridLayoutManager layoutManager3 = new GridLayoutManager(this, 4) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		viewBinding.UIAddBillActivitySwipeRecyclerViewKeyboard.setLayoutManager(layoutManager3);
		viewBinding.UIAddBillActivitySwipeRecyclerViewKeyboard.setAdapter(new Adapter() {
			@NonNull
			@NotNull
			@Override
			public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
				View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_keyboard_item, parent, false);
				return new ViewHolder(view) {
				};
			}

			@Override
			public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
				Button button = holder.itemView.findViewById(R.id.num);
				switch (position) {
					case 3:
						button.setText("删除");
						break;
					case 7:
						button.setText("-");
						break;
					case 11:
						button.setText("+");
						break;
					case 15:
						button.setText("保存");
						button.setOnClickListener(v -> {
							DashboardFragment fragment = (DashboardFragment)MasterFragmentPositionEnum.DASHBOARD_FRAGMENT.getFragment();
							DashBoardSharedData dataBinding = fragment.getDataBinding();
							Project project1 = dataBinding.getProject();
							if (project1 == null) {
								ToastUtils.showLong("请先创建项目");
								return;
							}
							List<ImageIconInfo> collect = iconList.stream().filter(ImageIconInfo::isSelected).collect(Collectors.toList());
							String name = collect.stream().map(ImageIconInfo::getName).collect(Collectors.joining(","));
							Bill bill = new Bill();
							bill.setName(name);
							bill.setProjectId(project1.getId());
							bill.setAmount(0L);
							bill.setConsumeTime(DateTimeUtil.format(new Date()));
							bill.setAssociateId("12345");
							bill.setCreateTime("12345");
							bill.setModifyTime("1234");
							bill.setRemark("123456");
							billDao.insert(bill);
							ToastUtils.showLong(JacksonUtil.toString(collect) + "\n" + JacksonUtil.toString(dataBinding) + "\n" + JacksonUtil.toString(bill));
						});
						break;
					case 0:
						button.setText("1");
						break;
					case 1:
						button.setText("2");
						break;
					case 2:
						button.setText("3");
						break;
					case 8:
						button.setText("7");
						break;
					case 9:
						button.setText("8");
						break;
					case 10:
						button.setText("9");
						break;
					case 13:
						button.setText("0");
						break;
					case 12:
						button.setText(".");
						break;
					case 14:
						button.setText("再记");
						break;
					default:
						button.setText(String.valueOf(position));
				}
			}

			@Override
			public int getItemCount() {
				return 16;
			}
		});
	}

	private void mock() {
		Arrays.stream(IconEnum.values()).forEach(iconEnum -> {
			ImageIconInfo imageIconInfo = new ImageIconInfo();
			imageIconInfo.setResourceId(iconEnum.getResourceId());
			imageIconInfo.setName(iconEnum.getDescription());
			imageIconInfo.setSelected(false);
			iconList.add(imageIconInfo);
		});

		ImageIconInfo imageAddIconInfo = new ImageIconInfo();
		imageAddIconInfo.setResourceId(R.drawable.bill_icon_add);
		imageAddIconInfo.setName("添加");
		iconList.add(imageAddIconInfo);

		ImageIconInfo imageAddIconInfoMe = new ImageIconInfo();
		imageAddIconInfoMe.setResourceId(R.drawable.vector_drawable_my);
		imageAddIconInfoMe.setName("我");
		associateList.add(imageAddIconInfoMe);

		ImageIconInfo imageAddIconInfo2 = new ImageIconInfo();
		imageAddIconInfo2.setResourceId(R.drawable.bill_icon_add);
		imageAddIconInfo2.setName("添加");
		associateList.add(imageAddIconInfo2);
	}
}