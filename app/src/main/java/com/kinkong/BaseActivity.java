package com.kinkong;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

public class BaseActivity extends AppCompatActivity {

    protected ImageView backButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        backButton = findViewById(R.id.back_button);
        if(backButton != null) {
            backButton.setOnClickListener(v -> onBackPressed());
        }
    }
}
