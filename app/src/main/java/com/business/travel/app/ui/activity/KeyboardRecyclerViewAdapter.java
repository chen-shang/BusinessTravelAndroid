package com.business.travel.app.ui.activity;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewbinding.ViewBinding;
import com.blankj.utilcode.util.ColorUtils;
import com.business.travel.app.R;
import com.business.travel.app.ui.activity.KeyboardRecyclerViewAdapter.KeyboardRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class KeyboardRecyclerViewAdapter extends BaseRecyclerViewAdapter<KeyboardRecyclerViewAdapterViewHolder, Integer> {

	public OnClickListener onSaveClick;
	public OnClickListener onDeleteClick;
	public OnClickListener onReRecordClick;

	public KeyboardRecyclerViewAdapter(List<Integer> integers, BaseActivity<? extends ViewBinding> baseActivity) {
		super(integers, baseActivity);
	}

	@NonNull
	@NotNull
	@Override
	public KeyboardRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
		View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_keyboard_item, parent, false);
		return new KeyboardRecyclerViewAdapterViewHolder(view);
	}

	@Override
	public void onBindViewHolder(@NonNull @NotNull KeyboardRecyclerViewAdapterViewHolder holder, int position) {
		Button button = holder.itemView.findViewById(R.id.num);
		TextView textView = activity.findViewById(R.id.textView5);
		TextView textView4 = activity.findViewById(R.id.textView4);

		switch (position) {
			case 3:
				button.setText("删除");
				button.setOnClickListener(v -> {
					String trim = textView.getText().toString().trim();
					String substring = trim.substring(0, trim.length() - 1);
					textView.setText(substring);
				});
				break;
			case 7:
				button.setText("-");
				break;
			case 11:
				button.setText("+");
				break;
			case 15:
				holder.itemView.setBackgroundColor(ColorUtils.getColor(R.color.teal_800));
				button.setText("保存");
				button.setOnClickListener(onSaveClick);
				break;
			case 0:
				button.setText("1");
				button.setOnClickListener(v -> {
					textView.append("1");
				});
				break;
			case 1:
				button.setText("2");
				button.setOnClickListener(v -> {
					textView.append("2");
				});
				break;
			case 2:
				button.setText("3");
				button.setOnClickListener(v -> {
					textView.append("3");
				});
				break;
			case 8:
				button.setText("7");
				button.setOnClickListener(v -> {
					textView.append("7");
				});
				break;
			case 9:
				button.setText("8");
				button.setOnClickListener(v -> {
					textView.append("8");
				});
				break;
			case 10:
				button.setText("9");
				button.setOnClickListener(v -> {
					textView.append("9");
				});
				break;
			case 13:
				button.setText("0");
				button.setOnClickListener(v -> {
					textView.append("0");
				});
				break;
			case 12:
				button.setText(".");
				button.setOnClickListener(v -> {
					if (textView.getText().toString().contains(".")) {
						return;
					}
					textView.append(".");
				});
				break;
			case 14:
				button.setText("再记");
				button.setOnClickListener(onReRecordClick);
				break;
			default:
				button.setText(String.valueOf(position));
				button.setOnClickListener(v -> {
					textView.append(String.valueOf(position));
				});
		}
	}

	@Override
	public int getItemCount() {
		return 16;
	}

	class KeyboardRecyclerViewAdapterViewHolder extends ViewHolder {
		public KeyboardRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
			super(itemView);
		}
	}
}
