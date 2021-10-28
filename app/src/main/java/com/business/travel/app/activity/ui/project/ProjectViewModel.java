package com.business.travel.app.activity.ui.project;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author chenshang
 */
public class ProjectViewModel extends ViewModel {

	private final MutableLiveData<String> mText;

	public ProjectViewModel() {
		mText = new MutableLiveData<>();
		mText.setValue("This is home fragment");
	}

	public LiveData<String> getText() {
		return mText;
	}
}