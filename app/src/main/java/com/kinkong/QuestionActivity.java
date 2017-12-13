package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.kinkong.database.FBDatabase;
import com.kinkong.database.data.Question;

import java.util.ArrayList;
import java.util.List;

import kin.sdk.core.KinAccount;

public class QuestionActivity extends AppCompatActivity {

    private Question question;
    private KinAccount account;
    private static final int DURATION_SECONDS = 10;
    private int answerIndex = -1;

    public static Intent getIntent(Context context) {
        return new Intent(context, QuestionActivity.class);
    }

    List<AnswerView> answers = new ArrayList<>(4);
    View.OnClickListener clickListener = view -> {
        AnswerView answerView = (AnswerView) view;
        answerView.setSelected(true);
        disableClicks();
        answerIndex = Integer.parseInt((String) view.getTag());
        sendAnswer();
    };

    private void sendAnswer() {
        if (isWinner()) {
            FBDatabase.getInstance().setWinner(getPublicAddress());
        } else {
            FBDatabase.getInstance().setAnswer(answerIndex);
        }
    }

    private boolean isWinner(){
        return answerIndex == question.correct_answer;
    }

    private void animateClose() {
        View view = findViewById(R.id.close_button);
        view.setVisibility(View.VISIBLE);
    }

    private void updateLoser() {
        findViewById(R.id.question_layout).setVisibility(View.GONE);
        findViewById(R.id.loser_layout).setVisibility(View.VISIBLE);
    }

    private void updateWinner() {
        findViewById(R.id.question_layout).setVisibility(View.GONE);
        findViewById(R.id.winner_layout).setVisibility(View.VISIBLE);
    }


    private String getPublicAddress() {
        return account.getPublicAddress();
    }

    private void setVotings() {
        List<Long> answers_count = question.answers_count;
        float sum = 0;
        for (Long count : answers_count) {
            sum += count;
        }
        for (int i = 0; i < answers_count.size(); i++) {
            float ratio = (float) (answers_count.get(i)) / sum;
            answers.get(i).setVotingRatio(ratio);
        }
    }

    private void disableClicks() {
        for (AnswerView answerView : answers) {
            answerView.setOnClickListener(null);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);
        answers.add(findViewById(R.id.answer0));
        answers.add(findViewById(R.id.answer1));
        answers.add(findViewById(R.id.answer2));
        answers.add(findViewById(R.id.answer3));

        account = ((App) getApplication()).getKinClient().getAccount();
        question = FBDatabase.getInstance().nextQuestion;
        initViews();
        startCountDown();
    }

    private void startCountDown() {
        CountDownShortView countDownShortView = findViewById(R.id.counter);
        countDownShortView.setListener(() -> {
            disableClicks();
            animateClose();
            if(isWinner()){
                updateWinner();
            }else{
                updateLoser();
            }
            countDownShortView.postDelayed(() -> setVotings(), 5000);
        });
        countDownShortView.startCount(DURATION_SECONDS * 1000);
    }

    private void initViews() {
        TextView questionTextView = findViewById(R.id.question);
        questionTextView.setText(question.getQuestion());

        List<String> answersStrArray = question.answers;
        for (int i = 0; i < answers.size(); i++) {
            AnswerView answerView = answers.get(i);
            answerView.setAnswer(answersStrArray.get(i));
            answerView.setOnClickListener(clickListener);
        }
    }


    public void closeQuestion(View view) {
        startActivity(CountDownActivity.getIntent(this));
        finish();
    }
}
