package com.business.travel.app.activity.ui.my;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author chenshang
 */
public class MyViewModel extends ViewModel {

	private final MutableLiveData<String> mText;

	public MyViewModel() {
		mText = new MutableLiveData<>();
		mText.setValue("This is notifications fragment");
	}

	public LiveData<String> getText() {
		return mText;
	}
}