package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;


public class CountDownActivity extends AppCompatActivity {

    CountDownView countDownView;

    public static Intent getIntent(Context context) {
        return new Intent(context, CountDownActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.countdown_activity);

        countDownView = findViewById(R.id.countdown_view);
        countDownView.setListener(new CountDownView.ICountDownListener() {
            @Override
            public void onComplete() {
                moveToQuestion();
            }
        });
        countDownView.startCountSec(259215);
    }

    private void moveToQuestion() {
        startActivity(QuestionActivity.getIntent(this));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
