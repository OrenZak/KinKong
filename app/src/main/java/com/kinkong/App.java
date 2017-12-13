package com.kinkong;

import android.app.Application;

import kin.sdk.core.KinClient;
import kin.sdk.core.ServiceProvider;
import kin.sdk.core.exception.EthereumClientException;

public class App extends Application {

    //based on parity
    private final String ROPSTEN_TEST_NET_URL = "http://parity.rounds.video:8545";
    private final String MAIN_NET_URL = "http://mainnet.rounds.video:8545";

    // ideally user should be asked for a passphrase when
    // creating an account and then the same passphrase
    // should be used when sending transactions
    // we are using a hardcoded passphrase.
    public static String PASSPHRASE = "user_passphrase";

    public enum NetWorkType {
        MAIN,
        ROPSTEN
    }

    private KinClient kinClient = null;

    @Override
    public void onCreate() {
        super.onCreate();
        createKinClient(NetWorkType.ROPSTEN);

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

    public KinClient getKinClient() {
        return kinClient;
    }

}
