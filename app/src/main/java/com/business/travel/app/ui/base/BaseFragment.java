package com.business.travel.app.ui.base;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

/**
 * @author chenshang
 */
public abstract class BaseFragment<VB extends ViewBinding, DATA extends ShareData> extends Fragment {
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

	@Nullable
	@org.jetbrains.annotations.Nullable
	@Override
	public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
		//利用反射先实例化 ViewBinding & dataBinding
		Type superclass = getClass().getGenericSuperclass();
		//第一个参数是 ViewBinding
		Class<?> clazz = (Class<?>)((ParameterizedType)Objects.requireNonNull(superclass)).getActualTypeArguments()[0];
		//第二个参数是 DATA
		Class<?> clazz2 = (Class<?>)((ParameterizedType)Objects.requireNonNull(superclass)).getActualTypeArguments()[1];

		try {
			//初始化viewBinding
			viewBinding = (VB)clazz.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class).invoke(null, getLayoutInflater(), container, false);
			//初始化dataBinding
			dataBinding = (DATA)clazz2.newInstance();
		} catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException | java.lang.InstantiationException e) {
			Log.e("BaseFragment", "banding error", e);
		}
		return viewBinding.getRoot();
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		//activity销毁的时候清空,有助于快速回收垃圾
		viewBinding = null;
		dataBinding = null;
	}
}
