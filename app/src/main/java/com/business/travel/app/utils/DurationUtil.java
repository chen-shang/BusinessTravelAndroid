package com.business.travel.app.utils;

import java.time.Duration;
import java.util.List;

import com.business.travel.app.dal.entity.Project;
import com.business.travel.utils.DateTimeUtil;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

public class DurationUtil {
	public static Long sumDurationDay(List<Pair<Long, Long>> timePeriods) {
		return timePeriods.stream().map(DurationUtil::durationDay).reduce(Long::sum).orElse(0L);
	}

	public static Long durationDay(Pair<Long, Long> timePeriod) {
		return Duration.between(DateTimeUtil.toLocalDateTime(timePeriod.getLeft()), DateTimeUtil.toLocalDateTime(timePeriod.getRight())).toDays() + 1;
	}

	@NotNull
	public static Pair<Long, Long> convertTimePeriod(Project project) {
		Long startTime = project.getStartTime();
		Long endTime = project.getEndTime();
		if (startTime == null || startTime <= 0) {
			startTime = DateTimeUtil.timestamp();
		}

		if (endTime == null || endTime <= 0) {
			endTime = DateTimeUtil.timestamp();
		}
		return Pair.of(startTime, endTime);
	}
}
