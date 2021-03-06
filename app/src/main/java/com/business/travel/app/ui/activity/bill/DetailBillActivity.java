package com.business.travel.app.ui.activity.bill;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.mtjsoft.www.gridviewpager_recycleview.GridViewPager;
import com.blankj.utilcode.util.CollectionUtils;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ScreenUtils;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.dal.entity.Project;
import com.business.travel.app.databinding.ActivityDetailBillBinding;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.service.MemberService;
import com.business.travel.app.service.ProjectService;
import com.business.travel.app.ui.base.ColorStatusBarActivity;
import com.business.travel.app.utils.GridViewPagerUtil;
import com.business.travel.app.utils.LogToast;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.app.utils.Try;
import com.business.travel.app.view.BottomIconListPopupView;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.SplitUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.business.travel.vo.enums.WeekEnum;
import com.google.common.base.Preconditions;
import com.lxj.xpopup.XPopup.Builder;
import com.lxj.xpopup.enums.PopupAnimation;
import com.lxj.xpopup.impl.BottomListPopupView;
import com.lxj.xpopup.impl.ConfirmPopupView;
import org.apache.commons.lang3.StringUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.business.travel.app.ui.activity.bill.DetailBillActivity.IntentKey.SELECT_BILL_ID;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toMap;

/**
 * ??????????????????
 */
public class DetailBillActivity extends ColorStatusBarActivity<ActivityDetailBillBinding> {

    /**
     * ????????????6???
     */
    private static final int COLUMN_COUNT = 6;
    /**
     * ????????????
     */
    private final List<ImageIconInfo> memberIconList = new ArrayList<>();
    /**
     * ?????????????????????
     */
    private final List<ImageIconInfo> consumptionIconList = new ArrayList<>();
    private final Builder builder = new Builder(this).popupHeight(ScreenUtils.getScreenHeight() / 2).popupAnimation(PopupAnimation.ScrollAlphaFromTop);
    /**
     * ??????????????????????????????
     */
    private Long selectBillId;
    private ConsumptionTypeEnum consumptionTypeEnum;
    //??????service
    private BillService billService;
    private ProjectService projectService;
    private MemberService memberService;
    private ConsumptionService consumptionService;
    /**
     * ????????????
     */
    private DatePickerDialog datePickerDialog;

    @Override
    protected void inject() {
        billService = new BillService(this);
        memberService = new MemberService(this);
        consumptionService = new ConsumptionService(this);
        projectService = new ProjectService(this);
        selectBillId = getIntent().getLongExtra(SELECT_BILL_ID, -1);

        LocalDateTime now = DateTimeUtil.now();
        datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {}, now.getYear(), now.getMonth().getValue() - 1, now.getDayOfMonth());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //?????????????????????
        registerConsumptionPageView(viewBinding.GridViewPagerConsumptionIconList, consumptionIconList);
        //??????????????????
        registerMemberPageView(viewBinding.GridViewPagerMemberIconList, memberIconList);

        //??????????????????????????????
        registerUpdateConsumeDate(viewBinding.time);

        //????????????????????????
        registerUpdateRemark(viewBinding.remark);

        //??????????????????????????????
        registerUpdateAmount(viewBinding.amount);

        //??????????????????????????????
        registerUpdateProjectName();

        //????????????????????????
        registerDeleteBill();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Try.of(() -> refreshData(selectBillId));
    }

    private void registerMemberPageView(GridViewPager gridViewPager, List<ImageIconInfo> dataList) {
        GridViewPagerUtil.registerPageViewCommonProperty(gridViewPager)
                // ?????????????????????
                .setDataAllCount(dataList.size())
                // ??????????????????
                .setRowCount(1)
                // ??????????????????
                .setColumnCount(COLUMN_COUNT)
                // ????????????
                .setImageTextLoaderInterface((imageView, textView, position) -> {
                    // ??????????????????????????????????????????????????????????????????
                    ImageIconInfo imageIconInfo = dataList.get(position);
                    imageIconInfo.setSelected(true);
                    imageIconInfo.refresh(imageView, textView);

                    imageView.setOnClickListener(v -> {
                        List<ImageIconInfo> result = genPopupMemberImageIcon();
                        //???????????????????????????
                        builder.asCustom(new BottomIconListPopupView(this, result).onConfirm(() -> {
                            String collect = result.stream().filter(ImageIconInfo::isSelected).map(ImageIconInfo::getId).map(String::valueOf).collect(joining(","));
                            Bill record = new Bill();
                            record.setMemberIds(collect);
                            updateBill(record);
                            //????????????
                            refreshData(selectBillId);
                        })).show();
                    });
                });
    }

    private void registerConsumptionPageView(GridViewPager gridViewPager, List<ImageIconInfo> dataList) {
        GridViewPagerUtil.registerPageViewCommonProperty(gridViewPager)
                // ?????????????????????
                .setDataAllCount(dataList.size())
                // ??????????????????
                .setRowCount(1)
                // ??????????????????
                .setColumnCount(COLUMN_COUNT)
                // ????????????
                .setImageTextLoaderInterface((imageView, textView, position) -> {
                    // ??????????????????????????????????????????????????????????????????
                    ImageIconInfo imageIconInfo = dataList.get(position);
                    imageIconInfo.setSelected(true);
                    imageIconInfo.refresh(imageView, textView);

                    imageView.setOnClickListener(v -> {
                        List<ImageIconInfo> result = genPopupConsumptionImageIcon();
                        //???????????????????????????
                        builder.asCustom(new BottomIconListPopupView(this, result).onConfirm(() -> {
                            String collect = result.stream().filter(ImageIconInfo::isSelected).map(ImageIconInfo::getId).map(String::valueOf).collect(joining(","));
                            Bill bill = new Bill();
                            bill.setConsumptionIds(collect);
                            updateBill(bill);
                            //????????????
                            refreshData(selectBillId);
                        })).show();
                    });
                });
    }

    private void updateBill(Bill bill) {
        billService.updateBill(selectBillId, bill);
        LogToast.infoShow("????????????");
    }

    /**
     * ??????????????????
     *
     * @param billId
     */
    private void refreshData(Long billId) {
        //????????????
        Preconditions.checkArgument(billId > 0, "???????????????");
        Bill bill = billService.queryBillById(billId);
        Preconditions.checkArgument(bill != null, "?????????????????? " + billId);
        //????????????
        showBillDetail(bill);
    }

    private void registerDeleteBill() {
        ConfirmPopupView confirmPopupView = new Builder(this).asConfirm("", "????????????????????????", () -> {
            //1. ???????????????
            billService.deleteBillById(selectBillId);
            //2. ??????????????????
            this.finish();
        });
        viewBinding.topTitleBar.contentBarRightIcon.setOnClickListener(v -> confirmPopupView.show());
    }

    private void registerUpdateProjectName() {
        Builder builder = new Builder(this).maxHeight(ScreenUtils.getScreenHeight() / 2).popupAnimation(PopupAnimation.ScrollAlphaFromTop);
        String[] data = projectService.queryAllProjectName();
        BottomListPopupView bottomListPopupView = builder.asBottomList("????????????", data, null, -1, (position, text) -> {
            viewBinding.projectName.setText(text);

            Bill record = new Bill();
            Project project = projectService.queryByName(text);
            record.setProjectId(project.getId());
            updateBill(record);
        });

        viewBinding.projectName.setOnClickListener(v -> {
            int checkedPosition = getIndexOf(data, viewBinding.projectName.getText().toString());
            bottomListPopupView.setCheckedPosition(checkedPosition).show();
            bottomListPopupView.show();
        });
    }

    private int getIndexOf(String[] data, String targetName) {
        for (int i = 0; i < data.length; i++) {
            if (targetName.equals(data[i])) {
                return i;
            }
        }
        return -1;
    }

    private void registerUpdateAmount(EditText editeText) {
        editeText.setTextColor(viewBinding.projectName.getCurrentTextColor());
        editeText.setOnFocusChangeListener((v, hasFocus) -> Try.of(() -> {
            //???????????????????????????
            if (!hasFocus) {
                String s = ((EditText) v).getText().toString();
                Bill record = new Bill();
                record.setAmount(MoneyUtil.toFen(s));
                updateBill(record);
            }
        }));
    }

    private void registerUpdateRemark(EditText remark) {
        remark.setTextColor(viewBinding.projectName.getCurrentTextColor());
        remark.setTextSize(15);
        remark.setOnFocusChangeListener((v, hasFocus) -> Try.of(() -> {
            //???????????????????????????
            if (!hasFocus) {
                String s = ((EditText) v).getText().toString();
                Bill record = new Bill();
                record.setRemark(s);
                updateBill(record);
            }
        }));
    }

    private void registerUpdateConsumeDate(View view) {
        //????????????
        datePickerDialog.setOnDateSetListener((v, year, month, dayOfMonth) -> Try.of(() -> {
            LocalDate localDate = LocalDate.of(year, month + 1, dayOfMonth);
            //????????????
            String date = DateTimeUtil.format(localDate, "yyyy-MM-dd");
            Bill record = new Bill();
            long consumeDate = DateTimeUtil.timestamp(date, "yyyy-MM-dd");
            record.setConsumeDate(consumeDate);

            Bill bill = billService.queryBillById(selectBillId);
            Long projectId = bill.getProjectId();
            Project project = projectService.queryById(projectId);
            Long startTime = project.getStartTime();
            if (startTime != null && consumeDate < startTime) {
                throw new IllegalArgumentException("???????????????????????????????????????????????????");
            }
            Long endTime = project.getEndTime();
            if (endTime != null && consumeDate > endTime) {
                throw new IllegalArgumentException("???????????????????????????????????????????????????");
            }

            updateBill(record);
            //??????
            WeekEnum weekEnum = WeekEnum.ofCode(localDate.getDayOfWeek().getValue());
            viewBinding.time.setText(date + " " + weekEnum.getMsg());
        }));

        //????????????
        view.setOnClickListener(v -> refreshDatePickerDialogDate((TextView) v).show());
    }

    /**
     * ????????????
     *
     * @param v
     * @return
     */
    private DatePickerDialog refreshDatePickerDialogDate(TextView v) {
        String time = v.getText().toString();
        String date = SplitUtil.trimToStringList(time, " ").get(0);
        LocalDate localDate = DateTimeUtil.parseLocalDate(date);
        datePickerDialog.updateDate(localDate.getYear(), localDate.getMonth().ordinal(), localDate.getDayOfMonth());
        return datePickerDialog;
    }

    /**
     * ??????????????????
     *
     * @param bill
     */
    @SuppressLint("SetTextI18n")
    private void showBillDetail(@NotNull Bill bill) {
        //???????????????
        String consumptionIds = bill.getConsumptionIds();
        showConsumption(consumptionIds);

        //????????????
        String memberIds = bill.getMemberIds();
        showMember(memberIds);

        //????????????
        viewBinding.amount.setText(MoneyUtil.toYuanString(bill.getAmount()));

        //????????????
        String date = DateTimeUtil.format(bill.getConsumeDate(), "yyyy-MM-dd");
        //??????
        WeekEnum weekEnum = WeekEnum.ofCode(DateTimeUtil.toLocalDateTime(bill.getConsumeDate()).getDayOfWeek().getValue());
        viewBinding.time.setText(date + " " + weekEnum.getMsg());
        //????????????
        String consumptionType = bill.getConsumptionType();
        //??????
        consumptionTypeEnum = ConsumptionTypeEnum.valueOf(consumptionType);
        viewBinding.consumerType.setText(consumptionTypeEnum.getMsg());

        //??????
        viewBinding.remark.setText(bill.getRemark());

        //????????????
        Long projectId = bill.getProjectId();
        String projectName = projectService.queryById(projectId).getName();
        viewBinding.projectName.setText(projectName);
    }

    /**
     * ????????????
     *
     * @param consumptionIds
     */
    private void showConsumption(String consumptionIds) {
        if (StringUtils.isBlank(consumptionIds)) {
            LogUtils.w("???????????????????????? id:" + selectBillId);
            return;
        }
        List<Long> consumptionIdList = SplitUtil.trimToLongList(consumptionIds);
        if (CollectionUtils.isEmpty(consumptionIdList)) {
            return;
        }
        List<ImageIconInfo> consumptions = consumptionService.queryByIds(consumptionIdList);
        if (CollectionUtils.isEmpty(consumptions)) {
            LogUtils.w("???????????????????????? id:" + selectBillId + ",consumptionIdList:" + consumptionIdList);
            return;
        }

        //????????????????????????????????????
        consumptionIconList.clear();
        consumptionIconList.addAll(consumptions);
        //??????????????????
        viewBinding.GridViewPagerConsumptionIconList
                //???????????????
                .setDataAllCount(consumptionIconList.size())
                //????????????
                .show();
    }

    /**
     * ????????????
     *
     * @param memberIds
     */
    private void showMember(String memberIds) {
        if (StringUtils.isBlank(memberIds)) {
            LogUtils.w("??????????????????????????? id:" + selectBillId);
            return;
        }
        List<Long> memberIdList = SplitUtil.trimToLongList(memberIds);
        if (CollectionUtils.isEmpty(memberIdList)) {
            return;
        }
        List<ImageIconInfo> members = memberService.queryByIds(memberIdList);
        if (CollectionUtils.isEmpty(members)) {
            LogUtils.w("???????????????????????? id:" + selectBillId + ",memberIdList:" + memberIdList);
            return;
        }

        //????????????????????????????????????
        memberIconList.clear();
        memberIconList.addAll(members);
        viewBinding.GridViewPagerMemberIconList
                //???????????????
                .setDataAllCount(memberIconList.size())
                //????????????
                .show();
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    private List<ImageIconInfo> genPopupMemberImageIcon() {
        //????????????????????????
        List<ImageIconInfo> imageIconInfos = memberService.queryAllMembersIconInfo();
        if (CollectionUtils.isEmpty(imageIconInfos)) {
            return memberIconList;
        }
        return mergeAndSort(imageIconInfos, memberIconList);

    }

    /**
     * ???????????????????????????
     *
     * @return
     */
    private List<ImageIconInfo> genPopupConsumptionImageIcon() {
        //????????????????????????
        List<ImageIconInfo> imageIconInfos = consumptionService.queryAllConsumptionIconInfo(consumptionTypeEnum);
        if (CollectionUtils.isEmpty(imageIconInfos)) {
            return consumptionIconList;
        }
        return mergeAndSort(imageIconInfos, consumptionIconList);
    }

    /**
     * ??????->??????->??????->??????
     *
     * @param source
     * @param selected
     * @return
     */
    private List<ImageIconInfo> mergeAndSort(List<ImageIconInfo> source, List<ImageIconInfo> selected) {
        //????????????
        if (CollectionUtils.isEmpty(source)) {
            return selected;
        }

        if (CollectionUtils.isEmpty(selected)) {
            return source;
        }

        //???????????????????????????
        Map<Long, ImageIconInfo> collect = source.stream().collect(toMap(ImageIconInfo::getId, item -> item));

        //merge
        selected.forEach(iconInfo -> {
            ImageIconInfo imageIconSelect = collect.get(iconInfo.getId());
            if (imageIconSelect != null) {
                imageIconSelect.setSelected(true);
            } else {
                source.add(iconInfo);
            }
        });

        //sort
        //??????,??????????????????
        return source.stream().sorted((o1, o2) -> {
            int i1 = o1.isSelected() ? 0 : 1;
            int i2 = o2.isSelected() ? 0 : 1;
            return i1 - i2;
        }).collect(Collectors.toList());
    }

    public static class IntentKey {
        public static final String SELECT_BILL_ID = "selectBillId";
    }
}