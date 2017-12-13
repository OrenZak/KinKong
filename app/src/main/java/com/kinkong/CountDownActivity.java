package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;

import com.kinkong.database.FBDatabase;

import java.sql.Timestamp;
import java.time.Clock;


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
    }

    private long getCountDownTime() {
        //TODO check if working
        long time = FBDatabase.getInstance().nextQuestion.getTimeStamp();
        long currentTime = new Timestamp(SystemClock.uptimeMillis()).getTime();
        return time - currentTime;
    }

    private void moveToQuestion() {
        startActivity(QuestionActivity.getIntent(this));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
