package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.kinkong.database.FBDatabase;
import com.kinkong.database.data.Question;

import java.util.ArrayList;
import java.util.List;

import kin.sdk.core.KinAccount;

public class QuestionActivity extends BaseActivity {

    private static final int DURATION_SECONDS = 10;
    private static final int UNSELECTED_INDEX = -1;

    private static final float UNSELECTED_ALPHA = 0.7f;
    private static final float SELECTED_ALPHA = 1f;

    private Question question;
    private KinAccount account;
    private MediaPlayer mediaPlayer;

    private int answerIndex = UNSELECTED_INDEX;

    private List<AnswerView> answerViewList = new ArrayList<>(4);
    private View.OnClickListener answerClickListener = view -> {
        AnswerView answerView = (AnswerView) view;
        answerView.setSelected(true);
        disableClicks();
        answerIndex = Integer.parseInt((String) view.getTag());
        updateSelection(answerIndex);
        sendAnswer();
    };

    public static Intent getIntent(Context context) {
        return new Intent(context, QuestionActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);
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

    public void closeQuestion(View view) {
        Intent countDownIntent = CountDownActivity.getIntent(this);
        if (startScreen(countDownIntent)) {
            finish();
        }
    }

    private void updateSelection(int index) {
        for (int i = 0; i < answerViewList.size(); i++) {
            if (i != index) {
                answerViewList.get(i).setAlpha(UNSELECTED_ALPHA);
            } else {
                answerViewList.get(i).setAlpha(SELECTED_ALPHA);
            }
        }
    }

    private void sendAnswer() {
        if (isWinner()) {
            FBDatabase.getInstance().setWinner(getPublicAddress());
        } else {
            if (answerIndex != UNSELECTED_INDEX) {
                FBDatabase.getInstance().setAnswer(answerIndex);
            }
        }
    }

    private boolean isWinner() {
        return answerIndex == question.getCorrectAnswer();
    }

    private void animateClose() {
        View view = findViewById(R.id.close_button);
        view.setAlpha(0);
        view.setVisibility(View.VISIBLE);
        view.animate().alpha(1).setDuration(1000).rotation(360).setStartDelay(3000).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    private void updateLoserLayout() {
        findViewById(R.id.question_layout).setVisibility(View.GONE);
        View view = findViewById(R.id.try_next_time_text);
        view.setAlpha(0);
        findViewById(R.id.loser_layout).setVisibility(View.VISIBLE);
        playAudio(false);
        view.animate().alpha(1).setDuration(1000).setStartDelay(1000).start();
    }

    private void deselectAnswers() {
        for (AnswerView answerView : answerViewList) {
            answerView.setSelected(false);
        }
    }

    private void updateWinnerLayout() {
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

    private void animateVoting() {
        List<Long> answers_count = question.getAnswersCount();
        float sum = 0;
        for (Long count : answers_count) {
            sum += count;
        }
        for (int i = 0; i < answers_count.size(); i++) {
            float ratio = (float) (answers_count.get(i)) / sum;
            answerViewList.get(i).animateVoting(ratio);
        }
    }

    private void disableClicks() {
        for (AnswerView answerView : answerViewList) {
            answerView.setOnClickListener(null);
        }
    }

    private void startCountDown() {
        CountDownShortView countDownShortView = findViewById(R.id.counter);
        countDownShortView.setListener(this::onCountDownComplete);
        countDownShortView.startCount(DURATION_SECONDS * 1000);
    }

    private void onCountDownComplete() {
        disableClicks();
        deselectAnswers();
        updateSelection(question.getCorrectAnswer());
        animateClose();
        updateAnswersColors();
        updateLayout();
        FBDatabase.getInstance().updateNextQuestion();
        animateVoting();
    }

    private void updateLayout() {
        if (isWinner()) {
            updateWinnerLayout();
        } else {
            updateLoserLayout();
        }
    }

    private void updateAnswersColors() {
        for (int i = 0; i < answerViewList.size(); i++) {
            if (i == question.getCorrectAnswer()) {
                answerViewList.get(i).markCorrect();
            } else {
                answerViewList.get(i).markRatio();
            }
        }
    }

    private void initViews() {
        answerViewList.add(findViewById(R.id.answer0));
        answerViewList.add(findViewById(R.id.answer1));
        answerViewList.add(findViewById(R.id.answer2));
        answerViewList.add(findViewById(R.id.answer3));
        ((TextView)findViewById(R.id.question)).setText(question.getQuestion());

        List<String> answersStrArray = question.getAnswers();
        for (int i = 0; i < answerViewList.size(); i++) {
            AnswerView answerView = answerViewList.get(i);
            answerView.setAnswer(answersStrArray.get(i));
            answerView.setOnClickListener(answerClickListener);
        }
    }
}
