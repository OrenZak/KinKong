package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import java.io.File;

public class KinTutorial extends BaseVideoActivity {

    private static final String IS_FROM_SPLASH = "is_from_splash";
    private boolean isFromSplash = false;

    public static Intent getIntent(Context context, boolean  isFromSplash){
        return  new Intent(context, KinTutorial.class).putExtra(IS_FROM_SPLASH, isFromSplash);
    }

    @Override
    String getVideoURL() {
        return getFilesDir() + File.separator + "kin_tutorial.mp4";
    }

    @Override
    boolean isLocal() {
        return true;
    }

    @Override
    MediaPlayer.OnCompletionListener getCompletionListener() {
        return mp -> startCountDown();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_video_base);
        findViewById(R.id.skip).setVisibility(View.VISIBLE);
        super.onCreate(savedInstanceState);
        isFromSplash = getIntent().getBooleanExtra(IS_FROM_SPLASH, false);
        findViewById(R.id.skip).setOnClickListener(v -> startCountDown());
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

    private void startCountDown() {
        Intent countDownIntent = CountDownActivity.getIntent(this);
        if (startScreen(countDownIntent) && isFromSplash) {
            releaseMediaPlayer();
            finish();
        }
    }
}
