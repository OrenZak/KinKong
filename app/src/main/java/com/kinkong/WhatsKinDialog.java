package com.kinkong;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

public class WhatsKinDialog extends Dialog {

    public WhatsKinDialog(@NonNull Context context) {
        super(context, R.style.FullScreenDialogStyle);
        setContentView(R.layout.dialog_whats_kin);
        findViewById(R.id.close_button).setOnClickListener(v -> dismiss());
    }
}
