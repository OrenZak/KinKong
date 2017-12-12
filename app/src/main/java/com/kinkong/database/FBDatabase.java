package com.kinkong.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.kinkong.database.data.Question;

public class FBDatabase {
    private static final FBDatabase ourInstance = new FBDatabase();

    private  FirebaseDatabase database = FirebaseDatabase.getInstance();

    public int nextQuestionNum;
    public Question nextQuestion;

    public static FBDatabase getInstance() {
        return ourInstance;
    }

    private FBDatabase() {}

    public void cacheBasicData() {
        getNextQuestion(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    nextQuestionNum = dataSnapshot.getValue(Integer.class);
                    cacheQuestionAt(nextQuestionNum);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void cacheQuestionAt(int index) {
        getQuestionAt(index, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() != null) {
                    nextQuestion = dataSnapshot.getValue(Question.class);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getNextQuestion(ValueEventListener valueEventListener) {
        database.getReference("next_question").addListenerForSingleValueEvent(valueEventListener);
    }

    public void getQuestionAt(int index, ValueEventListener valueEventListener) {
        database.getReference("questions").child(index + "").addListenerForSingleValueEvent(valueEventListener);
    }

    public void setWinner(int correctAnswer ,String publicAddress) {
        database.getReference("questions").child(nextQuestionNum + "").child("winners").push().setValue(publicAddress);

        DatabaseReference answerCount = database.getReference("questions/" + nextQuestionNum + "/answers_count/" + correctAnswer);
        upCount(answerCount);
    }

    private void upCount(DatabaseReference postRef) {
        postRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if(mutableData.getValue() == null) {
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
