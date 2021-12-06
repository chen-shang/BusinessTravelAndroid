package com.business.travel.app.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import androidx.viewbinding.ViewBinding;
import com.business.travel.app.ui.activity.master.fragment.BillFragment;
import com.business.travel.app.ui.activity.master.fragment.MyFragment;
import com.business.travel.app.ui.activity.master.fragment.ProjectFragment;
import com.business.travel.app.ui.base.BaseFragment;

/**
 * @author chenshang
 * 提前初始化好主界面的三个fragment,提高加载速度
 */
@RequiresApi(api = VERSION_CODES.N)
public enum MasterFragmentPositionEnum {
	/**
	 * ProjectFragment
	 */
	PROJECT_FRAGMENT(0, new ProjectFragment()),
	BILL_FRAGMENT(1, new BillFragment()),
	MY_FRAGMENT(2, new MyFragment()),
	;

	private static final Map<Integer, MasterFragmentPositionEnum> MAP = new HashMap<>();

	static {
		Arrays.stream(values()).forEach(item -> MAP.put(item.getPosition(), item));
	}

	private final int position;
	private final BaseFragment<? extends ViewBinding> fragment;

	MasterFragmentPositionEnum(int code, BaseFragment<? extends ViewBinding> fragment) {
		this.position = code;
		this.fragment = fragment;
	}

	public static MasterFragmentPositionEnum of(Integer position) {
		return MAP.get(position);
	}

	public <T extends BaseFragment<? extends ViewBinding>> T getFragment() {
		return (T)fragment;
	}

	public int getPosition() {
		return position;
	}

}
