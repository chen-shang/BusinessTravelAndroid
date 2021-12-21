package com.business.travel.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.R;
import com.business.travel.app.view.Keyboard.KeyboardRecyclerViewAdapter.KeyboardRecyclerViewAdapterViewHolder;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

public class Keyboard extends ConstraintLayout {

	/**
	 * 备注编辑框
	 */
	protected EditText editTextRemark;
	/**
	 * 收入支出显示tag
	 */
	protected TextView textViewPayType;
	/**
	 * 金额展示
	 */
	protected TextView textViewAmount;

	public Keyboard(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
		super(context, attrs);
		//键盘布局
		View inflate = LayoutInflater.from(context).inflate(R.layout.layout_keyboard, this);
		//备注编辑框
		editTextRemark = inflate.findViewById(R.id.EditText_Remark);
		//收入支出显示tag
		textViewPayType = inflate.findViewById(R.id.TextView_PayType);
		//金额展示
		textViewAmount = inflate.findViewById(R.id.TextView_Amount);
		//键盘布局
		SwipeRecyclerView recyclerViewKeyboard = inflate.findViewById(R.id.RecyclerView_Keyboard);

		//4*4 布局
		GridLayoutManager gridLayoutManager = new GridLayoutManager(context, 4) {
			@Override
			public boolean canScrollVertically() {
				return false;
			}
		};
		recyclerViewKeyboard.setLayoutManager(gridLayoutManager);
		KeyboardRecyclerViewAdapter keyboardRecyclerViewAdapter = new KeyboardRecyclerViewAdapter();
		recyclerViewKeyboard.setAdapter(keyboardRecyclerViewAdapter);
	}

	class KeyboardRecyclerViewAdapter extends RecyclerView.Adapter<KeyboardRecyclerViewAdapterViewHolder> {

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
						// TODO: 2021/11/30
					});
					break;
				case 14:
					//回退按钮
					holder.backImageButton.setOnClickListener(v -> {
						String amount = textViewAmount.getText().toString();
						if (StringUtils.isBlank(amount)) {
							return;
						}
						String newAmount = amount.trim().substring(0, amount.trim().length() - 1);
						textViewAmount.setText(newAmount);
					});
					break;
				case 7:
					//正号
					holder.numButton.setText("+");
					holder.numButton.setOnClickListener(v -> {
						// TODO: 2021/11/30
					});
					break;
				case 11:
					//负号
					holder.numButton.setText("-");
					holder.numButton.setOnClickListener(v -> {
						String amount = textViewAmount.getText().toString();
						if (StringUtils.isBlank(amount)) {
							return;
						}

					});
					break;
				case 15:
					holder.itemView.setBackgroundColor(ColorUtils.getColor(R.color.red_2));
					holder.numButton.setText("保存");
					//holder.numButton.setOnClickListener(onSaveClick);
					holder.numButton.setOnLongClickListener(v -> {
						//onReRecordClick.onClick(v);
						return true;
					});
					break;
				case 0:
					holder.numButton.setText("1");
					holder.numButton.setOnClickListener(v -> textViewAmount.append("1"));
					break;
				case 1:
					holder.numButton.setText("2");
					holder.numButton.setOnClickListener(v -> textViewAmount.append("2"));
					break;
				case 2:
					holder.numButton.setText("3");
					holder.numButton.setOnClickListener(v -> textViewAmount.append("3"));
					break;
				case 8:
					holder.numButton.setText("7");
					holder.numButton.setOnClickListener(v -> textViewAmount.append("7"));
					break;
				case 9:
					holder.numButton.setText("8");
					holder.numButton.setOnClickListener(v -> textViewAmount.append("8"));
					break;
				case 10:
					holder.numButton.setText("9");
					holder.numButton.setOnClickListener(v -> textViewAmount.append("9"));
					break;
				case 13:
					holder.numButton.setText("0");
					holder.numButton.setOnClickListener(v -> textViewAmount.append("0"));
					break;
				case 12:
					holder.numButton.setText(".");
					holder.numButton.setOnClickListener(v -> {
						//如果已经包含点了,再点击没有效果
						if (textViewAmount.getText().toString().contains(".")) {
							return;
						}
						textViewAmount.append(".");
					});
					break;
				default:
					holder.numButton.setText(String.valueOf(position));
					holder.numButton.setOnClickListener(v -> textViewAmount.append(String.valueOf(position)));
			}
		}

		@Override
		public int getItemViewType(int position) {
			return position;
		}

		@Override
		public int getItemCount() {
			return 16;
		}

		class KeyboardRecyclerViewAdapterViewHolder extends ViewHolder {
			public Button numButton;
			public ImageButton backImageButton;
			public TextView dateTextView;

			public KeyboardRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
				super(itemView);
				dateTextView = itemView.findViewById(R.id.UI_KeyboardItem_TextView_Date);
				numButton = itemView.findViewById(R.id.UI_KeyboardItem_Button_Num);
				backImageButton = itemView.findViewById(R.id.UI_KeyboardItem_ImageButton_Back);
			}
		}
	}
}
