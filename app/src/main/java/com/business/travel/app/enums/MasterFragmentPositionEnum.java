package com.business.travel.app.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import com.business.travel.app.ui.fragment.DashboardFragment;
import com.business.travel.app.ui.fragment.MyFragment;
import com.business.travel.app.ui.fragment.ProjectFragment;

/**
 * @author chenshang
 */
@RequiresApi(api = VERSION_CODES.N)
public enum MasterFragmentPositionEnum {
	/**
	 * ProjectFragment
	 */
	PROJECT_FRAGMENT(0, new ProjectFragment()),
	DASHBOARD_FRAGMENT(1, new DashboardFragment()),
	MY_FRAGMENT(2, new MyFragment()),
	;

	private static final Map<Integer, MasterFragmentPositionEnum> MAP = new HashMap<>();

	static {
		Arrays.stream(values()).forEach(item -> MAP.put(item.getPosition(), item));
	}

	private final int position;
	private final Fragment fragment;

	MasterFragmentPositionEnum(int code, Fragment fragment) {
		this.position = code;
		this.fragment = fragment;
	}

	public static MasterFragmentPositionEnum of(Integer position) {
		return MAP.get(position);
	}

	public Fragment getFragment() {
		return fragment;
	}

	public int getPosition() {
		return position;
	}

}
