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
import com.business.travel.app.utils.LogToast;
import com.business.travel.app.utils.MoneyUtil;
import com.business.travel.app.view.Keyboard.KeyboardRecyclerViewAdapter.KeyboardRecyclerViewAdapterViewHolder;
import com.business.travel.utils.DateTimeUtil;
import com.business.travel.utils.JacksonUtil;
import com.business.travel.utils.SplitUtil;
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
import java.util.Optional;
import java.util.Stack;

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
        recyclerViewKeyboard.setLayoutManager(buildGridLayoutManager(context, 4));
        keyboardRecyclerViewAdapter = new KeyboardRecyclerViewAdapter();
        recyclerViewKeyboard.setAdapter(keyboardRecyclerViewAdapter);

        BasePopupView basePopupView = new XPopup.Builder(context).autoOpenSoftInput(true) //是否弹窗显示的同时打开输入法，只在包含输入框的弹窗内才有效，默认为false
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
        return textViewAmount.getText().toString();
    }

    /**
     * 设置金额
     *
     * @param amount
     * @return
     */
    public Keyboard setAmount(String amount) {
        textViewAmount.setText(amount);
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
        //三号为是日期框
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

    private GridLayoutManager buildGridLayoutManager(Context context, int spanCount) {
        return new GridLayoutManager(context, spanCount) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };
    }

    /**
     * 键盘布局适配器
     */
    class KeyboardRecyclerViewAdapter extends RecyclerView.Adapter<KeyboardRecyclerViewAdapterViewHolder> {

        private Stack<String> stack = new Stack<>();
        /**
         * 用于记录当前数值，不代表页面上看到的值
         */
        private double currNum = 0;
        /***
         * 默认不是小数，只有按了小数点才算小数
         */
        private boolean point = false;


        private void clearCurrNum() {
            this.currNum = 0;
            this.point = false;
        }


        private void refreshCurrNum(double num) {
            if (!point) {
                currNum = currNum * 10 + num;
            } else {
                if (currNum == 0) {
                    currNum = num * 0.1;
                } else {
                    //小数位数
                    String s = SplitUtil.trimToStringList(String.valueOf(currNum), ".").get(0);
                    double pow = Math.pow(0.1, s.length());
                    currNum = currNum + num * pow;
                }
            }
        }

        public Double calculate(String currOpt) {
            //如果栈空,说明又初始化了
            if (stack.isEmpty()) {
                //当前值入栈
                stack.push(String.valueOf(currNum));
                //操作符压栈
                stack.push(currOpt);
                //当前值变成0
                clearCurrNum();
                LogToast.infoShow(JacksonUtil.toPrettyString(stack));
                return null;
            }
            //若果栈不为空，则要计算，然后在入栈
            //先弹出的一定是操作符
            String prevOpt = stack.pop();
            //上一次计算的结果
            String prevNum = stack.pop();
            double result = 0.0;
            switch (prevOpt) {
                case "+":
                    result = BigDecimal.valueOf(Double.parseDouble(prevNum)).add(BigDecimal.valueOf(currNum)).doubleValue();
                    break;
                case "-":
                    result = BigDecimal.valueOf(Double.parseDouble(prevNum)).subtract(BigDecimal.valueOf(currNum)).doubleValue();
                    break;
            }
            stack.push(String.valueOf(result));
            stack.push(currOpt);
            clearCurrNum();
            return result;
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
                        } else {
                            return DateTimeUtil.format(date, "MM.dd");
                        }
                    }).ifPresent(holder.dateTextView::setText);

                    //键盘日期按钮事件
                    holder.dateTextView.setOnClickListener(v -> {
                        //日期按钮点击后弹出日历
                        datePickerDialog.show();
                        //日历日期点击后更新选中的日期值并更新键盘日期显示
                        datePickerDialog.setOnDateSetListener((view, year, month, dayOfMonth) -> {
                            LocalDate localDate = LocalDate.of(year, month + 1, dayOfMonth);
                            //更新选中的日期
                            selectedDate = DateTimeUtil.timestamp(localDate);
                            Optional.of(selectedDate).map(date -> {
                                if (DateTimeUtil.toLocalDateTime(date).toLocalDate().equals(LocalDate.now())) {
                                    return "今天";
                                } else {
                                    return DateTimeUtil.format(date, "MM.dd");
                                }
                            }).ifPresent(((TextView) v)::setText);
                        });
                    });
                    break;
                case 14:
                    //回退按钮
                    holder.backImageButton.setOnClickListener(v -> {
                        String amount = textViewAmount.getText().toString();
                        if (StringUtils.isBlank(amount)) {
                            return;
                        }
                        String newAmount = amount.trim().substring(0, amount.trim().length() - 1);
                        textViewAmount.setText(newAmount);
                    });
                    break;
                case 7:
                    //正号
                    holder.numButton.setText("+");
                    holder.numButton.setOnClickListener(v -> {
                        textViewAmount.append("+");
                        Double calculate = calculate("+");
                        if (calculate != null) {
                            textViewAmount.setText(calculate + "+");
                        }
                    });
                    break;
                case 11:
                    //负号
                    holder.numButton.setText("-");
                    holder.numButton.setOnClickListener(v -> {
                        textViewAmount.append("-");
                        Double calculate = calculate("-");
                        if (calculate != null) {
                            textViewAmount.setText(calculate + "-");
                        }
                    });
                    break;
                case 15:
                    holder.itemView.setBackground(ResourceUtils.getDrawable(R.drawable.corners_shape_change));
                    holder.numButton.setText("保存");
                    holder.numButton.setTextColor(ColorUtils.getColor(R.color.white));
                    holder.numButton.setOnClickListener(onSaveClick);
                    holder.numButton.setOnLongClickListener(v -> {
                        onSaveLongClick.onClick(v);
                        return true;
                    });
                    break;
                case 0:
                    holder.numButton.setText("1");
                    holder.numButton.setOnClickListener(v -> {
                        refreshCurrNum(1);
                        textViewAmount.append("1");
                    });
                    break;
                case 1:
                    holder.numButton.setText("2");
                    holder.numButton.setOnClickListener(v -> {
                        refreshCurrNum(2);
                        textViewAmount.append("2");
                    });
                    break;
                case 2:
                    holder.numButton.setText("3");
                    holder.numButton.setOnClickListener(v -> {
                        refreshCurrNum(3);
                        textViewAmount.append("3");
                    });
                    break;
                case 8:
                    holder.numButton.setText("7");
                    holder.numButton.setOnClickListener(v -> {
                        refreshCurrNum(7);
                        textViewAmount.append("7");
                    });
                    break;
                case 9:
                    holder.numButton.setText("8");
                    holder.numButton.setOnClickListener(v -> {
                        refreshCurrNum(8);
                        textViewAmount.append("8");
                    });
                    break;
                case 10:
                    holder.numButton.setText("9");
                    holder.numButton.setOnClickListener(v -> {
                        refreshCurrNum(9);
                        textViewAmount.append("9");
                    });
                    break;
                case 13:
                    holder.numButton.setText("0");
                    holder.numButton.setOnClickListener(v -> {
                        refreshCurrNum(0);
                        textViewAmount.append("0");
                    });
                    break;
                case 12:
                    holder.numButton.setText(".");
                    holder.numButton.setOnClickListener(v -> {
                        //当前已经是小数了,在按无效
                        if (point) {
                            return;
                        }
                        point = true;
                        //如果已经包含点了,再点击没有效果
                        textViewAmount.append(".");
                    });
                    break;
                default:
                    holder.numButton.setText(String.valueOf(position));
                    holder.numButton.setOnClickListener(v -> {
                        refreshCurrNum(position);
                        textViewAmount.append(String.valueOf(position));
                    });
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
