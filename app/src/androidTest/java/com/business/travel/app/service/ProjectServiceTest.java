package com.business.travel.app.service;

import android.content.Context;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class ProjectServiceTest extends TestCase {

	@Test
	public void testCountDay() {
		// Context of the app under test.
		Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
		ProjectService projectService = new ProjectService(appContext);
		System.out.println("====countDay=====" + projectService.countTotalTravelDayByYear(null));
		System.out.println("==countProjectByYear====" + projectService.countTotalProjectByYear(null));
	}
}