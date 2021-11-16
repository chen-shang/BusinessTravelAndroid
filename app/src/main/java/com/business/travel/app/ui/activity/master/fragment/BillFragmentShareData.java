package com.business.travel.app.ui.activity.master.fragment;

import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.ui.base.ShareData;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author chenshang
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class BillFragmentShareData extends ShareData {
	private Project project;
}
