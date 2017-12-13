package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class QuestionActivity extends AppCompatActivity {

    public static Intent getIntent(Context context) {
        return new Intent(context, QuestionActivity.class);
    }

    List<AnswerView> answers = new ArrayList<>(3);
    View.OnClickListener clickListener = view -> {
        AnswerView answerView = (AnswerView) view;
        answerView.setSelected(true);
        Object tag = view.getTag();
        disableClicks();
        sendAnswer((String) view.getTag());
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                setVotings();
            }
        }, 3000);
    };

    private void sendAnswer(String answer) {
        //TODO
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
