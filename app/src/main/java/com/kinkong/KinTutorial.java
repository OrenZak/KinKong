package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.File;

public class KinTutorial extends BaseVideoActivity {

    public static Intent getIntent(Context context){
        return  new Intent(context, KinTutorial.class);
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
        return mp -> moveToCountDown();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_video_base);
        super.onCreate(savedInstanceState);
        findViewById(R.id.skip).setOnClickListener(v -> moveToCountDown());
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

    private void moveToCountDown() {
        startActivity(CountDownActivity.getIntent(this));
    }
}
