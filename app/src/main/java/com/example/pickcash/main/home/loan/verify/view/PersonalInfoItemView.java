package com.example.pickcash.main.home.loan.verify.view;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickcash.R;
import com.example.pickcash.util.NumberUtils;

public class PersonalInfoItemView extends LinearLayout {
    private String mTitle = "";
    private boolean mIsChoose = false;
    private String[] mChooseItems = new String[]{};
    private Context mContext;
    private AlertDialog selectDialog;
    private TextView chooseBtn;
    private boolean mIsMust;//是否是必填项
    private String enterStr = "";
    private EditText enter;

    public PersonalInfoItemView(Context context) {
        super(context);
        mContext = context;
        initView(context);
    }

    public PersonalInfoItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    public PersonalInfoItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
    }

    public PersonalInfoItemView(Context context, String t, boolean choose, String[] items, boolean isMust) {
        super(context);
        mContext = context;
        mTitle = t;
        mIsChoose = choose;
        mIsMust = isMust;
        if (items != null) {
            mChooseItems = items;
        }
        initView(context);
    }

    private void initView(Context context) {
        if (context != null) {
            View view = LayoutInflater.from(context).inflate(R.layout.personal_info_item_layout, this);
            TextView title = view.findViewById(R.id.personal_item_title);
            title.setText(mIsMust ? "*" + mTitle : mTitle);
            enter = view.findViewById(R.id.personal_item_enter);
            enter.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    enterStr = s.toString();
                }
            });
            chooseBtn = view.findViewById(R.id.personal_item_choose);
            if (mIsChoose) {
                enter.setVisibility(View.GONE);
                chooseBtn.setVisibility(View.VISIBLE);
                chooseBtn.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectDialog.show();
                    }
                });
            } else {
                enter.setVisibility(View.VISIBLE);
                chooseBtn.setVisibility(View.GONE);
            }
            initSelectDialog();
        }
    }

    private void initSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_select_layout, null);
        RecyclerView rv = view.findViewById(R.id.rv_select);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        rv.setLayoutManager(linearLayoutManager);
        SelectAdapter adapter = new SelectAdapter(mChooseItems, t -> {
            selectDialog.cancel();
            if (chooseBtn != null) {
                chooseBtn.setText(t);
            }
        });
        rv.setAdapter(adapter);
        TextView btnCancel = view.findViewById(R.id.btn_select_cancel);
        btnCancel.setOnClickListener(v -> selectDialog.cancel());

        builder.setView(view);
        selectDialog = builder.create();
        selectDialog.setCancelable(false);
        selectDialog.setCanceledOnTouchOutside(false);

        Window window = selectDialog.getWindow();
        assert window != null;
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public boolean isEmail() {
        return NumberUtils.isEmail(enterStr);
    }

    public boolean hasFilled() {
        if (!mIsMust) {
            return true;
        } else {
            if (!mIsChoose) {
                return enterStr.length() > 0;
            } else {
                return chooseBtn.getText().length() > 0 && !chooseBtn.getText().equals("Please choose");
            }
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public TextView getChooseBtn() {
        return chooseBtn;
    }

    public String getEnterStr() {
        return enterStr;
    }
}
