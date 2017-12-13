package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.kinkong.database.FBDatabase;
import com.kinkong.database.data.Question;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    private Question question;

    public static Intent getIntent(Context context) {
        return new Intent(context, QuestionActivity.class);
    }

    List<AnswerView> answers = new ArrayList<>(3);
    View.OnClickListener clickListener = view -> {
        AnswerView answerView = (AnswerView) view;
        answerView.setSelected(true);
        disableClicks();
        Object tag = view.getTag();
        sendAnswer(Integer.parseInt((String)tag));

        //TODO wait for timer
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVotings();
            }
        }, 3000);
    };

    private void sendAnswer(int answerIndex) {
        if(answerIndex == question.correct_answer){
            FBDatabase.getInstance().setWinner(getPublicAddress());
        } else{
            FBDatabase.getInstance().setAnswer(answerIndex);
        }
    }

    //TODO
    private String getPublicAddress() {
        return "TODO";
    }

    private void setVotings() {
        answers.get(0).setVotingRatio(0f);
        answers.get(1).setVotingRatio(0.4f);
        answers.get(2).setVotingRatio(1);
    }

    private void disableClicks() {
        answers.get(0).setOnClickListener(null);
        answers.get(1).setOnClickListener(null);
        answers.get(2).setOnClickListener(null);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);
        answers.add(findViewById(R.id.answer0));
        answers.add(findViewById(R.id.answer1));
        answers.add(findViewById(R.id.answer2));

        question = FBDatabase.getInstance().nextQuestion;
        initAnswers();
    }

    private void initAnswers() {
        answers.get(0).setAnswer("A." + " answer is ABBA");
        answers.get(1).setAnswer("B." + " answer is baby");
        answers.get(2).setAnswer("C." + " answer is cat");

        answers.get(0).setOnClickListener(clickListener);
        answers.get(1).setOnClickListener(clickListener);
        answers.get(2).setOnClickListener(clickListener);
    }


}
