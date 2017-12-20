package com.kinkong;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import kin.sdk.core.KinClient;
import kin.sdk.core.exception.OperationFailedException;
import kin.sdk.core.exception.PassphraseException;

public class AccountInfoActivity extends BaseActivity {
    private TextView privateKeyTitle;
    private TextView copyPrivate;
    private TextView privateKeyText;
    private KinClient kinClient;

    public static Intent getIntent(Context context) {
        return new Intent(context, AccountInfoActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setContentView(R.layout.activity_account_info);
        super.onCreate(savedInstanceState);
        kinClient = getApp().getKinClient();
        init();
    }

    private void init() {
        TextView publicAddressText = findViewById(R.id.public_address);
        TextView copyPublic = findViewById(R.id.copy_public);

        EditText backupEditText = findViewById(R.id.backup_passphrase);
        TextView doneButton = findViewById(R.id.done);

        privateKeyTitle = findViewById(R.id.private_key_title);
        copyPrivate = findViewById(R.id.copy_private);
        privateKeyText = findViewById(R.id.private_key);
        findViewById(R.id.whats_kin).setOnClickListener(v -> new WhatsKinDialog(this).show());

        privateKeyText.setMovementMethod(new ScrollingMovementMethod());
        String address = kinClient.getAccount().getPublicAddress();
        publicAddressText.setText(address);


        doneButton.setOnClickListener(v -> {
            String backupPassphrase = backupEditText.getText().toString();
            String privateKey = exportPrivateKey(backupPassphrase);
            showPrivateKey(privateKey);
            hideKeyboard(backupEditText);
            backupEditText.setText("");
        });

        copyPublic.setOnClickListener(v -> {
            String publicAddress = publicAddressText.getText().toString();
            copyToClipboard(publicAddress);
        });

        copyPrivate.setOnClickListener(v -> {
            String privateKey = privateKeyText.getText().toString();
            copyToClipboard(privateKey);
        });

        backupEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                doneButton.setEnabled(count > 0);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }


    private void copyToClipboard(CharSequence textToCopy) {
        int sdk = android.os.Build.VERSION.SDK_INT;
        if (sdk < android.os.Build.VERSION_CODES.HONEYCOMB) {
            android.text.ClipboardManager clipboard = (android.text.ClipboardManager) getSystemService(
                    Context.CLIPBOARD_SERVICE);
            clipboard.setText(textToCopy);
        } else {
            android.content.ClipboardManager clipboard = (android.content.ClipboardManager) getSystemService(
                    Context.CLIPBOARD_SERVICE);
            android.content.ClipData clip = android.content.ClipData.newPlainText("copied text", textToCopy);
            clipboard.setPrimaryClip(clip);
        }
        Toast.makeText(this, "Copied to your clipboard", Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private String exportPrivateKey(String passphrase) {
        String exportedKey = null;
        try {
            exportedKey = kinClient.getAccount().exportKeyStore(getPassPhrase(), passphrase);
        } catch (PassphraseException | OperationFailedException e) {
            e.printStackTrace();
        }

        return exportedKey;
    }

    private void showPrivateKey(String privateKey) {
        try {
            JSONObject jsonObject = new JSONObject(privateKey);
            privateKeyText.setText(jsonObject.toString());

            privateKeyTitle.setVisibility(View.VISIBLE);
            copyPrivate.setVisibility(View.VISIBLE);
            privateKeyText.setVisibility(View.VISIBLE);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
