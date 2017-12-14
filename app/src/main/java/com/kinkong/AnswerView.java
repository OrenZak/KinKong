package com.kinkong;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;


public class AnswerView extends FrameLayout {

    private TextView answer, voting;
    private static final int MAX_ANIM = 3000;
    private static final int MIN_ANIM = 500;

    public AnswerView(Context context) {
        super(context, null);
        init(context);
    }

    public AnswerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AnswerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.answer, this, true);
        answer = view.findViewById(R.id.answer);
        voting = view.findViewById(R.id.voting);
    }

    public void setAnswer(String answer) {
        this.answer.setText(answer);
    }

    @Override
    public void setSelected(boolean selected) {
        super.setSelected(selected);
        answer.setSelected(selected);
    }

    public void animateVoting(float ratio) {
        int endWidth = (int) (answer.getWidth() * ratio);
        int minSize = (int) getResources().getDimension(R.dimen.answer_corner);
        int maxSize = endWidth - minSize;
        if (endWidth < minSize) {
            endWidth = minSize;
        } else if (endWidth > maxSize) {
            endWidth = maxSize;
        }
        int startWidth = (int) minSize;

        ValueAnimator anim = ValueAnimator.ofInt(startWidth, endWidth);
        anim.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            LayoutParams layoutParams = (LayoutParams) voting.getLayoutParams();
            layoutParams.width = val;
            voting.setLayoutParams(layoutParams);
        });
        long duration = (long) (ratio * MAX_ANIM + MIN_ANIM + Math.random() * MAX_ANIM / 2);
        anim.setDuration(duration);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    public void markCorrect() {
        Drawable background = voting.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) background;
        gradientDrawable.setColor(ContextCompat.getColor(getContext(), R.color.blue_correct));
    }

    public void markRatio() {
        Drawable background = voting.getBackground();
        GradientDrawable gradientDrawable = (GradientDrawable) background;
        gradientDrawable.setColor(ContextCompat.getColor(getContext(), R.color.answer_ratio));
    }

}
