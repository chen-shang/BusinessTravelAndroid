package com.business.travel.app.service;

import android.content.Context;
import androidx.annotation.Nullable;
import androidx.room.Transaction;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.business.travel.app.dal.dao.BillDao;
import com.business.travel.app.dal.dao.ProjectDao;
import com.business.travel.app.dal.db.AppDatabase;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.utils.DurationUtil;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import com.business.travel.vo.enums.DeleteEnum;
import com.google.common.base.Preconditions;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 项目相关的业务逻辑
 */
public class ProjectService {
    private final ProjectDao projectDao;
    private final BillDao billDao;

    public ProjectService(Context context) {
        projectDao = AppDatabase.getInstance(context).projectDao();
        billDao = AppDatabase.getInstance(context).billDao();
    }

    /**
     * 新建项目
     *
     * @param project
     * @return
     */
    public Project createProject(Project project) {
        LogUtils.i("新建项目 project:" + JacksonUtil.toPrettyString(project));
        Preconditions.checkArgument(project != null, "param can not be null");
        String name = project.getName();
        Preconditions.checkArgument(StringUtils.isNotBlank(name), "project name can not be null");
        Project record = projectDao.selectByName(name);
        Preconditions.checkArgument(record == null, "项目已存在");

        project.setCreateTime(DateTimeUtil.timestamp());
        project.setModifyTime(DateTimeUtil.timestamp());
        projectDao.insert(project);
        return projectDao.selectByName(name);
    }

    /**
     * 删除项目
     *
     * @param projectId
     */
    @Transaction
    public void softDeleteProjectWithBill(Long projectId) {
        LogUtils.i("删除项目 projectId:" + projectId);
        Project record = projectDao.selectByPrimaryKey(projectId);
        Preconditions.checkArgument(record != null, "项目不存在");

        //逻辑删除 删除的项目更新一下名字
        record.setName(record.getName() + "_" + projectId);
        record.setIsDeleted(DeleteEnum.DELETE.getCode());
        record.setModifyTime(DateTimeUtil.timestamp());
        projectDao.update(record);
        LogUtils.i("删除项目成功,开始删除项目下的账单 projectId:" + projectId);
        //更新账单
        billDao.softDeleteByProjectId(projectId);
    }

    /**
     * 更新项目
     *
     * @param projectId
     * @param project
     */
    public void updateProject(Long projectId, Project project) {
        LogUtils.i("更新项目 projectId:" + projectId + " -> project:" + JacksonUtil.toPrettyString(project));
        Preconditions.checkArgument(projectId != null, "请选择对应的项目");
        Project record = projectDao.selectByPrimaryKey(projectId);
        Preconditions.checkArgument(record != null, "项目不存在");
        Preconditions.checkArgument(DeleteEnum.DELETE.getCode() != record.getIsDeleted(), "差旅项目已经被删除,请不要重复操作");

        if (StringUtils.isNotBlank(project.getName())) {
            record.setName(project.getName());
        }
        if (project.getStartTime() != null) {
            record.setStartTime(project.getStartTime());
        }
        if (project.getEndTime() != null) {
            record.setEndTime(project.getEndTime());
        }
        if (project.getRemark() != null) {
            record.setRemark(project.getRemark());
        }
        if (project.getStatus() != null) {
            record.setStatus(project.getStatus());
        }
        if (project.getSortId() != null) {
            record.setSortId(project.getSortId());
        }
        Long modifyTime = project.getModifyTime();
        if (modifyTime != null) {
            record.setModifyTime(modifyTime);
        }
        projectDao.update(record);
    }

    /**
     * 根据项目名称创建项目如果项目不存在的话
     *
     * @param projectName
     * @return
     */
    @Nullable
    public Project createIfNotExist(String projectName) {
        Preconditions.checkArgument(StringUtils.isNotBlank(projectName), "请输入项目名称");
        //按照项目名称查询项目信息
        Project project = projectDao.selectByName(projectName);
        //如果已经存在的项目,则直接返回
        if (project != null) {
            //注意这里更新了一下最后的访问时间
            project.setModifyTime((DateTimeUtil.timestamp()));
            projectDao.update(project);
            return project;
        }

        //如果不存在的项目则直接新建
        project = new Project();
        project.setName(projectName);
        project.setStartTime(DateTimeUtil.timestamp());
        project.setEndTime(null);
        project.setRemark("");
        project.setCreateTime(DateTimeUtil.timestamp());
        project.setModifyTime(DateTimeUtil.timestamp());
        project.setIsDeleted(DeleteEnum.NOT_DELETE.getCode());
        return createProject(project);
    }

    //各种查询

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    public Project queryById(Long id) {
        return projectDao.selectByPrimaryKey(id);
    }

    /**
     * 根据主键id查询
     *
     * @param name
     * @return
     */
    public Project queryByName(String name) {
        return projectDao.selectByName(name);
    }

    /**
     * 查询最后更新过的项目
     *
     * @return
     */
    public Project queryLatestModify() {
        return projectDao.selectLatestModify();
    }

    /**
     * 批量查询全部项目
     *
     * @return
     */
    public List<Project> queryAll() {
        return projectDao.selectAll();
    }

    /**
     * 批量查询全部项目名称
     *
     * @return
     */
    public String[] queryAllProjectName() {
        List<Project> projects = queryAll();
        if (CollectionUtils.isEmpty(projects)) {
            return new String[]{};
        }
        return projects.stream().map(Project::getName).collect(Collectors.toList()).toArray(new String[]{});
    }

    /**
     * 统计所有项目的总收入
     */
    public Long sumTotalIncomeMoney() {
        Long totalIncomeMoney = billDao.sumTotalIncomeMoney();
        LogUtils.i("统计所有项目的总收入:" + totalIncomeMoney);
        return totalIncomeMoney;
    }

    /**
     * 统计所有项目的总支出
     */
    public Long sumTotalSpendingMoney() {
        Long totalSpendingMoney = billDao.sumTotalSpendingMoney();
        LogUtils.i("统计所有项目的总支出:" + totalSpendingMoney);
        return totalSpendingMoney;
    }

    /**
     * 统计指定年份差旅项目数
     */
    public Long countTotalProjectByYear(Integer year) {
        if (year == null) {
            return projectDao.count();
        }
        long start = DateTimeUtil.timestamp(LocalDate.of(year, 1, 1));
        long end = DateTimeUtil.timestamp(LocalDate.of(year, 1, 1).plusYears(1));
        return projectDao.countByTime(start, end);
    }

    /**
     * 统计每年差旅天数
     *
     * @return
     */
    public Long countTotalTravelDayByYear(Integer year) {
        List<Project> projects;
        if (year == null) {
            projects = projectDao.selectAll();
        } else {
            long start = DateTimeUtil.timestamp(LocalDate.of(year, 1, 1));
            long end = DateTimeUtil.timestamp(LocalDate.of(year, 1, 1).plusYears(1));
            projects = projectDao.selectByTime(start, end);
        }

        if (CollectionUtils.isEmpty(projects)) {
            return 0L;
        }

        if (projects.size() == 1) {
            Project project = projects.get(0);
            Pair<Long, Long> firstProjectTimePeriod = DurationUtil.convertTimePeriod(project);
            return DurationUtil.durationDay(firstProjectTimePeriod);
        }

        //项目不重复的时间段
        List<Pair<Long, Long>> timePeriods = genTimePeriods(projects);
        return DurationUtil.sumDurationDay(timePeriods);
    }

    @NotNull
    private List<Pair<Long, Long>> genTimePeriods(List<Project> projects) {
        List<Pair<Long, Long>> timePeriods = new ArrayList<>();
        projects.stream()
                //过滤掉开始时间为空的
                .filter(item -> Objects.nonNull(item.getStartTime()))
                //过滤掉结束时间为空的
                //.filter(item -> Objects.nonNull(item.getEndTime()))
                //先按照开始时间排序,开始时间非空
                .sorted(Comparator.comparing(Project::getStartTime))
                //转换成时间
                .map(DurationUtil::convertTimePeriod)
                //开始处理重复时间段时间
                .reduce((first, second) -> {
                    Long firstStartTime = first.getLeft();
                    Long firstEndTime = first.getRight();

                    Long secondStartTime = second.getLeft();
                    Long secondEndTime = second.getRight();
                    //第二段的开始时间比第一段的结束时间大
                    if (secondStartTime > firstEndTime) {
                        //先把第一段加入结果集合
                        timePeriods.add(first);
                        //返回第二段时间作为下一个时间段,因为已经断开了
                        return second;
                    }

                    if (secondEndTime <= firstEndTime) {
                        //返回第一段,因为第一段时间包含第二段时间
                        return first;
                    }

                    return Pair.of(firstStartTime, secondEndTime);
                }).ifPresent(timePeriods::add);
        return timePeriods;
    }
}
