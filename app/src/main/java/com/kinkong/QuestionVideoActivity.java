package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kinkong.database.FBDatabase;

public class QuestionVideoActivity extends BaseVideoActivity {


    public static Intent getIntent(Context context) {
        return new Intent(context, QuestionVideoActivity.class);
    }

    @Override
    String getVideoURL() {
        return FBDatabase.getInstance().nextQuestion.getVideoUrl();
    }

    @Override
    boolean isLocal() {
        return false;
    }

    @Override
    MediaPlayer.OnCompletionListener getCompletionListener() {
        return mp -> startQuestion();
    }

    private void startQuestion() {
        Intent questionIntent = QuestionActivity.getIntent(this);
        if (startScreen(questionIntent)) {
            releaseMediaPlayer();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        prepareMediaPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_video_base);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        // no back
    }
}
