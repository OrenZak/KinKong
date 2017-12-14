package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
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
    private MediaPlayer mediaPlayer;

    public static Intent getIntent(Context context) {
        return new Intent(context, QuestionActivity.class);
    }

    List<AnswerView> answers = new ArrayList<>(4);
    View.OnClickListener clickListener = view -> {
        AnswerView answerView = (AnswerView) view;
        answerView.setSelected(true);
        disableClicks();
        answerIndex = Integer.parseInt((String) view.getTag());
        deselectBeside(answerIndex);
        sendAnswer();
    };

    private void deselectBeside(int index) {
        for (int i = 0; i < answers.size(); i++) {
            if (i != index) {
                answers.get(i).setAlpha(0.7f);
            } else {
                answers.get(i).setAlpha(1f);
            }
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

    @Override
    protected void onStop() {
        super.onStop();
        releaseMediaPlayer();
    }

    private void sendAnswer() {
        if (isWinner()) {
            FBDatabase.getInstance().setWinner(getPublicAddress());
        } else {
            FBDatabase.getInstance().setAnswer(answerIndex);
        }
    }

    private boolean isWinner() {
        return answerIndex == question.correct_answer;
    }

    private void animateClose() {
        View view = findViewById(R.id.close_button);
        view.setAlpha(0);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1).setDuration(1000).rotation(360).setStartDelay(3000).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    private void updateLoser() {
        findViewById(R.id.question_layout).setVisibility(View.GONE);
        View view = findViewById(R.id.try_next_time_text);
        view.setAlpha(0);
        findViewById(R.id.loser_layout).setVisibility(View.VISIBLE);
        playAudio(false);
        view.animate().alpha(1).setDuration(1000).setStartDelay(1000).start();
    }
    
    private void deselectAnswer() {
        for (AnswerView answerView : answers) {
            answerView.setSelected(false);
        }
    }
    
    private void updateWinner() {
        findViewById(R.id.question_layout).setVisibility(View.GONE);
        View bg = findViewById(R.id.winner_bg);
        bg.setAlpha(0);
        bg.setY(bg.getY() - 200);
        findViewById(R.id.winner_layout).setVisibility(View.VISIBLE);
        playAudio(true);
        bg.animate().alpha(1).translationYBy(200).setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(4000).start();
    }

    private void playAudio(boolean isWin) {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), isWin ? R.raw.win : R.raw.fail);
        mediaPlayer.setOnPreparedListener(MediaPlayer::start);
        mediaPlayer.setOnCompletionListener(mp -> releaseMediaPlayer());
    }

    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }


    private String getPublicAddress() {
        return account.getPublicAddress();
    }

    private void animateVotings() {
        List<Long> answers_count = question.answers_count;
        float sum = 0;
        for (Long count : answers_count) {
            sum += count;
        }
        for (int i = 0; i < answers_count.size(); i++) {
            float ratio = (float) (answers_count.get(i)) / sum;
            answers.get(i).animateVoting(ratio);
        }
    }

    private void disableClicks() {
        for (AnswerView answerView : answers) {
            answerView.setOnClickListener(null);
        }
    }

    private void startCountDown() {
        CountDownShortView countDownShortView = findViewById(R.id.counter);
        countDownShortView.setListener(() -> {
            onCountDownComplete();
        });
        countDownShortView.startCount(DURATION_SECONDS * 1000);
    }

    private void onCountDownComplete() {
        disableClicks();
        deselectAnswer();
        deselectBeside(question.correct_answer);
        animateClose();
        markColors();
        if (isWinner()) {
            updateWinner();
        } else {
            updateLoser();
        }
        FBDatabase.getInstance().updateNextQuestion();
        animateVotings();
    }

    private void markColors() {
        for (int i = 0; i < answers.size(); i++) {
            if (i == question.correct_answer) {
                answers.get(i).markCorrect();
            } else {
                answers.get(i).markRatio();
            }
        }
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
