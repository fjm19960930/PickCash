package com.example.pickcash.main.home.loan.verify;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickcash.R;
import com.example.pickcash.main.home.loan.verify.mgr.VerifyMgr;
import com.example.pickcash.main.home.loan.verify.view.SelectAdapter;
import com.zcolin.frame.app.BaseFrameActivity;

public class VerifyGetIfscActivity extends BaseFrameActivity {
    private AlertDialog selectDialog;
    private RecyclerView dialogRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_get_ifsc);

        findViewById(R.id.bank_ifsc_back_btn).setOnClickListener(v -> {
            finish();
        });

        TextView bankStateChoose = findViewById(R.id.bank_state_choose);
        TextView bankCityChoose = findViewById(R.id.bank_city_choose);
        TextView bankNameChoose = findViewById(R.id.bank_name_choose);
        TextView bankBranchChoose = findViewById(R.id.bank_branch_choose);
        bankStateChoose.setOnClickListener(v -> VerifyMgr.getIfsc("", "", new VerifyMgr.GetIfscListener() {
            @Override
            public void onSuccess(String[] items) {
                showSelectDialog(items, new SelectAdapter.SelectItemListener() {
                    @Override
                    public void onClick(String t) {
                        selectDialog.cancel();
                        bankStateChoose.setText(t);
                        bankCityChoose.setText("Please choose");
                        bankNameChoose.setText("Please choose");
                        bankBranchChoose.setText("Please choose");
                    }
                });
            }

            @Override
            public void onError(int code, String errorMsg) {

            }
        }));
        bankCityChoose.setOnClickListener(v -> {
            String state = bankStateChoose.getText().toString();
            if (state.isEmpty() || state.equals("Please choose")) {
                Toast.makeText(mActivity, "Please select the state where the bank is based", Toast.LENGTH_SHORT).show();
                return;
            }
            VerifyMgr.getIfsc("state", state, new VerifyMgr.GetIfscListener() {
                @Override
                public void onSuccess(String[] items) {
                    showSelectDialog(items, new SelectAdapter.SelectItemListener() {
                        @Override
                        public void onClick(String t) {
                            selectDialog.cancel();
                            bankCityChoose.setText(t);
                            bankNameChoose.setText("Please choose");
                            bankBranchChoose.setText("Please choose");
                        }
                    });
                }

                @Override
                public void onError(int code, String errorMsg) {

                }
            });
        });
        bankNameChoose.setOnClickListener(v -> {
            String city = bankCityChoose.getText().toString();
            if (city.isEmpty() || city.equals("Please choose")) {
                Toast.makeText(mActivity, "Please select the city or municipality where the bank is based", Toast.LENGTH_SHORT).show();
                return;
            }
            VerifyMgr.getIfsc("city", city, new VerifyMgr.GetIfscListener() {
                @Override
                public void onSuccess(String[] items) {
                    showSelectDialog(items, new SelectAdapter.SelectItemListener() {
                        @Override
                        public void onClick(String t) {
                            selectDialog.cancel();
                            bankNameChoose.setText(t);
                            bankBranchChoose.setText("Please choose");
                        }
                    });
                }

                @Override
                public void onError(int code, String errorMsg) {

                }
            });
        });
        bankBranchChoose.setOnClickListener(v -> {
            String bankName = bankNameChoose.getText().toString();
            if (bankName.isEmpty() || bankName.equals("Please choose")) {
                Toast.makeText(mActivity, "Please select the name of the bank", Toast.LENGTH_SHORT).show();
                return;
            }
            VerifyMgr.getIfsc("bank", bankName, new VerifyMgr.GetIfscListener() {
                @Override
                public void onSuccess(String[] items) {
                    showSelectDialog(items, new SelectAdapter.SelectItemListener() {
                        @Override
                        public void onClick(String t) {
                            selectDialog.cancel();
                            bankBranchChoose.setText(t);
                        }
                    });
                }

                @Override
                public void onError(int code, String errorMsg) {
                    Toast.makeText(mActivity, errorMsg, Toast.LENGTH_SHORT).show();
                }
            });
        });
        findViewById(R.id.bank_card_submit_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String bankBranch = bankBranchChoose.getText().toString();
                if (bankBranch.isEmpty() || bankBranch.equals("Please choose")) {
                    Toast.makeText(mActivity, "Please select the branch of the bank", Toast.LENGTH_SHORT).show();
                    return;
                }
                VerifyMgr.getIfsc("branch", bankBranch, new VerifyMgr.GetIfscListener() {
                    @Override
                    public void onSuccess(String[] items) {
                        Intent data = new Intent();
                        data.putExtra("ifsc", items[0]);
                        setResult(RESULT_OK, data);
                        finish();
                    }

                    @Override
                    public void onError(int code, String errorMsg) {

                    }
                });
            }
        });
        initSelectDialog();
    }

    private void showSelectDialog(String[] chooseItems, SelectAdapter.SelectItemListener listener) {
        ((SelectAdapter) dialogRecyclerView.getAdapter()).setDataAndListener(chooseItems, listener);
        selectDialog.show();
    }

    private void initSelectDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
        View view = LayoutInflater.from(mActivity).inflate(R.layout.dialog_select_layout, null);
        dialogRecyclerView = view.findViewById(R.id.rv_select);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mActivity);
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        dialogRecyclerView.setLayoutManager(linearLayoutManager);
        dialogRecyclerView.setAdapter(new SelectAdapter());
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
}