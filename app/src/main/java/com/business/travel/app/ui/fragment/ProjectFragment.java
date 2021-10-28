package com.business.travel.app.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import androidx.viewpager2.widget.ViewPager2;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.databinding.FragmentProjectBinding;
import com.business.travel.app.ui.base.BaseFragment;
import org.apache.commons.lang3.RandomStringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * @author chenshang
 */
public class ProjectFragment extends BaseFragment<FragmentProjectBinding> {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProjectBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerView;
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add("第" + i + "天技术考察");
        }
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(new Adapter() {
            @NonNull
            @NotNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_item, parent, false);
                return new ViewHolder(view) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
                TextView nameText = holder.itemView.findViewById(R.id.name);
                nameText.setText(list.get(position));
                TextView payText = holder.itemView.findViewById(R.id.pay);
                payText.setText("支出:" + RandomStringUtils.randomNumeric(3));

                CardView cardView = holder.itemView.findViewById(R.id.card_view);
                ViewPager2 viewpager = getActivity().findViewById(R.id.viewPager);
                cardView.setOnClickListener(v -> {
                    ToastUtils.showShort(nameText.getText());
                    viewpager.setCurrentItem(1);
                });
            }

            @Override
            public int getItemCount() {
                return list.size();
            }
        });
        return root;
    }
}