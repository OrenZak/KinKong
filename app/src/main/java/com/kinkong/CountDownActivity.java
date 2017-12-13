package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.kinkong.database.FBDatabase;

import kin.sdk.core.Balance;
import kin.sdk.core.KinClient;
import kin.sdk.core.ResultCallback;


public class CountDownActivity extends AppCompatActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, CountDownActivity.class);
    }

    private KinClient kinClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown_activity);

        CountDownView countDownView = findViewById(R.id.countdown_view);
        countDownView.setListener(this::moveToQuestion);
        countDownView.startCount(getCountDownTime());

        kinClient = ((App)getApplication()).getKinClient();

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
        return 5000;
    }

    private long getCountDownTime() {
        long time = FBDatabase.getInstance().nextQuestion.getTimeStamp();
        long currentTime = System.currentTimeMillis();
        return time - currentTime;
    }

    private void moveToQuestion() {
        startActivity(QuestionActivity.getIntent(this));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    public void openAccountInfo(View view) {
        //TODO start openAccountInfo
    }

    public void openTutorial(View view) {
        startActivity(KinTutorial.getIntent(this));
        finish();
    }
}
