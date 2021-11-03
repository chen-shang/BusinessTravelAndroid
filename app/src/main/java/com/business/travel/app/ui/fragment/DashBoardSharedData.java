package com.business.travel.app.ui.fragment;

import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.ui.base.ShareData;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author chenshang
 */
@Data
@NoArgsConstructor
@ToString(callSuper = true)
public class DashBoardSharedData extends ShareData {

	private Project project;
}
