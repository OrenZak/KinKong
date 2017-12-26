package com.kinkong;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;

public class WhatsKinDialog extends Dialog {

    private final static String ECO_SYS_KIN = "http://kinecosystem.org/";

    public WhatsKinDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialogStyle);
        setContentView(R.layout.dialog_whats_kin);
        findViewById(R.id.close_button).setOnClickListener(v -> dismiss());

        findViewById(R.id.description).setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(ECO_SYS_KIN));
            context.startActivity(browserIntent);
        });
    }
}
