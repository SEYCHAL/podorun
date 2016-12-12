package com.ericseychal.podorun;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.util.concurrent.TimeUnit;

public class WearService extends WearableListenerService {
    public WearService() {
    }

    private final static String TAG = WearService.class.getCanonicalName();
    protected GoogleApiClient apiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        apiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        apiClient.connect();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        apiClient.disconnect();
    }

    /**
     * Appellé à la réception d'un message envoyé depuis la montre
     * @param messageEvent message reçu
     */
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        super.onMessageReceived(messageEvent);

        //Ouvre une connection vers la montre
        ConnectionResult connectionResult = apiClient.blockingConnect(30, TimeUnit.SECONDS);
        if (!connectionResult.isSuccess()) {
            Log.e(TAG,"Failed to connect to GoogleApiCLient");
            return;
        }

        //Traite le message reçu
        final String path = messageEvent.getPath();
    }

    /**
     * Envoie un message à la montre
     * @param path identifiant du message
     * @param message message à transmettre
     */
    protected void sendMessage(final String path, final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // envoie le message aux montres
                final NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(apiClient).await();
                for (Node node : nodes.getNodes()) {
                    Wearable.MessageApi.sendMessage(apiClient, node.getId(), path, message.getBytes()).await();
                }
            }
        });
    }
}
