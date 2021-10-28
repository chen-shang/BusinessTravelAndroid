package com.business.travel.app.ui.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author chenshang
 */
public abstract class BaseFragment<T extends ViewBinding> extends Fragment {
    protected T binding;

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        Type superclass = getClass().getGenericSuperclass();
        Class<?> clazz = (Class<?>) ((ParameterizedType) Objects.requireNonNull(superclass)).getActualTypeArguments()[0];
        try {
            binding = (T) clazz.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class).invoke(null, getLayoutInflater(), container, false);
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            Log.e("BaseFragment", "view banding error", e);
        }
        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
