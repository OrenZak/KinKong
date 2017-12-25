package com.kinkong;

import android.graphics.Matrix;
import android.graphics.SurfaceTexture;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;

abstract class BaseVideoActivity extends BaseActivity implements TextureView.SurfaceTextureListener {

    protected TextureView textureView;
    private MediaPlayer mediaPlayer;

    abstract String getVideoURL();

    abstract boolean isLocal();

    abstract MediaPlayer.OnCompletionListener getCompletionListener();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
    }

    private void init() {
        textureView = findViewById(R.id.texture_view);
        textureView.setAlpha(0);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
        initMediaPlayer(surfaceTexture);
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        releaseMediaPlayer();
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
    }

    protected void prepareMediaPlayer() {
        if (textureView == null) {
            init();
        } else {
            if (textureView.isAvailable()) {
                initMediaPlayer(textureView.getSurfaceTexture());
                return;
            }
        }
        textureView.setSurfaceTextureListener(this);
    }

    private void initMediaPlayer(SurfaceTexture surfaceTexture) {
        try {
            Surface surface = new Surface(surfaceTexture);
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            }
            if (isLocal()) {
                mediaPlayer.setDataSource(getApplicationContext(), Uri.parse(getVideoURL()));
            } else {
                mediaPlayer.setDataSource(getVideoURL());
                mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                        .setLegacyStreamType(AudioManager.STREAM_MUSIC)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MOVIE)
                        .build());
            }
            mediaPlayer.setSurface(surface);
            mediaPlayer.setLooping(false);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnCompletionListener(getCompletionListener());

            // Play video when the media source is ready for playback.
            mediaPlayer.setOnPreparedListener(mediaPlayer -> {
                adjustAspectRatio(mediaPlayer.getVideoWidth(), mediaPlayer.getVideoHeight());
                textureView.animate().alpha(1).setDuration(500).start();
                mediaPlayer.start();
            });

        } catch (Exception e) {
            Log.e("BASE VIDEO ACTIVITY", e.getMessage());
            releaseMediaPlayer();
        }
    }

    /**
     * Release media player, after use
     */
    protected void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    /**
     * Sets the TextureView transform to preserve the aspect ratio of the video.
     */
    private void adjustAspectRatio(int videoWidth, int videoHeight) {
        int viewWidth = textureView.getWidth();
        int viewHeight = textureView.getHeight();
        double aspectRatio = (double) videoHeight / videoWidth;

        int newWidth;

        // limited by short height; restrict width
        newWidth = (int) (viewHeight / aspectRatio);

        int xoff = (viewWidth - newWidth) / 2;

        Matrix txform = new Matrix();
        textureView.getTransform(txform);
        txform.setScale((float) newWidth / viewWidth, 1f);

        txform.postTranslate(xoff, 0);
        textureView.setTransform(txform);
    }
}
