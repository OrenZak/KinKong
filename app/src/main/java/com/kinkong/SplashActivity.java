package com.kinkong;

import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import com.kinkong.database.FBDatabase;
import com.kinkong.database.data.Question;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import kin.sdk.core.KinClient;
import kin.sdk.core.exception.CreateAccountException;

public class SplashActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private File kinTutorialFile;
    private OutputStream outStream;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences(Constants.MAIN_SHAEREDPREF, MODE_PRIVATE);
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://kinkong-977fc.appspot.com").child("test_monky.mp4");
        kinTutorialFile = new File(getFilesDir() + File.separator + "kin_tutorial.mp4");
        createAccount();
        FBDatabase.getInstance().cacheBasicData(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FBDatabase.getInstance().nextQuestion = dataSnapshot.getValue(Question.class);
                if(isFirsTime()) {
                    try {
                        downloadTutorial();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    moveToCountDown();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void createAccount() {
        try {
            KinClient kinClient = ((App)getApplication()).getKinClient();
            kinClient.createAccount(App.PASSPHRASE);
        } catch (CreateAccountException e) {
            e.printStackTrace();
        }
    }

    private void downloadTutorial() throws IOException {
        sharedPreferences.edit().putBoolean(Constants.FIRST_TIME,false).apply();
        if(kinTutorialFile.exists()){
            new Handler().postDelayed(() -> moveToTutorial(), 1000);

        }
        else {
            outStream = new FileOutputStream(kinTutorialFile);
            storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(bytes -> {
                try {
                    outStream.write(bytes);
                    outStream.close();
                    moveToTutorial();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            });
        }
    }

    private void moveToTutorial() {
        startActivity(KinTutorial.getIntent(this));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    private void moveToCountDown() {
        new Handler().postDelayed(() -> {
            startActivity(CountDownActivity.getIntent(SplashActivity.this));
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }, 1000);

    }

    private boolean isFirsTime() {
        return sharedPreferences.getBoolean(Constants.FIRST_TIME, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storageReference = null;
        sharedPreferences = null;
    }
}
