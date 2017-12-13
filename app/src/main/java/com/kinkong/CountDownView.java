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


public class CountDownView extends LinearLayout {

    interface ICountDownListener {
        void onComplete();
    }

    List<TextSwitcher> txtArray = new ArrayList<>();
    int hour0 = -1, hour1 = -1, minute0 = -1, minute1 = -1, seconds0 = -1, seconds1 = -1;
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

    public CountDownView(Context context) {
        super(context, null);
        init(context);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.countdown_long, this, true);
        txtArray.add(view.findViewById(R.id.hour0));
        txtArray.add(view.findViewById(R.id.hour1));
        txtArray.add(view.findViewById(R.id.min0));
        txtArray.add(view.findViewById(R.id.min1));
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


    public void startCount(long miliSeconds) {
        updateTime(miliSeconds);
        countDownTimer = new CountDownTimer(miliSeconds, 1000) {

            @Override
            public void onTick(long l) {
                updateTime(l / 1000);
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
        txtArray.get(index).setText(String.valueOf(digit).trim());
    }

    private void updateTime(long longSeconds) {
        int hours = (int) longSeconds / 3600;
        int remainder = (int) longSeconds - hours * 3600;
        int minutes = remainder / 60;
        remainder = remainder - minutes * 60;
        int seconds = remainder;

        int h0 = hours / 10;
        int h1 = hours - h0 * 10;

        if (h0 != hour0) {
            hour0 = h0;
            updateDigit(0, hour0);
        }
        if (h1 != hour1) {
            hour1 = h1;
            updateDigit(1, hour1);
        }

        int m0 = minutes / 10;

        if (m0 != minute0) {
            minute0 = m0;
            updateDigit(2, minute0);
        }
        int m1 = minutes - m0 * 10;
        if (m1 != minute1) {
            minute1 = m1;
            updateDigit(3, minute1);
        }

        int s0 = seconds / 10;

        if (s0 != seconds0) {
            seconds0 = s0;
            updateDigit(4, seconds0);
        }

        int s1 = seconds - s0 * 10;
        if (s1 != seconds1) {
            seconds1 = s1;
            updateDigit(5, seconds1);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        countDownTimer.cancel();
        super.onDetachedFromWindow();
    }
}
