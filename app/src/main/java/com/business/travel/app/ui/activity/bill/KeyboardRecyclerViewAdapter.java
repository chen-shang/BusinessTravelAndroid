package com.business.travel.app.ui.activity.bill;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.R;
import com.business.travel.app.ui.activity.bill.KeyboardRecyclerViewAdapter.KeyboardRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.LogToast;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class KeyboardRecyclerViewAdapter extends BaseRecyclerViewAdapter<KeyboardRecyclerViewAdapterViewHolder, Integer> {

	private OnClickListener onSaveClick;
	private OnClickListener onDeleteClick;
	private OnClickListener onReRecordClick;

	public KeyboardRecyclerViewAdapter(List<Integer> integers, BaseActivity<? extends ViewBinding> baseActivity) {
		super(integers, baseActivity);
	}

	public KeyboardRecyclerViewAdapter onSaveClick(OnClickListener onSaveClick) {
		this.onSaveClick = onSaveClick;
		return this;
	}

	public KeyboardRecyclerViewAdapter onDeleteClick(OnClickListener onDeleteClick) {
		this.onDeleteClick = onDeleteClick;
		return this;
	}

	public KeyboardRecyclerViewAdapter onReRecordClick(OnClickListener onReRecordClick) {
		this.onReRecordClick = onReRecordClick;
		return this;
	}

	@NonNull
	@NotNull
	@Override
	public KeyboardRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
		switch (viewType) {
			case 3:
				//date 日期按钮样式
				return new KeyboardRecyclerViewAdapterViewHolder(layoutInflater.inflate(R.layout.recyclerview_keyboard_item_date, parent, false));
			case 14:
				//回退按钮样式
				return new KeyboardRecyclerViewAdapterViewHolder(layoutInflater.inflate(R.layout.recyclerview_keyboard_item_back, parent, false));
			default:
				return new KeyboardRecyclerViewAdapterViewHolder(layoutInflater.inflate(R.layout.recyclerview_keyboard_item_num, parent, false));
		}
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull KeyboardRecyclerViewAdapterViewHolder holder, int position) {
		switch (position) {
			case 3:
				//date 日期按钮样式
				holder.dateTextView.setOnClickListener(v -> {
					LogToast.infoShow("弹出日历选框");
				});
				break;
			case 14:
				//回退按钮
				holder.backImageButton.setOnClickListener(v -> {
					String amount = holder.amountTextView.getText().toString();
					if (StringUtils.isBlank(amount)) {
						return;
					}
					String newAmount = amount.trim().substring(0, amount.trim().length() - 1);
					holder.amountTextView.setText(newAmount);
				});
				break;
			case 7:
				//正号
				holder.numButton.setText("+");
				break;
			case 11:
				//负号
				holder.numButton.setText("-");
				break;
			case 15:
				holder.itemView.setBackgroundColor(ColorUtils.getColor(R.color.teal_800));
				holder.numButton.setText("保存");
				holder.numButton.setOnClickListener(onSaveClick);
				break;
			case 0:
				holder.numButton.setText("1");
				holder.numButton.setOnClickListener(v -> holder.amountTextView.append("1"));
				break;
			case 1:
				holder.numButton.setText("2");
				holder.numButton.setOnClickListener(v -> holder.amountTextView.append("2"));
				break;
			case 2:
				holder.numButton.setText("3");
				holder.numButton.setOnClickListener(v -> holder.amountTextView.append("3"));
				break;
			case 8:
				holder.numButton.setText("7");
				holder.numButton.setOnClickListener(v -> holder.amountTextView.append("7"));
				break;
			case 9:
				holder.numButton.setText("8");
				holder.numButton.setOnClickListener(v -> holder.amountTextView.append("8"));
				break;
			case 10:
				holder.numButton.setText("9");
				holder.numButton.setOnClickListener(v -> holder.amountTextView.append("9"));
				break;
			case 13:
				holder.numButton.setText("0");
				holder.numButton.setOnClickListener(v -> holder.amountTextView.append("0"));
				break;
			case 12:
				holder.numButton.setText(".");
				holder.numButton.setOnClickListener(v -> {
					if (holder.amountTextView.getText().toString().contains(".")) {
						return;
					}
					holder.amountTextView.append(".");
				});
				break;
			default:
				holder.numButton.setText(String.valueOf(position));
				holder.numButton.setOnClickListener(v -> holder.amountTextView.append(String.valueOf(position)));
		}
	}

	@Override
	public int getItemCount() {
		return 16;
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	class KeyboardRecyclerViewAdapterViewHolder extends ViewHolder {
		public TextView dateTextView;
		public Button numButton;
		public ImageButton backImageButton;
		public TextView amountTextView;

		public KeyboardRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
			dateTextView = itemView.findViewById(R.id.UI_KeyboardItem_TextView_Date);
			amountTextView = activity.findViewById(R.id.UI_AddBillActivity_TextView_Amount);
			numButton = itemView.findViewById(R.id.UI_KeyboardItem_Button_Num);
			backImageButton = itemView.findViewById(R.id.UI_KeyboardItem_ImageButton_Back);
		}
	}
}
