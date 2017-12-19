package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinkong.database.FBDatabase;
import com.kinkong.database.data.Question;

import kin.sdk.core.Balance;
import kin.sdk.core.ResultCallback;


public class CountDownActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, CountDownActivity.class);
    }

    private final static int MAX_HOURS = 99;
    private final static float TO_HOURS_DIVIDER = 1000 / 60 / 60;
    private final static String TELEGRAM_LINK = "https://t.me/kinfoundation";
    private Question question;
    private Animatable animatable;

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown_activity);
        question = FBDatabase.getInstance().nextQuestion;
        long countDownTime = getCountDownTime();
        if (isInvalidTime(countDownTime)) {
            ((TextView) (findViewById(R.id.next_question_title))).setText(getResources().getString(R.string.keep_me_posted));
            findViewById(R.id.clock_count_down).setVisibility(View.GONE);
            findViewById(R.id.prize_telegram).setVisibility(View.GONE);
            findViewById(R.id.join_telegram_title).setVisibility(View.VISIBLE);
        } else {
            updatePrize();
            startCountDown(countDownTime);
        }
        ImageView timer = findViewById(R.id.timer);
        animatable = ((Animatable) timer.getDrawable());

        startThreadAnimation();
    }

    @Override
    protected void onStart() {
        super.onStart();
        updatePendingBalance();
    }

    private void updatePendingBalance() {
        getApp().getKinClient().getAccount().getPendingBalance().run(new ResultCallback<Balance>() {
            @Override
            public void onResult(Balance balance) {
                updatePendingBalance(balance);
            }

            @Override
            public void onError(Exception e) {

            }
        });
    }

    private boolean isInvalidTime(long countDownTime) {
        return countDownTime < 0 || countDownTime / TO_HOURS_DIVIDER > MAX_HOURS;
    }

    private void startCountDown(long countDownTime) {
        ClockCountDownView countDownView = findViewById(R.id.clock_count_down);
        countDownView.setListener(this::startQuestion);
        countDownView.startCount(countDownTime);
    }

    private void startThreadAnimation() {
        thread = new Thread() {
            @Override
            public void run() {
                super.run();
                while (true) {
                    if (!animatable.isRunning()) {
                        startAnimation();
                    }
                }
            }
        };
        thread.start();
    }

    private void startAnimation() {
        runOnUiThread(() -> animatable.start());
    }

    @Override
    protected void onResume() {
        super.onResume();
        startThreadAnimation();
    }

    @Override
    protected void onStop() {
        super.onStop();
        thread.interrupt();
    }

    private void updatePrize() {
        TextView prize = findViewById(R.id.prize);
        String prizeStr = getPrize() + " KIN";
        prize.setText(prizeStr);
    }

    private void updatePendingBalance(Balance accountBalance) {
        TextView balance = findViewById(R.id.balance);
        String balanceStr = accountBalance.value(1) + " KIN";
        balance.setText(balanceStr);
    }

    private int getPrize() {
        return question.getPrize();
    }

    private long getCountDownTime() {
        long time = question.getTimeStamp();
        long currentTime = System.currentTimeMillis();
        return time - currentTime;
    }

    private void startQuestion() {
        Intent questionIntent = QuestionVideoActivity.getIntent(this);
        if (startScreen(questionIntent)) {
            finish();
        }
    }

    public void startAccountInfo(View view) {
        Intent accountInfoIntent = AccountInfoActivity.getInent(this);
        startScreen(accountInfoIntent);
    }

    public void startTutorial(View view) {
        Intent kinTutorialIntent = KinTutorial.getIntent(this, false);
        if (startScreen(kinTutorialIntent)) {
            finish();
        }
    }

    public void openTelegramGroup(View view) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(TELEGRAM_LINK));
        startActivity(browserIntent);
    }
}
