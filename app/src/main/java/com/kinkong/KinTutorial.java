package com.kinkong;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.File;

public class KinTutorial extends BaseVideoActivity {

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
        return mp -> startActivity(CountDownActivity.getIntent(this));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_video_base);
        super.onCreate(savedInstanceState);
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
}
