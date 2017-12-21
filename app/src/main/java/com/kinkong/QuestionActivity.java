package com.kinkong;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.kinkong.analytics.FBAnalytics;
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
    private static final long WAIT_TIME_BETWEEN_QUESTIONS = 10000;

    private Question question;
    private KinAccount account;
    private MediaPlayer mediaPlayer;
    private int answerIndex = UNSELECTED_INDEX;
    private View root;

    private List<AnswerView> answerViewList = new ArrayList<>(4);
    private boolean isWinner;
    private boolean shouldMoveToScreen;
    private boolean shouldMoveToNextSession;


    private View.OnClickListener answerClickListener = view -> {
        AnswerView answerView = (AnswerView) view;
        answerView.setSelected(true);
        disableClicks();
        answerIndex = Integer.parseInt((String) view.getTag());
        updateSelection(answerIndex);
        isWinner = answerIndex == question.getCorrectAnswer();
        sendAnswer();
        FBAnalytics.getInstance().answeredTapped(view.getContext(), isWinner, answerIndex);
    };

    public static Intent getIntent(Context context) {
        return new Intent(context, QuestionActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.question_activity);
        root = findViewById(R.id.root);
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

    @Override
    protected void onResume() {
        super.onResume();
        if (shouldMoveToScreen) {
            //TODO
        }
    }

    public void closeScreen(long waitTime) {
        shouldMoveToScreen = true;
        root.postDelayed(() -> {
            if (shouldMoveToNextSession) {
                startNextSessionCountDown();
            } else {
                startNextQuestionVideo();
            }
        }, waitTime);
    }

    private void startNextQuestionVideo() {
        startScreen(QuestionVideoActivity.getIntent(this));
        finish();
    }

    private void startNextSessionCountDown() {
        startScreen(CountDownActivity.getIntent(this));
        finish();
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
        if (isWinner) {
            FBDatabase.getInstance().setWinner(getPublicAddress());
        }
        if (answerIndex != UNSELECTED_INDEX) {
            FBDatabase.getInstance().setAnswer(answerIndex);
        }
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
        FBDatabase.getInstance().getAnswersCount(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    List<Long> answers_count = (List<Long>) dataSnapshot.getValue();
                    float sum = 0;
                    for (int i = 0; i < answers_count.size(); i++) {
                        sum += answers_count.get(i);
                    }
                    for (int i = 0; i < answers_count.size(); i++) {
                        if (i < answerViewList.size()) {
                            float ratio = (float) (answers_count.get(i)) / sum;
                            answerViewList.get(i).animateVoting(ratio);
                        }
                    }
                } catch (Exception e) {
                    Log.d("animateVoting", "onDataChange: animateVoting Exception");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("animateVoting", "animateVoting onCancelled: ");
            }
        });
    }

    private void disableClicks() {
        for (AnswerView answerView : answerViewList) {
            answerView.setOnClickListener(null);
        }
    }

    private void startCountDown() {
        ClockCountDownView countDownView = findViewById(R.id.clock_count_down);
        countDownView.setListener(this::onCountDownComplete);
        countDownView.startCount(DURATION_SECONDS * 1000);
    }

    private void onCountDownComplete() {
        disableClicks();
        deselectAnswers();
        updateSelection(question.getCorrectAnswer());
        updateAnswersColors();
        updateLayout();
        animateVoting();
        if (isWinner && FBDatabase.getInstance().shouldSeenNextQuestion()) {
            FBDatabase.getInstance().updateNextQuestion();
        } else {
            FBDatabase.getInstance().updateNextQuestionSession();
            shouldMoveToNextSession = true;
        }
        closeScreen(WAIT_TIME_BETWEEN_QUESTIONS);
    }

    private void updateLayout() {
        if (isWinner) {
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
        ((TextView) findViewById(R.id.question)).setText(question.getQuestion());

        List<String> answersStrArray = question.getAnswers();
        for (int i = 0; i < answerViewList.size(); i++) {
            AnswerView answerView = answerViewList.get(i);
            answerView.setAnswer(answersStrArray.get(i));
            answerView.setOnClickListener(answerClickListener);
        }
    }
}
