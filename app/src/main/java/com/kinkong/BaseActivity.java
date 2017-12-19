package com.kinkong;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class BaseActivity extends AppCompatActivity {

    protected ImageView backButton;

    protected boolean isVisible = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backButton = findViewById(R.id.back_button);
        if (backButton != null) {
            backButton.setOnClickListener(v -> onBackPressed());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        isVisible = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isVisible = false;
    }

    protected boolean startScreen(Intent moveToIntent) {
        if (isVisible) {
            startActivity(moveToIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            return true;
        }
        return false;
    }

    protected String getPassPhrase() {
        return getApp().getPassphrase();
    }

    protected App getApp(){
        return (App) getApplication();
    }
}
