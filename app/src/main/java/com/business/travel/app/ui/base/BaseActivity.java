package com.business.travel.app.ui.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
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
}

