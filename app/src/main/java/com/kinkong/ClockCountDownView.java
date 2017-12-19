package com.kinkong;

import android.content.Context;
import android.content.res.TypedArray;
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


public class ClockCountDownView extends LinearLayout {

    interface ICountDownListener {
        void onComplete();
    }

    private static final int SEC = 1000;
    private List<TextSwitcher> txtArray = new ArrayList<>();
    private int hour0 = -1, hour1 = -1, minute0 = -1, minute1 = -1, seconds0 = -1, seconds1 = -1;
    private CountDownTimer countDownTimer;
    private ClockCountDownView.ICountDownListener listener;
    private boolean hasHours, hasMinutes;
    private ViewSwitcher.ViewFactory factory = (ViewSwitcher.ViewFactory) () -> {
        TextView textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        int fontSize = (int) (getResources().getDimension(R.dimen.count_down_font_size_big) / getResources().getDisplayMetrics().density);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, fontSize);
        textView.setTextColor(Color.WHITE);
        textView.setTypeface(Typeface.DEFAULT_BOLD);
        return textView;
    };

    public ClockCountDownView(Context context) {
        super(context, null);
        init(context);
    }

    public ClockCountDownView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.ClockCountDownView,
                0, 0);
        try {
            hasHours = a.getBoolean(R.styleable.ClockCountDownView_hours, false);
            if(hasHours){
                hasMinutes = true;
            }else {
                hasMinutes = a.getBoolean(R.styleable.ClockCountDownView_minutes, false);
            }
        } finally {
            a.recycle();
        }
        init(context);
    }

    private void init(Context context) {
        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.clock_countdown, this, true);
        txtArray.add(view.findViewById(R.id.sec0));
        txtArray.add(view.findViewById(R.id.sec1));
        if (hasMinutes) {
            view.findViewById(R.id.min0).setVisibility(VISIBLE);
            view.findViewById(R.id.min1).setVisibility(VISIBLE);
            view.findViewById(R.id.min_delim).setVisibility(VISIBLE);
            txtArray.add(view.findViewById(R.id.min0));
            txtArray.add(view.findViewById(R.id.min1));

        }
        if (hasHours) {
            view.findViewById(R.id.hour0).setVisibility(VISIBLE);
            view.findViewById(R.id.hour1).setVisibility(VISIBLE);
            view.findViewById(R.id.hours_delim).setVisibility(VISIBLE);
            txtArray.add(view.findViewById(R.id.hour0));
            txtArray.add(view.findViewById(R.id.hour1));
        }
        for (TextSwitcher textswitcher : txtArray) {
            textswitcher.setFactory(factory);
            textswitcher.setInAnimation(getContext(), R.anim.digit_in);
            textswitcher.setOutAnimation(getContext(), R.anim.digit_out);

        }
    }

    public void setListener(ClockCountDownView.ICountDownListener listener) {
        this.listener = listener;
    }


    public void startCount(long miliSeconds) {
        updateTime(miliSeconds / SEC);
        countDownTimer = new CountDownTimer(miliSeconds, SEC) {

            @Override
            public void onTick(long l) {
                updateTime(l / SEC);
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

    private void updateSec(int seconds) {
        seconds %= 60;
        int divder = seconds / 10;
        if (seconds0 != divder) {
            seconds0 = divder;
            updateDigit(1, seconds0);
        }
        int reminder = seconds - divder * 10;
        if (seconds1 != reminder) {
            seconds1 = reminder;
            updateDigit(0, seconds1);
        }
    }

    private void updateMin(int seconds) {
        seconds %= 60;
        int divider = seconds / 10;
        if (minute0 != divider) {
            minute0 = divider;
            updateDigit(3, minute0);
        }
        int reminder = seconds - divider * 10;
        if (minute1 != reminder) {
            minute1 = reminder;
            updateDigit(2, minute1);
        }
    }

    private void updateHours(int seconds) {
        seconds %= 100;
        int divider = seconds / 10;
        if (hour0 != divider) {
            hour0 = divider;
            updateDigit(5, hour0);
        }
        int reminder = seconds - divider * 10;
        if (hour1 != reminder) {
            hour1 = reminder;
            updateDigit(4, hour1);
        }
    }

    private void updateTime(long seconds) {
        updateSec((int) seconds);
        if (hasMinutes) {
            updateMin(((int) seconds / 60));
        }
        if (hasHours) {
            updateHours((int) seconds / (60 * 60));
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if(countDownTimer != null) {
            countDownTimer.cancel();
        }
        super.onDetachedFromWindow();
    }
}
