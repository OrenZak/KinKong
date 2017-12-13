package com.kinkong;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import java.util.ArrayList;
import java.util.List;


public class CountDownShortView extends LinearLayout {

    interface ICountDownListener {
        void onComplete();
    }

    List<TextSwitcher> txtArray = new ArrayList<>();
    int seconds0 = -1, seconds1 = -1;
    CountDownTimer countDownTimer;
    ICountDownListener listener;

    ViewSwitcher.ViewFactory factory = (ViewSwitcher.ViewFactory) () -> {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        //TODO need to fix this not const
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 34);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        return textView;
    };

    public CountDownShortView(Context context) {
        super(context, null);
        init(context);
    }

    public CountDownShortView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CountDownShortView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.countdown_short, this, true);
        txtArray.add(view.findViewById(R.id.sec0));
        txtArray.add(view.findViewById(R.id.sec1));

        for (TextSwitcher textswitcher : txtArray) {
            textswitcher.setFactory(factory);
            textswitcher.setInAnimation(getContext(), R.anim.digit_in);
            textswitcher.setOutAnimation(getContext(), R.anim.digit_out);

        }
    }

    public void setListener(ICountDownListener listener) {
        this.listener = listener;
    }


    public void startCount(int miliSeconds) {
        updateTime(miliSeconds);
        countDownTimer = new CountDownTimer(miliSeconds, 1000) {

            @Override
            public void onTick(long l) {
                updateTime((int)l / 1000);
            }

            @Override
            public void onFinish() {
                updateTime(0);
                if (listener != null) {
                    listener.onComplete();
                }
            }
        };
        countDownTimer.start();
    }

    private void updateDigit(int index, int digit) {
        txtArray.get(index).setText(String.valueOf(digit));
    }

    private void updateTime(int seconds) {
        int s0 = seconds / 10;

        if (s0 != seconds0) {
            seconds0 = s0;
            updateDigit(0, seconds0);
        }

        int s1 = seconds - s0 * 10;
        if (s1 != seconds1) {
            seconds1 = s1;
            updateDigit(1, seconds1);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        countDownTimer.cancel();
        super.onDetachedFromWindow();
    }
}
