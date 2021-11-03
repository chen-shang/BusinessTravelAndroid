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
import lombok.Getter;

/**
 * @author chenshang
 */
public abstract class BaseActivity<VB extends ViewBinding, DATA extends ShareData> extends AppCompatActivity {
	/**
	 * viewBinding 用于绑定该Fragment对应的view的
	 * 免去写各种findViewById的方法
	 */
	protected VB viewBinding;
	/**
	 * dataBinding  用于绑定该Fragment共享的一些对象的
	 * 可以方便共享数据
	 */
	@Getter
	protected DATA dataBinding;

	@Override
	protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//利用反射先实例化 ViewBinding & dataBinding
		Type superclass = getClass().getGenericSuperclass();
		//第一个参数是 ViewBinding
		Class<?> clazzViewBinding = (Class<?>)((ParameterizedType)Objects.requireNonNull(superclass)).getActualTypeArguments()[0];
		//第二个参数是 DATA
		Class<?> clazzData = (Class<?>)((ParameterizedType)Objects.requireNonNull(superclass)).getActualTypeArguments()[1];
		try {
			viewBinding = (VB)clazzViewBinding.getDeclaredMethod("inflate", LayoutInflater.class).invoke(null, getLayoutInflater());
			//初始化dataBinding
			dataBinding = (DATA)clazzData.newInstance();
			setContentView(Objects.requireNonNull(viewBinding).getRoot());
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | InstantiationException e) {
			Log.e("BaseActivity", "view banding error", e);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		viewBinding = null;
		dataBinding = null;
	}
}

