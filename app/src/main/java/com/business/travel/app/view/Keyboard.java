package com.business.travel.app.view;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import com.blankj.utilcode.util.ColorUtils;
import com.blankj.utilcode.util.ResourceUtils;
import com.business.travel.app.R;
import com.business.travel.app.view.Keyboard.KeyboardRecyclerViewAdapter.KeyboardRecyclerViewAdapterViewHolder;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.vo.enums.ConsumptionTypeEnum;
import com.kyleduo.switchbutton.SwitchButton;
import com.lxj.xpopup.XPopup;
import com.lxj.xpopup.core.BasePopupView;
import com.yanzhenjie.recyclerview.SwipeRecyclerView;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义键盘
 */
public class Keyboard extends ConstraintLayout {

    /**
     * 收入支出显示tag
     */
    public final SwitchButton switchButton;
    /**
     * 金额展示
     */
    public final TextView textViewAmount;
    /**
     * 备注编辑框
     */
    public final EditText editTextRemark;
    /**
     * 日历弹框
     */
    private final DatePickerDialog datePickerDialog;
    /**
     * 键盘适配器,可以通过这个控制键盘的行为
     */
    private final KeyboardRecyclerViewAdapter keyboardRecyclerViewAdapter;
    /**
     * 选中的时间
     */
    private Long selectedDate = DateTimeUtil.timestamp(LocalDate.now());
    /**
     * 点击保存的时候的动作行为
     */
    private OnClickListener onSaveClick;
    /**
     * 长按保存时候的动作行为
     */
    private OnClickListener onSaveLongClick;

    public Keyboard(Context context, AttributeSet attrs) {
        super(context, attrs);
        //键盘布局
        View inflate = LayoutInflater.from(context).inflate(R.layout.layout_keyboard, this);
        //备注编辑框
        editTextRemark = inflate.findViewById(R.id.EditText_Remark);
        //收入支出显示tag
        switchButton = inflate.findViewById(R.id.TextView_PayType);
        //金额展示
        textViewAmount = inflate.findViewById(R.id.TextView_Amount);

        LocalDateTime now = DateTimeUtil.now();
        datePickerDialog = new DatePickerDialog(context, (view, year, month, dayOfMonth) -> {
        }, now.getYear(), now.getMonth().getValue() - 1, now.getDayOfMonth());

        //键盘数字布局
        SwipeRecyclerView recyclerViewKeyboard = inflate.findViewById(R.id.RecyclerView_Keyboard);

        //4*4 布局
        recyclerViewKeyboard.setLayoutManager(new GridLayoutManager(context, 4) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        keyboardRecyclerViewAdapter = new KeyboardRecyclerViewAdapter();
        recyclerViewKeyboard.setAdapter(keyboardRecyclerViewAdapter);

        BasePopupView basePopupView = new XPopup.Builder(context)
                //是否弹窗显示的同时打开输入法，只在包含输入框的弹窗内才有效，默认为false
                .autoOpenSoftInput(true)
                //自定义
                .asCustom(new BottomRemarkEditPopupView(context, this));
        editTextRemark.setFocusable(false);
        editTextRemark.setOnClickListener(v -> basePopupView.show());

    }

    /**
     * 设置 remark 显示
     */
    public String getRemark() {
        return editTextRemark.getText().toString();
    }

    /**
     * 设置 remark 显示
     */
    public Keyboard setRemark(String remark) {
        editTextRemark.setText(remark);
        return this;
    }

    /**
     * 消费类型
     */
    public ConsumptionTypeEnum getConsumptionType() {
        if (switchButton.isChecked()) {
            //选中展示支出
            return ConsumptionTypeEnum.SPENDING;
        } else {
            //没有选中展示收入
            return ConsumptionTypeEnum.INCOME;
        }
    }

    /**
     * 设置 消费类型
     */
    public Keyboard setConsumptionType(ConsumptionTypeEnum consumptionType) {
        if (ConsumptionTypeEnum.SPENDING == consumptionType) {
            //选中展示支出
            switchButton.setChecked(true);
        } else if (ConsumptionTypeEnum.INCOME == consumptionType) {
            //没有选中展示收入
            switchButton.setChecked(false);
        }
        return this;
    }

    /**
     * 获取金额
     *
     * @return
     */
    public String getAmount() {
        return textViewAmount.getText().toString().trim();
    }

    /**
     * 设置金额
     *
     * @param amount
     * @return
     */
    public Keyboard setAmount(String amount) {
        textViewAmount.setText(amount == null ? null : amount.trim());
        return this;
    }

    /**
     * 获取选择时间
     *
     * @return
     */
    public Long getDate() {
        return selectedDate;
    }

    /**
     * 设置时间
     *
     * @param date
     * @return
     */
    public Keyboard setDate(Long date) {
        selectedDate = date;
        keyboardRecyclerViewAdapter.notifyItemChanged(3);
        return this;
    }

    public Keyboard onSaveClick(OnClickListener onSaveClick) {
        this.onSaveClick = onSaveClick;
        return this;
    }

    public Keyboard onSaveLongClick(OnClickListener onSaveLongClick) {
        this.onSaveLongClick = onSaveLongClick;
        return this;
    }

    public Keyboard onSwitchClick(OnClickListener onSwitchClick) {
        switchButton.setOnCheckedChangeListener((buttonView, isChecked) -> onSwitchClick.onClick(buttonView));
        return this;
    }

    /**
     * 键盘布局适配器
     */
    class KeyboardRecyclerViewAdapter extends RecyclerView.Adapter<KeyboardRecyclerViewAdapterViewHolder> {
        /**
         * 保存按钮展示的是等号还是保存
         */
        private String saveButtonText = "保存";

        public void refreshSaveButtonText(String saveButtonText) {
            if (this.saveButtonText.equals(saveButtonText)) {
                return;
            }

            this.saveButtonText = saveButtonText;
            notifyItemChanged(15);
        }

        /**
         * 键盘计算器的核心逻辑
         *
         * @return
         */
        private String calculate() {
            String amount = getAmount();
            if (StringUtils.isBlank(amount)) {
                return "";
            }

            if (amount.startsWith("+") || amount.startsWith("-")) {
                amount = "0" + amount;
            }

            if (amount.endsWith("-") || amount.endsWith("+")) {
                amount = amount + "0";
            }

            Pattern pattern = Pattern.compile("[+\\-]");
            Matcher matcher = pattern.matcher(amount);
            String[] numArray = pattern.split(amount);
            //操作数入队
            Queue<String> numQueues = new LinkedList<>(Arrays.asList(numArray));

            //操作符号入队
            Queue<String> optQueues = new LinkedList<>();
            while (matcher.find()) {
                optQueues.add(matcher.group().trim());
            }

            //第一个数
            BigDecimal resBig = new BigDecimal(numQueues.poll());
            while (!optQueues.isEmpty()) {
                //操作符出队
                String opt = optQueues.poll();
                String nextNum = numQueues.poll();
                BigDecimal nextBig = new BigDecimal(nextNum);

                //求和
                if ("+".equals(opt)) {
                    resBig = resBig.add(nextBig);
                }
                //作差
                if ("-".equals(opt)) {
                    resBig = resBig.subtract(nextBig);
                }
            }
            return resBig.toString();
        }

        @NonNull
        @NotNull
        @Override
        public KeyboardRecyclerViewAdapterViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
            final LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case 3:
                    //date 日期按钮样式
                    return new KeyboardRecyclerViewAdapterViewHolder(layoutInflater.inflate(R.layout.recyclerview_keyboard_item_date, parent, false));
                case 14:
                    //回退按钮样式
                    return new KeyboardRecyclerViewAdapterViewHolder(layoutInflater.inflate(R.layout.recyclerview_keyboard_item_back, parent, false));
                default:
                    return new KeyboardRecyclerViewAdapterViewHolder(layoutInflater.inflate(R.layout.recyclerview_keyboard_item_num, parent, false));
            }
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull @NotNull KeyboardRecyclerViewAdapterViewHolder holder, int position) {
            switch (position) {
                case 3:
                    //键盘日期显示的值
                    Optional.ofNullable(selectedDate).map(date -> {
                        if (DateTimeUtil.toLocalDateTime(date).toLocalDate().equals(LocalDate.now())) {
                            return "今天";
                        }
                        return DateTimeUtil.format(date, "MM.dd");
                    }).ifPresent(holder.dateTextView::setText);

                    //日历日期点击后更新选中的日期值并更新键盘日期显示
                    datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                        LocalDate localDate = LocalDate.of(year, month + 1, dayOfMonth);
                        //更新选中的日期
                        selectedDate = DateTimeUtil.timestamp(localDate);
                        notifyItemChanged(3);
                    });

                    //键盘日期按钮事件
                    //日期按钮点击后弹出日历
                    holder.dateTextView.setOnClickListener(v -> datePickerDialog.show());
                    break;
                case 14:
                    //回退按钮
                    holder.backImageButton.setOnClickListener(v -> {
                        String amount = getAmount();
                        if (StringUtils.isBlank(amount)) {
                            return;
                        }
                        String newAmount = amount.trim().substring(0, amount.trim().length() - 1);
                        setAmount(newAmount);
                        showEqualsOrSave();
                    });
                    break;
                case 7:
                    //正号
                    holder.numButton.setText("+");
                    holder.numButton.setOnClickListener(v -> {
                        setAmount(calculate() + "+");
                        showEqualsOrSave();
                    });
                    break;
                case 11:
                    //负号
                    holder.numButton.setText("-");
                    holder.numButton.setOnClickListener(v -> {
                        setAmount(calculate() + "-");
                        showEqualsOrSave();
                    });
                    break;
                case 15:
                    holder.itemView.setBackground(ResourceUtils.getDrawable(R.drawable.corners_shape_change));
                    holder.numButton.setText(saveButtonText);
                    holder.numButton.setTextColor(ColorUtils.getColor(R.color.white));
                    holder.numButton.setOnClickListener(v -> {
                        if ("保存".equals(saveButtonText)) {
                            onSaveClick.onClick(v);
                            return;
                        }

                        if ("=".equals(saveButtonText)) {
                            setAmount(calculate());
                        }
                        showEqualsOrSave();
                    });
                    holder.numButton.setOnLongClickListener(v -> {
                        if ("=".equals(saveButtonText)) {
                            setAmount(calculate());
                        }
                        onSaveLongClick.onClick(v);
                        showEqualsOrSave();
                        return true;
                    });
                    break;
                case 12:
                    holder.numButton.setText(".");
                    holder.numButton.setOnClickListener(v -> {
                        String amount = getAmount();
                        if (!amount.contains("+") && !amount.contains("-") && amount.contains(".")) {
                            return;
                        }

                        if (!amount.endsWith("-") || !amount.endsWith("+")) {
                            String[] split = amount.split("[+\\-]");
                            if (split.length > 1 && split[1].contains(".")) {
                                return;
                            }
                        }

                        textViewAmount.append(".");
                    });
                    break;
                case 0:
                    holder.numButton.setText("1");
                    holder.numButton.setOnClickListener(v -> {
                        if (!canAppend()) {
                            return;
                        }
                        textViewAmount.append("1");
                        showEqualsOrSave();
                    });
                    break;
                case 1:
                    holder.numButton.setText("2");
                    holder.numButton.setOnClickListener(v -> {
                        if (!canAppend()) {
                            return;
                        }
                        textViewAmount.append("2");
                        showEqualsOrSave();
                    });
                    break;
                case 2:
                    holder.numButton.setText("3");
                    holder.numButton.setOnClickListener(v -> {
                        if (!canAppend()) {
                            return;
                        }
                        textViewAmount.append("3");
                        showEqualsOrSave();
                    });
                    break;
                case 8:
                    holder.numButton.setText("7");
                    holder.numButton.setOnClickListener(v -> {
                        if (!canAppend()) {
                            return;
                        }
                        textViewAmount.append("7");
                        showEqualsOrSave();
                    });
                    break;
                case 9:
                    holder.numButton.setText("8");
                    holder.numButton.setOnClickListener(v -> {
                        if (!canAppend()) {
                            return;
                        }
                        textViewAmount.append("8");
                        showEqualsOrSave();
                    });
                    break;
                case 10:
                    holder.numButton.setText("9");
                    holder.numButton.setOnClickListener(v -> {
                        if (!canAppend()) {
                            return;
                        }
                        textViewAmount.append("9");
                        showEqualsOrSave();
                    });
                    break;
                case 13:
                    holder.numButton.setText("0");
                    holder.numButton.setOnClickListener(v -> {
                        if (!canAppend()) {
                            return;
                        }
                        textViewAmount.append("0");
                        showEqualsOrSave();
                    });
                    break;
                default:
                    holder.numButton.setText(String.valueOf(position));
                    holder.numButton.setOnClickListener(v -> {
                        if (!canAppend()) {
                            return;
                        }
                        textViewAmount.append(String.valueOf(position));
                        showEqualsOrSave();
                    });
            }
        }

        private boolean canAppend() {
            String amount = getAmount();
            if (StringUtils.isBlank(amount)) {
                return true;
            }
            if (amount.endsWith("-") || amount.endsWith("+")) {
                return true;
            }

            //最大8位整数
            if (!amount.contains("+") && !amount.contains("-") && !amount.contains(".") && amount.length() < 8) {
                return true;
            }

            if (!amount.contains("+") && !amount.contains("-") && amount.contains(".") && amount.length() < amount.lastIndexOf(".") + 3) {
                return true;
            }

            if (amount.contains("+") || amount.contains("-")) {
                String[] split = amount.split("[+\\-]");
                String s = split[1];
                if (!s.contains(".") && s.length() < 8) {
                    return true;
                }

                if (s.contains(".") && s.length() < s.lastIndexOf(".") + 3) {
                    return true;
                }
            }

            return false;
        }

        private void showEqualsOrSave() {
            if (getAmount().contains("+") || getAmount().contains("-")) {
                refreshSaveButtonText("=");
            } else {
                refreshSaveButtonText("保存");
            }
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        @Override
        public int getItemCount() {
            return 16;
        }

        class KeyboardRecyclerViewAdapterViewHolder extends ViewHolder {
            //普通的数字按钮
            public Button numButton;
            //删除按钮
            public ImageButton backImageButton;
            //日期按钮
            public TextView dateTextView;

            public KeyboardRecyclerViewAdapterViewHolder(@NonNull @NotNull View itemView) {
                super(itemView);
                dateTextView = itemView.findViewById(R.id.UI_KeyboardItem_TextView_Date);
                numButton = itemView.findViewById(R.id.UI_KeyboardItem_Button_Num);
                backImageButton = itemView.findViewById(R.id.UI_KeyboardItem_ImageButton_Back);
            }
        }
    }
}
