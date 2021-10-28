package com.business.travel.app.ui.base;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author chenshang
 */
public abstract class BaseActivity<T extends ViewBinding> extends AppCompatActivity {
    protected T binding;

    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Type superclass = getClass().getGenericSuperclass();
        Class<?> clazz = (Class<?>) ((ParameterizedType) Objects.requireNonNull(superclass)).getActualTypeArguments()[0];
        try {
            binding = (T) clazz.getDeclaredMethod("inflate", LayoutInflater.class).invoke(null, getLayoutInflater());
            setContentView(Objects.requireNonNull(binding).getRoot());
        } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
            Log.e("BaseActivity", "view banding error", e);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}

