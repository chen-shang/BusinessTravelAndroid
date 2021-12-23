package com.business.travel.app.view;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.appcompat.widget.SearchView.SearchAutoComplete;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.business.travel.app.R;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;
import org.springframework.util.CollectionUtils;

/**
 * 自定义搜索
 */
public class SearchBar extends ConstraintLayout {

	//基础组件
	//选中的结果展示
	public final TextView selectedResultTextView;
	//搜索框
	public final SearchView searchView;
	//结果展示动画
	public final SwipeRecyclerView searchResultListRecyclerView;

	/**
	 * 列表数据
	 */
	private final List<String> dataList = new ArrayList<>();
	private final SearchBarViewAdapter swipeRecyclerViewAdapter;
	/**
	 * 搜索函数
	 */
	private Function<String, List<String>> querySearch;

	public SearchBar(@NonNull @NotNull Context context, @Nullable @org.jetbrains.annotations.Nullable AttributeSet attrs) {
		super(context, attrs);
		//加载布局
		View inflate = LayoutInflater.from(context).inflate(R.layout.search_bar, this);
		//初始化组件
		selectedResultTextView = inflate.findViewById(R.id.TextView_SelectedResult);
		searchView = inflate.findViewById(R.id.searchView);
		searchResultListRecyclerView = inflate.findViewById(R.id.RecyclerView_SearchResultList);

		//列表线性布局
		searchResultListRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), RecyclerView.VERTICAL, false));
		swipeRecyclerViewAdapter = new SearchBarViewAdapter(dataList);
		searchResultListRecyclerView.setAdapter(swipeRecyclerViewAdapter);

		//搜索框的文本显示
		SearchAutoComplete mSearchSrcTextView = findViewById(R.id.search_src_text);
		mSearchSrcTextView.setTextColor(Color.WHITE);
		mSearchSrcTextView.setTextSize(20);

		selectedResultTextView.setOnClickListener(v -> {
			searchView.setVisibility(VISIBLE);
			searchResultListRecyclerView.setVisibility(VISIBLE);
			searchView.onActionViewExpanded();
			searchView.setQuery(selectedResultTextView.getText(), false);
		});

		//搜索框获取焦点事件
		searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
			if (hasFocus) {
				searchView.onActionViewExpanded();
				List<String> result = querySearch.apply(searchView.getQuery().toString());
				dataList.clear();
				dataList.addAll(result);
				swipeRecyclerViewAdapter.notifyDataSetChanged();
			} else {
				selectedResultTextView.setText(searchView.getQuery());
				searchView.setVisibility(INVISIBLE);
				searchResultListRecyclerView.setVisibility(INVISIBLE);
			}
		});

		searchView.setOnQueryTextListener(new OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String query) {
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				List<String> result = querySearch.apply(newText);
				dataList.clear();
				dataList.addAll(result);
				swipeRecyclerViewAdapter.notifyDataSetChanged();
				return true;
			}
		});
	}

	public SearchBar withQuerySearch(Function<String, List<String>> searchData) {
		this.querySearch = searchData;
		return this;
	}

	class SearchBarViewAdapter extends RecyclerView.Adapter<ViewHolder> {
		private final List<String> dataList;

		public SearchBarViewAdapter(List<String> projectNames) {
			this.dataList = projectNames;
		}

		@NonNull
		@NotNull
		@Override
		public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_bar_item, parent, false);
			return new ViewHolder(view) {
			};
		}

		@Override
		public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
			if (CollectionUtils.isEmpty(dataList)) {
				return;
			}
			String data = dataList.get(position);
			TextView textView = holder.itemView.findViewById(R.id.resultText);
			textView.setText(data);

			CardView cardView = holder.itemView.findViewById(R.id.cardView);
			cardView.setOnClickListener(v -> {
				selectedResultTextView.setText(data);
				searchView.setVisibility(INVISIBLE);
				searchResultListRecyclerView.setVisibility(INVISIBLE);

			});
		}

		@Override
		public int getItemCount() {
			return dataList.size();
		}
	}
}


