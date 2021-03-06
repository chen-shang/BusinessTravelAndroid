package com.business.travel.app.dal.dao;

import androidx.room.Dao;
import androidx.room.Query;
import com.business.travel.app.dal.dao.base.BaseDao;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.model.ConsumeDatePeriod;

import java.util.List;

/**
 * @author chenshang
 */
@Dao
public interface BillDao extends BaseDao<Bill> {

    /**
     * 查询全部
     */
    @Query("SELECT * FROM bill where isDeleted=1")
    List<Bill> selectAll();

    /**
     * 统计项目的总支出
     *
     * @param projectId
     * @return
     * @see com.business.travel.vo.enums.ConsumptionTypeEnum
     */
    @Query("SELECT sum(amount) FROM bill where projectId=:projectId and consumptionType='SPENDING' and isDeleted=1")
    Long sumTotalSpendingMoney(Long projectId);

    /**
     * 统计总收入
     *
     * @see com.business.travel.vo.enums.ConsumptionTypeEnum
     */
    @Query("SELECT sum(amount) FROM bill where consumptionType='INCOME' and isDeleted=1")
    Long sumTotalIncomeMoney();

    /**
     * 统计项目的总收入
     *
     * @param projectId
     * @return
     * @see com.business.travel.vo.enums.ConsumptionTypeEnum
     */
    @Query("SELECT sum(amount) FROM bill where projectId=:projectId and consumptionType='INCOME' and isDeleted=1")
    Long sumTotalIncomeMoney(Long projectId);

    /**
     * 统计总支出
     *
     * @return
     * @see com.business.travel.vo.enums.ConsumptionTypeEnum
     */
    @Query("SELECT sum(amount) FROM bill where  consumptionType='SPENDING' and isDeleted=1")
    Long sumTotalSpendingMoney();

    /**
     * 统计项目某天的总支出
     *
     * @param projectId
     * @return
     * @see com.business.travel.vo.enums.ConsumptionTypeEnum
     */
    @Query("SELECT sum(amount) FROM bill where projectId=:projectId and consumptionType='SPENDING' and isDeleted=1 and consumeDate=:consumeDate")
    Long sumTotalSpendingMoney(Long projectId, Long consumeDate);

    /**
     * 统计项目某天的总收入
     *
     * @param projectId
     * @return
     * @see com.business.travel.vo.enums.ConsumptionTypeEnum
     */
    @Query("SELECT sum(amount) FROM bill where projectId=:projectId and consumptionType='INCOME' and isDeleted=1 and consumeDate=:consumeDate")
    Long sumTotalIncomeMoney(Long projectId, Long consumeDate);

    /**
     * 查询一个项目下所有的账单
     *
     * @param projectId
     * @return
     */
    @Query("SELECT * FROM bill where projectId=:projectId and isDeleted=1")
    List<Bill> selectByProjectId(Long projectId);

    /**
     * 查询一个项目下所有的消费日期
     *
     * @param projectId
     * @return
     */
    @Query("SELECT  * FROM bill where projectId=:projectId and isDeleted=1 and consumeDate=:consumeDate")
    List<Bill> selectByProjectIdAndConsumeDate(Long projectId, Long consumeDate);

    /**
     * 查询一个项目下所有的消费日期
     *
     * @param projectId
     * @return
     */
    @Query("SELECT distinct consumeDate FROM bill where projectId=:projectId and isDeleted=1 order by consumeDate desc")
    List<Long> selectConsumeDateByProjectId(Long projectId);

    /**
     * 删除一个项目下所有的账单
     *
     * @param id
     */
    @Query("update bill set isDeleted=0 where projectId=:id and isDeleted!=0")
    void softDeleteByProjectId(Long id);

    /**
     * 根据主键id删除账单
     *
     * @param id
     */
    @Query("update bill set isDeleted=0 where id=:id and isDeleted!=0")
    void softDeleteByPrimaryKey(Long id);

    /**
     * 根据主键id查询账单
     *
     * @param id
     * @return
     */
    @Query("SELECT * FROM bill where id=:id limit 1")
    Bill selectByPrimaryKey(Long id);

    /**
     * 统计账单总数
     *
     * @return
     */
    @Query("SELECT count(*) FROM bill where isDeleted=1")
    Long count();

    @Query("SELECT max(consumeDate) as max,min(consumeDate)  as min FROM bill where projectId=:projectId and isDeleted=1")
    ConsumeDatePeriod selectMaxAndMinConsumeDate(Long projectId);
}
