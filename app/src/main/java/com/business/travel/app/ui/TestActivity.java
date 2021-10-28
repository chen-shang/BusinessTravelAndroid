package com.business.travel.app.ui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.blankj.utilcode.util.ToastUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.dao.UserDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.User;
import com.business.travel.app.databinding.ActivityTestBinding;
import com.business.travel.app.ui.base.BaseActivity;
import com.business.travel.utils.JacksonUtil;
import org.apache.commons.lang3.RandomUtils;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public class TestActivity extends BaseActivity<ActivityTestBinding> {
    private UserDao userDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userDao = AppDatabase.getInstance(this).userDao();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppDatabase.getInstance(this).close();
    }

    public void insert(View view) {
        User user = new User();
        user.setFirstName("chen" + RandomUtils.nextInt());
        user.setLastName("shang" + RandomUtils.nextInt());
        userDao.insertUsers(user);
        ToastUtils.showShort("插入成功:" + JacksonUtil.toString(user));
    }

    public void select(View view) {
        User[] users = userDao.loadAllUsers();
        List<User> userList = new ArrayList<>(Arrays.asList(users));
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.setAdapter(new Adapter() {
            @NonNull
            @NotNull
            @Override
            public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_rv_item_simple, parent, false);
                return new ViewHolder(view) {
                };
            }

            @Override
            public void onBindViewHolder(@NonNull @NotNull ViewHolder holder, int position) {
                TextView userInfo = holder.itemView.findViewById(R.id.userInfo);
                User user = userList.get(userList.size() - position - 1);
                userInfo.setText(JacksonUtil.toString(user));
            }

            @Override
            public int getItemCount() {
                return userList.size();
            }
        });
    }

    public void delete(View view) {
    }
}