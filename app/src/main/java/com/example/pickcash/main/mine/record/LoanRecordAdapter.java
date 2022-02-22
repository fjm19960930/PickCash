package com.example.pickcash.main.mine.record;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickcash.R;
import com.example.pickcash.main.mine.mgr.MineMgr;

import java.util.ArrayList;

public class LoanRecordAdapter extends RecyclerView.Adapter<LoanRecordAdapter.LoanRecordViewHolder> {
    private ArrayList<LoanRecordEntity> mData = new ArrayList<>();
    private Context context;

    public LoanRecordAdapter(Activity activty, ArrayList<LoanRecordEntity> data) {
        context = activty;
        mData.clear();
        mData.addAll(data);
    }

    @NonNull
    @Override
    public LoanRecordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_rv_item_layout, parent, false);
        return new LoanRecordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LoanRecordViewHolder holder, int position) {
        holder.date.setText(mData.get(position).date);
        holder.state.setText(mData.get(position).state);
        holder.money.setText(mData.get(position).money);
        if (mData.get(position).repayBtn) {
            holder.repayBtn.setVisibility(View.VISIBLE);
            holder.repayBtn.setOnClickListener(v -> {
                if (context != null) {
                    MineMgr.getRepayLink(new MineMgr.GetRepayLinkListener() {
                        @Override
                        public void onSuccess(String link) {
                            Intent intent = new Intent(context, LoanItemDetailActivity.class);
                            intent.putExtra("link", link);
                            context.startActivity(intent);
                        }

                        @Override
                        public void onError(int code, String errorMsg) {

                        }
                    });
                }
            });
            holder.date.setTextColor(Color.parseColor("#EB1414"));
        } else {
            holder.repayBtn.setVisibility(View.GONE);
            holder.date.setTextColor(Color.parseColor("#999999"));
        }
        holder.receiptDate.setText(mData.get(position).receiptDate);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    class LoanRecordViewHolder extends RecyclerView.ViewHolder{
        private TextView date;
        private TextView state;
        private TextView money;
        private TextView repayBtn;
        private TextView receiptDate;

        public LoanRecordViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.record_item_date);
            state = itemView.findViewById(R.id.record_item_state);
            money = itemView.findViewById(R.id.record_item_money);
            repayBtn = itemView.findViewById(R.id.record_item_repay_btn);
            receiptDate = itemView.findViewById(R.id.record_item_receipt_date);
        }
    }
}
