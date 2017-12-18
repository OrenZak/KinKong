package com.kinkong;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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

import kin.sdk.core.exception.CreateAccountException;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();
    private StorageReference storageReference;
    private File kinTutorialFile;
    private OutputStream outStream;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        storageReference = FirebaseStorage.getInstance().getReferenceFromUrl("gs://kinkong-977fc.appspot.com").child("kin_tutorial2.mp4");
        kinTutorialFile = new File(getFilesDir() + File.separator + "kin_tutorial.mp4");
        try {
            ((App) getApplication()).createAccount(this);
        } catch (CreateAccountException e) {
            e.printStackTrace();
            finish();
        }
        firebaseAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        signInAnonymously();
    }

    private void signInAnonymously() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "already signInAnonymously");
            getFireBaseBasicData();
        } else {
            firebaseAuth.signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "signInAnonymously:success");
                                getFireBaseBasicData();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "signInAnonymously:failure", task.getException());
                                Toast.makeText(SplashActivity.this, "Authentication failed. close and reopen app",
                                        Toast.LENGTH_LONG).show();
                                Toast.makeText(SplashActivity.this, "Authentication failed. close and reopen app",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }

    private void getFireBaseBasicData() {
        FBDatabase.getInstance().cacheBasicData(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                FBDatabase.getInstance().setNextQuestion(dataSnapshot.getValue(Question.class));
                if (!hasSeenTutorial()) {
                    try {
                        downloadTutorial();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    moveToCountDown();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void downloadTutorial() throws IOException {
        if (kinTutorialFile.exists()) {
            new Handler().postDelayed(this::moveToTutorial, 1000);

        } else {
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
        startActivity(KinTutorial.getIntent(this, true));
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

    private boolean hasSeenTutorial() {
        App app = (App) getApplication();
        return app.hasSeenTutorial();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storageReference = null;
    }

}
