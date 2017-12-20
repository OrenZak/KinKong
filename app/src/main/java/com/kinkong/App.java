package com.kinkong;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.UUID;

import kin.sdk.core.KinClient;
import kin.sdk.core.ServiceProvider;
import kin.sdk.core.exception.CreateAccountException;
import kin.sdk.core.exception.EthereumClientException;

public class App extends Application {

    //based on parity
    private final String ROPSTEN_TEST_NET_URL = "http://parity.rounds.video:8545";
    private final String MAIN_NET_URL = "http://mainnet.rounds.video:8545";

    private static final String PASSPHRASE_KEY = "passphraeKey";
    private static final String TUTORIAL_KEY = "tutorialKey";
    private static final String SHARE_PREF = "kingkongSharePref";
    private KinClient kinClient = null;
    private SharedPreferences sharedPreferences;
    private enum NetWorkType {
        MAIN,
        ROPSTEN
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createKinClient(NetWorkType.ROPSTEN);
        sharedPreferences = getSharedPreferences(SHARE_PREF, MODE_PRIVATE);
    }

    public String getPassphrase() {
        return sharedPreferences.getString(PASSPHRASE_KEY, null);
    }

    public void createAccount(Context context) throws CreateAccountException {
        kinClient.createAccount(createPassphrase());
    }

    public void onSeenTutorial() {
        sharedPreferences.edit().putBoolean(TUTORIAL_KEY, true).commit();
    }

    public boolean hasSeenTutorial() {
        return sharedPreferences.getBoolean(TUTORIAL_KEY, false);
    }


    public KinClient getKinClient() {
        return kinClient;
    }

    private KinClient createKinClient(NetWorkType type) {
        String providerUrl;
        int netWorkId;
        switch (type) {
            case MAIN:
                providerUrl = MAIN_NET_URL;
                netWorkId = ServiceProvider.NETWORK_ID_MAIN;
                break;
            case ROPSTEN:
                providerUrl = ROPSTEN_TEST_NET_URL;
                netWorkId = ServiceProvider.NETWORK_ID_ROPSTEN;
                break;
            default:
                providerUrl = ROPSTEN_TEST_NET_URL;
                netWorkId = ServiceProvider.NETWORK_ID_ROPSTEN;
        }
        try {
            kinClient = new KinClient(this,
                    new ServiceProvider(providerUrl, netWorkId));
        } catch (EthereumClientException e) {
            e.printStackTrace();
        }
        return kinClient;
    }

    private String createPassphrase() {
        final String passphrase = UUID.randomUUID().toString();
        sharedPreferences.edit().putString(PASSPHRASE_KEY, passphrase).commit();
        return passphrase;
    }
}
