package com.kinkong;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kinkong.database.FBDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SplashActivity extends AppCompatActivity {

    private StorageReference storageReference;
    private File kinTutorialFile;
    private OutputStream outStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://kinkong-977fc.appspot.com").child("test_monky.mp4");
        kinTutorialFile = new File(getFilesDir() + File.separator + "kin_tutorial.mp4");
        FBDatabase.getInstance().cacheBasicData();
        try {
            downloadTutorial();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void downloadTutorial() throws IOException {
        //TODO add show once
        if(kinTutorialFile.exists()){
            new Handler().postDelayed(() -> moveToTutorial(), 500);

        }
        else {
            outStream = new FileOutputStream(kinTutorialFile);
            storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    try {
                        outStream.write(bytes);
                        outStream.close();
                        moveToTutorial();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }

    private void moveToTutorial() {
        Intent tutorialIntent = new Intent(SplashActivity.this, KinTutorial.class);
        startActivity(tutorialIntent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storageReference = null;
    }
}
