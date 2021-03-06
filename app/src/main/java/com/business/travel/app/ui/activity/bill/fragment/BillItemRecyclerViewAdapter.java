package com.business.travel.app.ui.activity.bill.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.blankj.utilcode.util.CollectionUtils;
import com.business.travel.app.R;
import com.business.travel.app.dal.entity.Bill;
import com.business.travel.app.enums.MasterFragmentPositionEnum;
import com.business.travel.app.model.ImageIconInfo;
import com.business.travel.app.service.BillService;
import com.business.travel.app.service.ConsumptionService;
import com.business.travel.app.ui.activity.bill.DetailBillActivity;
import com.business.travel.app.ui.activity.bill.fragment.BillItemRecyclerViewAdapter.BillItemRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.activity.bill.fragment.BillRecyclerViewAdapter.BillRecyclerViewAdapterViewHolder;
import com.business.travel.app.ui.base.BaseRecyclerViewAdapter;
import com.business.travel.app.utils.ImageLoadUtil;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.utils.SplitUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.XPopup.Builder;
import com.lxj.xpopup.impl.AttachListPopupView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author chenshang
 */
public class BillItemRecyclerViewAdapter extends BaseRecyclerViewAdapter<BillItemRecyclerViewAdapterViewHolder, Bill> {

    private final BillFragment billFragment = MasterFragmentPositionEnum.BILL_FRAGMENT.getFragment();
    //这个是父类 viewHolder
    private final BillRecyclerViewAdapterViewHolder viewHolder;
    //注入service
    private BillService billService;
    private ConsumptionService consumptionService;

    public BillItemRecyclerViewAdapter(List<Bill> bills, Context context, BillRecyclerViewAdapterViewHolder holder) {
        super(bills, context);
        this.viewHolder = holder;
    }

    @Override
    protected void inject() {
        billService = new BillService(context.getApplicationContext());
        consumptionService = new ConsumptionService(context.getApplicationContext());
    }

    @NonNull
    @NotNull
    @Override
    public BillItemRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_bill_item, parent, false);
        return new BillItemRecyclerViewAdapterViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull BillItemRecyclerViewAdapterViewHolder holder, int position) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        Bill bill = dataList.get(position);
        if (bill == null) {
            return;
        }

        holder.iconImageView.setBackgroundResource(R.drawable.corners_shape_select);
        ImageLoadUtil.loadImageToView(bill.getIconDownloadUrl(), holder.iconImageView);

        String name = bill.getConsumptionIds();
        String remark = bill.getRemark();
        if (StringUtils.isNotBlank(remark)) {
            //备注不为空,消费项就显示备注
            holder.consumptionItemTextView.setText(remark);
            //否则就展示消费项的名称
        } else if (StringUtils.isNotBlank(name)) {
            List<ImageIconInfo> consumptions = consumptionService.queryByIds(SplitUtil.trimToLongList(name));
            String names = consumptions.stream().map(ImageIconInfo::getName).collect(Collectors.joining(","));
            holder.consumptionItemTextView.setText(names);
        }

        Long amount = bill.getAmount();
        String type = bill.getConsumptionType();
        String amountText = "";
        if (ConsumptionTypeEnum.INCOME.name().equals(type)) {
            amountText = MoneyUtil.toYuanString(amount);
        } else if (ConsumptionTypeEnum.SPENDING.name().equals(type)) {
            amountText = "-" + MoneyUtil.toYuanString(amount);
        }
        holder.amountTextView.setText(amountText);

        holder.cardView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailBillActivity.class);
            intent.putExtra(DetailBillActivity.IntentKey.SELECT_BILL_ID, bill.getId());
            context.startActivity(intent);
        });

        //长按弹出删除、编辑按钮
        AttachListPopupView attachListPopupView = newAttachListPopupView(holder, position, bill);
        //长按弹出删除、编辑按钮
        holder.cardView.setOnLongClickListener(v -> {
            attachListPopupView.show();
            return true;
        });
    }

    private AttachListPopupView newAttachListPopupView(@NotNull BillItemRecyclerViewAdapterViewHolder holder, int position, Bill bill) {
        return new Builder(context).watchView(holder.cardView).asAttachList(new String[]{"删除"}, new int[]{R.drawable.ic_base_delete}, (pos, text) -> {
            switch (pos) {
                case 0:
                    delete(position, bill);
                    break;
                default:
                    //do nothing
            }
        });
    }

    private void delete(int position, Bill bill) {
        //弹出确认删除对话框
        new XPopup.Builder(context).asConfirm("", "是否要删除该账目", () -> confirmDelete(position, bill)).show();
    }

    //确认删除
    private void confirmDelete(int position, Bill bill) {
        //1. 数据库删除
        billService.deleteBillById(bill.getId());
        //2. 列表移除
        dataList.remove(bill);
        this.notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataList.size() - position);
        //刷新右上角金额
        billFragment.refreshMoneyShow(bill.getProjectId());
        //如果当前数据list没有了
        if (CollectionUtils.isEmpty(dataList)) {
            //刷新整个列表
            billFragment.refreshBillList(bill.getProjectId());
        }
        //刷新对应的天的金额
        billFragment.getBillRecyclerViewAdapter().refreshMoneyShow(viewHolder, bill.getProjectId(), bill.getConsumeDate());
    }

    @SuppressLint("NonConstantResourceId")
    static class BillItemRecyclerViewAdapterViewHolder extends ViewHolder {

        @BindView(R.id.UI_BillFragment_BillItemAdapter_Icon)
        public ImageView iconImageView;
        @BindView(R.id.UI_BillFragment_BillItemAdapter_Amount)
        public TextView amountTextView;
        @BindView(R.id.UI_BillFragment_BillItemAdapter_ConsumptionItem)
        public TextView consumptionItemTextView;
        @BindView(R.id.card)
        public CardView cardView;

        public BillItemRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
