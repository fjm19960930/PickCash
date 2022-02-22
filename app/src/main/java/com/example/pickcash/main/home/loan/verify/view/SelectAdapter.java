package com.example.pickcash.main.home.loan.verify.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickcash.R;

public class SelectAdapter extends RecyclerView.Adapter<SelectAdapter.SelectViewHolder> {
    private String[] mData = new String[]{};
    private SelectItemListener mListener;

    public SelectAdapter() { }

    public SelectAdapter(String[] data, SelectItemListener listener) {
        if (data != null && data.length > 0) {
            mData = new String[]{};
            mData = data;
        }
        mListener = listener;
    }

    public void setDataAndListener(String[] data, SelectItemListener listener) {
        if (data != null && data.length > 0) {
            mData = new String[]{};
            mData = data;
        }
        mListener = listener;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SelectViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.select_item_layout, parent, false);
        return new SelectViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SelectViewHolder holder, int position) {
        holder.item.setText(mData[position]);
        holder.item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onClick(mData[position]);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.length;
    }

    class SelectViewHolder extends RecyclerView.ViewHolder{
        private TextView item;

        public SelectViewHolder(@NonNull View itemView) {
            super(itemView);
            item = itemView.findViewById(R.id.select_item);
        }
    }

    public interface SelectItemListener {
        void onClick(String t);
    }
}
