package com.kinkong;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.kinkong.database.FBDatabase;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import kin.sdk.core.KinClient;
import kin.sdk.core.exception.CreateAccountException;

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
        createAccount();
        try {
            downloadTutorial();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        //TODO add show once
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashActivity.this, AccountInfoActivity.class);
            startActivity(intent);
        }, 500);

//        if(kinTutorialFile.exists()){
//            new Handler().postDelayed(() -> moveToTutorial(), 1000);
//
//        }
//        else {
//            outStream = new FileOutputStream(kinTutorialFile);
//            storageReference.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
//                @Override
//                public void onSuccess(byte[] bytes) {
//                    try {
//                        outStream.write(bytes);
//                        outStream.close();
//                        moveToTutorial();
//                    } catch (IOException ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            });
//        }
    }

    private void moveToTutorial() {
        startActivity(KinTutorial.getIntent(this));
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storageReference = null;
    }
}
