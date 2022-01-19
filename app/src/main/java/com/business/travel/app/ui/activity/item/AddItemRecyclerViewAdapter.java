package com.business.travel.app.ui.activity.item;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.api.BusinessTravelResourceApi;
import com.business.travel.app.model.GiteeContent;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.model.converter.ImageIconInfoConverter;
import com.business.travel.app.ui.activity.item.AddItemRecyclerViewAdapter.AddItemRecyclerViewAdapterViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.FutureUtil;
import com.business.travel.app.utils.LogToast;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class AddItemRecyclerViewAdapter extends BaseRecyclerViewAdapter<AddItemRecyclerViewAdapterViewAdapterViewHolder, GiteeContent> {
    private static final LoadingCache<String, List<GiteeContent>> cache = CacheBuilder.newBuilder()
            //缓存100个
            .maximumSize(100)
            //最多5分钟
            .expireAfterWrite(5, TimeUnit.MINUTES)
            //超时更新
            .build(new CacheLoader<String, List<GiteeContent>>() {
                @Override
                public List<GiteeContent> load(@NotNull String path) {
                    //通过网络加载
                    return BusinessTravelResourceApi.getRepoContents(path).stream()
                            //只取文件夹
                            .filter(item -> "file".equals(item.getType()))
                            //支取svg图标
                            .filter(item -> item.getName().endsWith("svg"))
                            //集合
                            .collect(Collectors.toList());
                }
            });

    public AddItemRecyclerViewAdapter(List<GiteeContent> giteeContents, Context context) {
        super(giteeContents, context);
    }

    /**
     * 从缓存中获取路径下对应的图标信息
     *
     * @param path
     * @return
     */
    private List<GiteeContent> getFromCache(String path) {
        try {
            return cache.get(path);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    @NonNull
    @NotNull
    @Override
    public AddItemRecyclerViewAdapterViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_add_item_recycler_view_adapter, parent, false);
        return new AddItemRecyclerViewAdapterViewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull AddItemRecyclerViewAdapterViewAdapterViewHolder holder, int position) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        //先获取到类型,也就是文件夹的名字
        GiteeContent giteeContent = dataList.get(position);
        final String name = giteeContent.getName();
        holder.iconPathTextView.setText(name);

        //然后开始处理每一项下面的图标
        List<ImageIconInfo> imageIconInfoList = new ArrayList<>();
        String path = giteeContent.getPath();
        //接下来是对icon的处理
        LayoutManager layoutManager = new GridLayoutManager(context, 5);
        holder.imageIconInfoRecyclerView.setLayoutManager(layoutManager);
        AddItemRecyclerViewInnerAdapter billRecyclerViewAdapter = new AddItemRecyclerViewInnerAdapter(imageIconInfoList, context);
        holder.imageIconInfoRecyclerView.setAdapter(billRecyclerViewAdapter);

        try {
            FutureUtil.supplyAsync(() -> getFromCache(path)).thenApply(giteeContents -> giteeContents.stream().sorted(Comparator.comparingInt(GiteeContent::getItemSort)).map(ImageIconInfoConverter.INSTANCE::convertImageIconInfo).collect(Collectors.toList())).thenAccept(item -> {
                imageIconInfoList.clear();
                imageIconInfoList.addAll(item);
            }).get(5, TimeUnit.SECONDS);
            billRecyclerViewAdapter.notifyDataSetChanged();
        } catch (Exception e) {
            LogToast.errorShow("网络环境较差,请稍后重试");
        }
    }

    @SuppressLint("NonConstantResourceId")
    static class AddItemRecyclerViewAdapterViewAdapterViewHolder extends ViewHolder {

        @BindView(R.id.UI_AddItemRecyclerViewAdapter_TextView_IconPath)
        public TextView iconPathTextView;
        @BindView(R.id.UI_AddItemRecyclerViewAdapter_SwipeRecyclerView)
        public SwipeRecyclerView imageIconInfoRecyclerView;

        public AddItemRecyclerViewAdapterViewAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
