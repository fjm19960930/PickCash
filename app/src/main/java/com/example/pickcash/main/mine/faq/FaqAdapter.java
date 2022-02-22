package com.example.pickcash.main.mine.faq;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pickcash.R;

import java.util.ArrayList;

public class FaqAdapter extends RecyclerView.Adapter<FaqAdapter.FaqViewHolder> {
    private ArrayList<FaqEntity> mData = new ArrayList<>();

    public FaqAdapter(ArrayList<FaqEntity> data) {
        mData.clear();
        mData.addAll(data);
    }

    @NonNull
    @Override
    public FaqViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.faq_rv_item_layout, parent, false);
        return new FaqViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FaqViewHolder holder, int position) {
        holder.title.setText(mData.get(position).title);
        holder.content.setText(mData.get(position).content);
        final int p = position;
        holder.showOrHide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData.get(p).show) {
                    rotateAnimation(holder.showOrHide, 0f, 180f);
                    alphaAnimation(holder.content, 1f, 0f);
                    mData.get(p).show = false;
                } else {
                    rotateAnimation(holder.showOrHide, 180f, 0f);
                    alphaAnimation(holder.content, 0f, 1f);
                    mData.get(p).show = true;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    private void rotateAnimation(View view, float start, float end) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "rotation", start, end);
        animator.setDuration(200);
        animator.start();
    }

    private void alphaAnimation(View view, float start, float end) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(view, "alpha", start, end);
        animator.setDuration(200);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                view.setVisibility(start == 1f ? View.GONE: View.VISIBLE);
            }
        });
        animator.start();
    }

    class FaqViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView showOrHide;
        private TextView content;

        public FaqViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.faq_item_title);
            content = itemView.findViewById(R.id.faq_item_content);
            showOrHide = itemView.findViewById(R.id.faq_item_show_and_hide);
        }
    }
}
