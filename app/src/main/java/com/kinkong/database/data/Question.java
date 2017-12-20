package com.kinkong.database.data;

import java.util.List;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class Question {

    public String video_url;
    public String question;
    public long time_stamp;
    public int correct_answer;
    public List<Long> answers_count;
    public List<String> answers;
    public int prize;

    public Question() {
    }

    public int getPrize() {
        return prize;
    }

    public String getVideoUrl() {
        return video_url;
    }

    public String getQuestion() {
        return question;
    }

    public long getTimeStamp() {
        return time_stamp;
    }

    public List<Long> getAnswersCount() {
        return answers_count;
    }

    public List<String> getAnswers() {
        return answers;
    }

    public int getCorrectAnswer() {
        return correct_answer;
    }

    @Override
    public String toString() {
        return "Question: " + question + " TimeStamp: " + time_stamp + " Answers: " + answers.toString();
    }
}