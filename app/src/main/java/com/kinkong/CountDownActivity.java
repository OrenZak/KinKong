package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.kinkong.database.FBDatabase;


public class CountDownActivity extends AppCompatActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, CountDownActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown_activity);

        CountDownView countDownView = findViewById(R.id.countdown_view);
        countDownView.setListener(this::moveToQuestion);
        countDownView.startCount(getCountDownTime());

        updatePrize();
        updateBalance();

    }

    private void updatePrize() {
        TextView prize = findViewById(R.id.prize);
        String prizeStr = getPrize() + " Kin";
        prize.setText(prizeStr);
    }

    private void updateBalance() {
        TextView balance = findViewById(R.id.balance);
        String balanceStr = getKinBalance() + " Kin";
        balance.setText(balanceStr);
    }

    private int getPrize() {
        //TODO
        return 5000;
    }


    private int getKinBalance() {
        //TODO get balance
        return 199;
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
