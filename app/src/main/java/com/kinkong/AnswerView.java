package com.kinkong;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;


public class AnswerView extends FrameLayout {

    private TextView answer, voting;

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

    public void setVotingRatio(float ratio) {
        float size = answer.getWidth() * ratio;
        float minSize = getResources().getDimension(R.dimen.answer_corner);
        float maxSize = size - minSize;
        if (size < minSize) {
            size = minSize;
        } else if (size > maxSize) {
            size = maxSize;
        }
        FrameLayout.LayoutParams layoutParams = (LayoutParams) voting.getLayoutParams();
        layoutParams.width = (int) size;
        voting.setLayoutParams(layoutParams);
        voting.invalidate();
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
