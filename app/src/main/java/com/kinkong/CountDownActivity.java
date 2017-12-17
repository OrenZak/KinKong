package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.kinkong.database.FBDatabase;
import com.kinkong.database.data.Question;

import kin.sdk.core.Balance;
import kin.sdk.core.KinClient;
import kin.sdk.core.ResultCallback;


public class CountDownActivity extends BaseActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, CountDownActivity.class);
    }

    private Question question;
    private Animatable animatable;

    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown_activity);
        question = FBDatabase.getInstance().nextQuestion;
        KinClient kinClient = ((App) getApplication()).getKinClient();

        CountDownView countDownView = findViewById(R.id.countdown_view);
        countDownView.setListener(this::startQuestion);
        countDownView.startCount(getCountDownTime());

        updatePrize();
        kinClient.getAccount().getBalance().run(new ResultCallback<Balance>() {
            @Override
            public void onResult(Balance balance) {
                updateBalance(balance);
            }

            @Override
            public void onError(Exception e) {

            }
        });

        ImageView timer = findViewById(R.id.timer);
        animatable = ((Animatable) timer.getDrawable());

        startThreadAnimation();
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

    private void updateBalance(Balance accountBalance) {
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
}
