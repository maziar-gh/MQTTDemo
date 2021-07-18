package com.internetofhomethings.common.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.internetofhomethings.common.Base;
import com.internetofhomethings.common.utiles.PahoMqttClient;
import com.internetofhomethings.common.R;
import com.internetofhomethings.common.activity.MainActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONObject;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.annotation.NonNull;

public class MqttMessageService extends Service {

    private static final String TAG = "MqttMessageService";
    private PahoMqttClient pahoMqttClient;
    private MqttAndroidClient mqttAndroidClient;
    private String clientid = "";
    private Timer myTimer;

    public MqttMessageService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate");

        Random r = new Random();        //Unique Client ID for connection
        int i1 = r.nextInt(5000 - 1) + 1;
        clientid = "mqtt" + i1;

        pahoMqttClient = new PahoMqttClient();
        mqttAndroidClient = pahoMqttClient.getMqttClient(getApplicationContext(), Base.MQTT_URL, clientid, Base.MQTT_USERNAME,Base.MQTT_PASSWORD);

        //Create Timer to report MQTT connection status every 1 second
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ScheduleTasks();
            }

        }, 0, 1000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "onStartCommand");


        mqttCallback();

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
        //stopForeground(true);
    }


    private void ScheduleTasks() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        new Handler(Looper.getMainLooper()).post(RunScheduledTasks);

        //this.runOnUiThread(RunScheduledTasks);
    }


    private Runnable RunScheduledTasks = new Runnable() {
        public void run() {
            //This method runs in the same thread as the UI.

            //Check MQTT Connection Status

            if (pahoMqttClient.mqttAndroidClient.isConnected()) {

                if (!Base.MQTT_SUBSCRIBE_TOPIC_ALL.isEmpty()) {
                    try {
                        pahoMqttClient.subscribe(mqttAndroidClient, Base.MQTT_SUBSCRIBE_TOPIC_ALL, 1);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

                if (!Base.MQTT_SUBSCRIBE_TOPIC_ERROR.isEmpty()) {
                    try {
                        pahoMqttClient.subscribe(mqttAndroidClient, Base.MQTT_SUBSCRIBE_TOPIC_ERROR, 1);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

            }/* else
                setMessageNotification("هشدار", "ارتباط با سرور قطع شده");*/
        }
    };

    private void mqttCallback() {
        mqttAndroidClient.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
                Log.d(TAG, "connectComplete");


            }

            @Override
            public void connectionLost(Throwable throwable) {
                Log.d(TAG, "connectionLost");
            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                //Log.d(TAG, "topic: " + topic);
                //Log.d(TAG, "messageArrived: " + mqttMessage.toString());
                //Toast.makeText(MqttMessageService.this, mqttMessage.toString(), Toast.LENGTH_SHORT).show();

                JSONObject jsonObject = new JSONObject(mqttMessage.toString());

                if (jsonObject.getString("error").equals("temp1")) {
                    setMessageNotification("دستگاه 1", "دمای بالا! دستگاه 1 خاموش شد");

                } else if (jsonObject.getString("error").equals("temp2")) {
                    setMessageNotification("دستگاه 21", "دمای بالا! دستگاه 21 خاموش شد");

                } else if (jsonObject.getString("error").equals("temp3")) {
                    setMessageNotification("دستگاه 3", "دمای بالا! دستگاه 3 خاموش شد");

                } else if (jsonObject.getString("error").equals("temp4")) {
                    setMessageNotification("دستگاه 4", "دمای بالا! دستگاه 4 خاموش شد");

                } else if (jsonObject.getString("error").equals("temp5")) {
                    setMessageNotification("دستگاه 5", "دمای بالا! دستگاه 5 خاموش شد");

                } else if (jsonObject.getString("error").equals("temp6")) {
                    setMessageNotification("دستگاه 6", "دمای بالا! دستگاه 6 خاموش شد");
                }

            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
                Log.d(TAG, "deliveryComplete");
            }
        });
    }

    private void setMessageNotification(@NonNull String topic, @NonNull String msg) {
        Log.e(TAG, "setMessageNotification");


        String CHANNEL_ID = "channel_1";
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, i, 0);

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        Notification.Builder builder = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification
                    .Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.ic_message_black_24dp)
                    .setContentTitle(topic)
                    .setContentText(msg)
                    .setContentIntent(pi)
                    .setAutoCancel(true);
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "System Alert", NotificationManager.IMPORTANCE_DEFAULT);
            assert mNotificationManager != null;
            mNotificationManager.createNotificationChannel(channel);

        } else {
            builder = new Notification
                    .Builder(this)
                    .setSmallIcon(R.drawable.ic_message_black_24dp)
                    .setContentTitle(topic)
                    .setContentText(msg)
                    .setContentIntent(pi)
                    .setAutoCancel(true);
        }

        mNotificationManager.notify(100, builder.build());



/*
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_message_black_24dp)
                        .setContentTitle(topic)
                        .setContentText(msg);
        Intent resultIntent = new Intent(this, MainActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(100, mBuilder.build());*/
    }
}
