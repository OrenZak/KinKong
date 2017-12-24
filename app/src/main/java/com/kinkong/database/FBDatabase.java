package com.kinkong.database;

import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.kinkong.database.data.Question;

public class FBDatabase {
    public static final int MAX_QUESTIONS_IN_RAW = 3;
    private static final FBDatabase ourInstance = new FBDatabase();

    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    public int nextQuestionIndex, nextQuestionIndexOrigin;
    public Question nextQuestion;

    public static FBDatabase getInstance() {
        return ourInstance;
    }

    private FBDatabase() {
    }

    private String getUserUid() {
        String userUid = "";
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (firebaseUser != null) {
            userUid = firebaseUser.getUid();
        }
        return userUid;
    }

    public void cacheBasicData(final ValueEventListener valueEventListener) {
        getNextQuestion(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    nextQuestionIndex = dataSnapshot.getValue(Integer.class);
                    nextQuestionIndexOrigin = nextQuestionIndex;
                    getQuestionAt(nextQuestionIndex, valueEventListener);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void setNextQuestion(Question question) {
        this.nextQuestion = question;
    }

    public void getNextQuestion(ValueEventListener valueEventListener) {
        database.getReference("next_question").addListenerForSingleValueEvent(valueEventListener);
    }

    public void getQuestionAt(int index, ValueEventListener valueEventListener) {
        database.getReference("questions").child(index + "").addListenerForSingleValueEvent(valueEventListener);
    }

    public void setWinner(String publicAddress) {
        String userUid = getUserUid();
        if (!TextUtils.isEmpty(userUid)) {
            database.getReference("answers").child(nextQuestionIndex + "").child("winners").child(userUid).setValue(publicAddress);
        }
    }

    public void setAnswer(int answerIndex) {
        DatabaseReference answerCount = database.getReference("answers/" + nextQuestionIndex + "/answers_count/" + answerIndex);
        upCount(answerCount);
    }

    public void getAnswersCount(ValueEventListener valueEventListener) {
        database.getReference("answers/" + nextQuestionIndex + "/answers_count").addListenerForSingleValueEvent(valueEventListener);
    }

    public boolean upateNextQuestion(boolean isWinner) {
        boolean hasNextQuestion = false;
        if (isWinner && shouldSeeNextQuestion()) {
            ++nextQuestionIndex;
            hasNextQuestion = true;
        } else {
            nextQuestionIndex = nextQuestionIndexOrigin + MAX_QUESTIONS_IN_RAW;
        }
        getQuestionAt(nextQuestionIndex, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                setNextQuestion(dataSnapshot.getValue(Question.class));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        return hasNextQuestion;
    }

    private boolean shouldSeeNextQuestion() {
        return (nextQuestionIndex + 1) - nextQuestionIndexOrigin < MAX_QUESTIONS_IN_RAW;
    }

    private void upCount(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    mutableData.setValue(new Integer(1));
                } else {
                    int count = mutableData.getValue(Integer.class);
                    mutableData.setValue(++count);

                }
                return Transaction.success(mutableData);
            }

            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }
}
