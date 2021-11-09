package com.business.travel.app.ui.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

/**
 * @author chenshang
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {
    /**
     * viewBinding 用于绑定该Activity对应的view的
     * 免去写各种findViewById的方法
     */
    protected VB viewBinding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Objects.requireNonNull(getSupportActionBar()).hide();
        //利用反射先实例化 ViewBinding & dataBinding
        Type superclass = getClass().getGenericSuperclass();
        //第一个参数是 ViewBinding
        Class<?> clazzViewBinding = (Class<?>)((ParameterizedType)Objects.requireNonNull(superclass)).getActualTypeArguments()[0];
        try {
            //初始化viewBinding
            viewBinding = (VB)clazzViewBinding.getDeclaredMethod("inflate", LayoutInflater.class).invoke(null, getLayoutInflater());
            //初始化加载页面
            setContentView(Objects.requireNonNull(viewBinding).getRoot());
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            Log.e("BaseActivity", "banding error", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //activity销毁的时候清空,有助于快速回收垃圾
        viewBinding = null;
    }

    /**
     * 弹出的键盘在失去焦点的时候收回
     *
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() != MotionEvent.ACTION_DOWN) {
            return getWindow().superDispatchTouchEvent(ev) || onTouchEvent(ev);
        }

        View v = getCurrentFocus();
        if (!isShouldHideInput(v, ev)) {
            return super.dispatchTouchEvent(ev);
        }

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
        // 必不可少，否则所有的组件都不会有TouchEvent了
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (!(v instanceof EditText)) {
            return false;
        }

        int[] leftTop = {0, 0};
        //获取输入框当前的location位置
        v.getLocationInWindow(leftTop);
        int left = leftTop[0];
        int top = leftTop[1];
        int bottom = top + v.getHeight();
        int right = left + v.getWidth();
        if (event.getX() > left && event.getX() < right && event.getY() > top && event.getY() < bottom) {
            // 点击的是输入框区域，保留点击EditText的事件
            return false;
        }
        v.setFocusable(false);
        v.setFocusableInTouchMode(true);
        return true;
    }
}

