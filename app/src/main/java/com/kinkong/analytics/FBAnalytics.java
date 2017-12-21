package com.kinkong.analytics;

import android.content.Context;
import android.os.Bundle;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;

public class FBAnalytics {
    private static final FBAnalytics ourInstance = new FBAnalytics();

    public static FBAnalytics getInstance() {
        return ourInstance;
    }

    private FBAnalytics() {
    }


    public void putUserData(Context ctx) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(ctx);
        analytics.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
    }

    public void appOpened(Context ctx) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(ctx);
        analytics.logEvent(AnalyticsConst.APP_OPENED, new Bundle());
    }

    public void answeredTapped(Context ctx, boolean isCorrect, int answerIndex) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(ctx);
        Bundle data = new Bundle();
        data.putBoolean(AnalyticsConst.IS_CORRECT, isCorrect);
        data.putInt(AnalyticsConst.ANSWER_INDEX, answerIndex);
        analytics.logEvent(AnalyticsConst.ANSWER_TAPPED, data);
    }

    public void myBalanceTapped(Context ctx, String kinAmount) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(ctx);
        Bundle data = new Bundle();
        data.putString(AnalyticsConst.KIN_AMOUNT, kinAmount);
        analytics.logEvent(AnalyticsConst.MY_BALANCE_TAPPED, data);
    }

    public void publicAddressCopyTapped(Context ctx) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(ctx);
        analytics.logEvent(AnalyticsConst.PUBLIC_ADD_COPY_TAPPED, new Bundle());
    }

    public void accountBackupDoneTapped(Context ctx) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(ctx);
        analytics.logEvent(AnalyticsConst.ACCOUNT_BACKUP_DONE_TAPPED, new Bundle());
    }

    public void exportKeyCopyTapped(Context ctx) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(ctx);
        analytics.logEvent(AnalyticsConst.EXPORT_KEY_COPY_TAPPED, new Bundle());
    }

    public void whatsKinTapped(Context ctx) {
        FirebaseAnalytics analytics = FirebaseAnalytics.getInstance(ctx);
        analytics.logEvent(AnalyticsConst.WHATS_KIN_TAPPED, new Bundle());
    }
}
