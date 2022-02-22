package com.example.pickcash.main.home.loan.verify.view;

import android.Manifest;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickcash.PickCashApplication;
import com.example.pickcash.R;
import com.zcolin.frame.permission.PermissionHelper;
import com.zcolin.frame.permission.PermissionsResultAction;

public class PersonalContactItemView extends LinearLayout {
    private Context mContext;
    private AlertDialog selectDialog;
    private String[] mChooseItems = new String[]{"HUSBAND_AND_WIFE", "PARENTS", "CHILDREN", "BROTHER", "SISTER", "FRIEND", "COLLEAGUES", "OTHER"};
    private TextView relationChoose;
    private ContactClickListener mListener;
    private TextView numChoose;
    private int position;

    private String mName;
    private String mPhone;

    public PersonalContactItemView(Context context, int p, ContactClickListener listener) {
        super(context);
        mContext = context;
        position = p;
        mListener = listener;
        initView(context);
    }

    public PersonalContactItemView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    public PersonalContactItemView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
    }

    private void initView(Context context) {
        if (context != null) {
            View view = LayoutInflater.from(context).inflate(R.layout.personal_info_contact_item_layout, this);
            relationChoose = view.findViewById(R.id.personal_contact_relation_choose);
            relationChoose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    selectDialog.show();
                }
            });
            numChoose = view.findViewById(R.id.personal_contact_num_choose);
            numChoose.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (PickCashApplication.mHasGetContactPermission) {
                        if (mListener != null) {
                            mListener.onClick(position);
                        }
                    } else {
                        contactPermissionApply();
                    }
                }
            });
        }
        initSelectDialog();
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
            if (relationChoose != null) {
                relationChoose.setText(t);
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

    public void setPhoneNum(String name, String num) {
        if (numChoose != null) {
            mName = name;
            mPhone = num;
            numChoose.setText(name + "\n" + num);
        }
    }

    public boolean hasFilled() {
        return relationChoose.getText().length() > 0 && !relationChoose.getText().equals("Please choose") && numChoose.getText().length() > 0;
    }

    private void contactPermissionApply() {
        //TODO 需要自定义
        String[] permissions = new String[]{Manifest.permission.READ_CONTACTS};
        PermissionHelper.requestPermission(mContext, permissions, new PermissionsResultAction() {
            @Override
            public void onGranted() {//权限申请成功
                PickCashApplication.mHasGetContactPermission = true;
                if (mListener != null) {
                    mListener.onClick(position);
                }
            }

            @Override
            public void onDenied(String permission) {//权限申请失败
                Toast.makeText(mContext, "Failed to get the information as your access to contact list was rejected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public TextView getRelationChoose() {
        return relationChoose;
    }

    public String getName() {
        return mName;
    }

    public String getPhone() {
        return mPhone;
    }

    public interface ContactClickListener{
        void onClick(int position);
    }
}
