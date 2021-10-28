package com.business.travel.app.activity.ui.dashboard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * @author chenshang
 */
public class DashboardViewModel extends ViewModel {

	private final MutableLiveData<String> mText;

	public DashboardViewModel() {
		mText = new MutableLiveData<>();
		mText.setValue("This is dashboard fragment");
	}

	public LiveData<String> getText() {
		return mText;
	}

	public void setmText(String msg) {
		mText.setValue(msg);
	}
}