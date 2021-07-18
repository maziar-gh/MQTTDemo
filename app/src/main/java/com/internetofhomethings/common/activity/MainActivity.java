package com.internetofhomethings.common.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.internetofhomethings.common.Base;
import com.internetofhomethings.common.R;
import com.internetofhomethings.common.service.MqttMessageService;
import com.internetofhomethings.common.utiles.ColorArcProgressBar;
import com.internetofhomethings.common.utiles.PahoMqttClient;
import com.internetofhomethings.common.utiles.SaveData;
import com.internetofhomethings.common.utiles.SavePref;
import com.jacksonandroidnetworking.JacksonParserFactory;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import cn.pedant.SweetAlert.SweetAlertDialog;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class MainActivity extends AppCompatActivity {

    long maxtemp = 0;
    long realtemp1, realtemp2, realtemp3, realtemp4, realtemp5, realtemp6, realtemp7, realtemp8 = 0;
    long checktime = 0;
    String versionos = "0";
    String licence, datetime, url;

    final String command = "{\"r1\":\"alloff\"}";
    Thread timer;

    SavePref save;

    boolean isOnline, li = false;
    ImageView img_isonline, img_status;
    Switch sw_1, sw_2, sw_3, sw_4, sw_5, sw_6, swfan1, swfan2;
    CardView card1, card2, card3, card4, card5, card6, card7, card8;
    CardView card_t_1, card_t_2, card_t_3, card_t_4, card_t_5, card_t_6, card_t_7, card_t_8;
    boolean r1, r2, r3, r4, r5, r6, r7, r8;
    TextView tv_1, tv_2, tv_3, tv_4, tv_5, tv_6, tv_7, tv_8;
    ColorArcProgressBar progress1, progress2, progress3, progress4, progress5, progress6, progress7, progress8;


    private MqttAndroidClient client;
    private String TAG = "MainActivity";
    private PahoMqttClient pahoMqttClient;
    private String clientid = "";
    private Timer myTimer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Main Activity layout file

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_app_launcher_round);

        save = new SavePref(this);

        img_isonline = (ImageView) findViewById(R.id.img_status2);
        img_status = (ImageView) findViewById(R.id.img_status);

        sw_1 = (Switch) findViewById(R.id.sw1);
        sw_2 = (Switch) findViewById(R.id.sw2);
        sw_3 = (Switch) findViewById(R.id.sw3);
        sw_4 = (Switch) findViewById(R.id.sw4);
        sw_5 = (Switch) findViewById(R.id.sw5);
        sw_6 = (Switch) findViewById(R.id.sw6);

        swfan1 = (Switch) findViewById(R.id.swfan1);
        swfan2 = (Switch) findViewById(R.id.swfan2);

        card_t_1 = (CardView) findViewById(R.id.card_t_1);
        card_t_2 = (CardView) findViewById(R.id.card_t_2);
        card_t_3 = (CardView) findViewById(R.id.card_t_3);
        card_t_4 = (CardView) findViewById(R.id.card_t_4);
        card_t_5 = (CardView) findViewById(R.id.card_t_5);
        card_t_6 = (CardView) findViewById(R.id.card_t_6);
        card_t_7 = (CardView) findViewById(R.id.card_t_7);
        card_t_8 = (CardView) findViewById(R.id.card_t_8);

        /*
        card_t_2.setVisibility(View.GONE);
        card_t_3.setVisibility(View.GONE);
        card_t_4.setVisibility(View.GONE);
        card_t_5.setVisibility(View.GONE);
        card_t_6.setVisibility(View.GONE);
        card_t_7.setVisibility(View.GONE);
        card_t_8.setVisibility(View.GONE);*/

        card1 = (CardView) findViewById(R.id.card1);
        card2 = (CardView) findViewById(R.id.card2);
        card3 = (CardView) findViewById(R.id.card3);
        card4 = (CardView) findViewById(R.id.card4);
        card5 = (CardView) findViewById(R.id.card5);
        card6 = (CardView) findViewById(R.id.card6);
        card7 = (CardView) findViewById(R.id.card7);
        card8 = (CardView) findViewById(R.id.card8);

        /*
        card2.setVisibility(View.GONE);
        card3.setVisibility(View.GONE);
        card4.setVisibility(View.GONE);
        card5.setVisibility(View.GONE);
        card6.setVisibility(View.GONE);
        card7.setVisibility(View.GONE);
        card8.setVisibility(View.GONE);
*/
        
        tv_1 = (TextView) findViewById(R.id.tv1);
        tv_2 = (TextView) findViewById(R.id.tv2);
        tv_3 = (TextView) findViewById(R.id.tv3);
        tv_4 = (TextView) findViewById(R.id.tv4);
        tv_5 = (TextView) findViewById(R.id.tv5);
        tv_6 = (TextView) findViewById(R.id.tv6);
        tv_7 = (TextView) findViewById(R.id.tvfan1);
        tv_8 = (TextView) findViewById(R.id.tvfan2);

        tv_1.setText(save.load(SaveData.TV_1.name(), tv_1.getText().toString()));
        tv_2.setText(save.load(SaveData.TV_2.name(), tv_2.getText().toString()));
        tv_3.setText(save.load(SaveData.TV_3.name(), tv_3.getText().toString()));
        tv_4.setText(save.load(SaveData.TV_4.name(), tv_4.getText().toString()));
        tv_5.setText(save.load(SaveData.TV_5.name(), tv_5.getText().toString()));
        tv_6.setText(save.load(SaveData.TV_6.name(), tv_6.getText().toString()));
        tv_7.setText(save.load(SaveData.TV_7.name(), tv_7.getText().toString()));
        tv_8.setText(save.load(SaveData.TV_8.name(), tv_8.getText().toString()));

        progress1 = (ColorArcProgressBar) findViewById(R.id.progress1);
        progress2 = (ColorArcProgressBar) findViewById(R.id.progress2);
        progress3 = (ColorArcProgressBar) findViewById(R.id.progress3);
        progress4 = (ColorArcProgressBar) findViewById(R.id.progress4);
        progress5 = (ColorArcProgressBar) findViewById(R.id.progress5);
        progress6 = (ColorArcProgressBar) findViewById(R.id.progress6);
        progress7 = (ColorArcProgressBar) findViewById(R.id.progress7);
        progress8 = (ColorArcProgressBar) findViewById(R.id.progress8);

        progress1.setTitle(tv_1.getText().toString());
        progress2.setTitle(tv_2.getText().toString());
        progress3.setTitle(tv_3.getText().toString());
        progress4.setTitle(tv_4.getText().toString());
        progress5.setTitle(tv_5.getText().toString());
        progress6.setTitle(tv_6.getText().toString());
        progress7.setTitle(tv_7.getText().toString());
        progress8.setTitle(tv_8.getText().toString());


        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        AndroidNetworking.initialize(getApplicationContext());
        AndroidNetworking.setParserFactory(new JacksonParserFactory());

        check();


        /*Intent serviceIntent = new Intent(this, MqttMessageService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            ContextCompat.startForegroundService(this, serviceIntent);
            return;
        }
        this.startService(serviceIntent);*/
        startService(new Intent(this, MqttMessageService.class));

        //Generate unique client id for MQTT broker connection
        Random r = new Random();
        int i1 = r.nextInt(5999 - 1) + 1;
        clientid = "mqtt" + i1;

        //Get Edit field values from layout GUI

        pahoMqttClient = new PahoMqttClient();
        client = pahoMqttClient.getMqttClient(getApplicationContext(),                        // Connect to MQTT Broker
                Base.MQTT_URL,
                clientid,
                Base.MQTT_USERNAME,
                Base.MQTT_PASSWORD
        );


        //Create listener for MQTT messages.
        mqttCallback();

        //Create Timer to report MQTT connection status every 1 second
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                ScheduleTasks();
            }

        }, 0, 1000);


        switchHandle();
    }


    private Date parseDate(String date, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        return formatter.parse(date);
    }


    private void check() {

        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


        AndroidNetworking.get("http://figureapp.ir/check/check.php?id=" + Base.FARM_NAME)
                .setTag("li")
                .setPriority(Priority.LOW)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {

                        try {
                            licence = response.getString("licence");
                            datetime = response.getString("datetime");
                            url = response.getString("url");
                        } catch (JSONException e) {
                            e.printStackTrace();

                        }

                        if (licence.equals("qZ2eqFWeqFWkoA==")) {
                            try {
                                Date date = parseDate(sdf1.format(timestamp), "yyyy-MM-dd HH:mm:ss");
                                Date date1 = parseDate(datetime, "yyyy-MM-dd HH:mm:ss");

                                if (date.compareTo(date1) == 1) {


                                    li = true;

                                    card1.setEnabled(false);
                                    card2.setEnabled(false);
                                    card3.setEnabled(false);
                                    card4.setEnabled(false);
                                    card5.setEnabled(false);
                                    card6.setEnabled(false);
                                    card7.setEnabled(false);
                                    card8.setEnabled(false);/*
                                    */

                                    progress1.setCurrentValues(0);
                                    progress2.setCurrentValues(0);
                                    progress3.setCurrentValues(0);
                                    progress4.setCurrentValues(0);
                                    progress5.setCurrentValues(0);
                                    progress6.setCurrentValues(0);
                                    progress7.setCurrentValues(0);
                                    progress8.setCurrentValues(0);


                                    new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                                            .setTitleText("سیستم")
                                            .setContentText("جهت کارکرد درست سیستم نرم افزار را بروز رسانی کنید")
                                            .setConfirmText("بروز رسانی")
                                            .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                @Override
                                                public void onClick(SweetAlertDialog sDialog) {

                                                    Intent i = new Intent(Intent.ACTION_VIEW);
                                                    i.setData(Uri.parse(url));
                                                    startActivity(i);

                                                    finish();
                                                }
                                            })
                                            .show();
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                    }
                });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.mnu_maxtemp:
                if (!li)
                    dialog(getApplicationContext(), 1, 0, save.load(SaveData.MAXTEMP.name(),"0"), "حداکثر دما جهت خاموشی وسایل متصل(به سانتی گراد)");
                break;
            case R.id.mnu_check:
                if (!li)
                    dialog(getApplicationContext(), 3, 0, String.valueOf(checktime / 1000), "زمان چک کردن دما به صورت خودکار(به ثانیه)");
                break;
            /*case R.id.mnu_update:
                if (!li) dialog(getApplicationContext(), 4, 0, "", "نام فایل را وارد کنید");

                break;*/
            case R.id.mnu_about:
                Toast.makeText(this, versionos, Toast.LENGTH_LONG).show();
                break;
            case R.id.mnu_tel:
                Intent telegram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://telegram.me/Bit_man_manager"));
                startActivity(telegram);
                break;
            case R.id.mnu_reset:
                if (!li) publish("{\"r1\":\"rst\"}", "ریست وایفای", false);

                break;
            case R.id.mnu_reset_sys:
                if (!li) publish("{\"r1\":\"reset\"}", "ریست کلی", false);
                break;
            /*case R.id.mnu_real:
                if (!li)
                    dialog(getApplicationContext(), 2, 0, String.valueOf(realtemp1), "از 1 به بالا");

                break;*/
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendfakepublish() {
        timer = new Thread() {
            public void run(){
                try {
                    sleep(3000);

                    if (!pahoMqttClient.mqttAndroidClient.isConnected()) {
                        Toast.makeText(MainActivity.this, "اتصال سرور قطع می باشد", Toast.LENGTH_SHORT).show();
                    }

                    if (!command.isEmpty()) {
                        try {
                            pahoMqttClient.publishMessage(client, command, 1, Base.MQTT_PUBLISH_TOPIC_ALL);

                        } catch (MqttException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        timer.start();
    }

    private void switchHandle() {

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r1) {
                    r1 = publish("{\"r1\":\"on\"}", "روشن", r1);
                } else {
                    r1 = publish("{\"r1\":\"off\"}", "خاموش", r1);
                }
            }
        });

        card2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r2) {
                    r2 = publish("{\"r2\":\"on\"}", "روشن", r2);
                } else {
                    r2 = publish("{\"r2\":\"off\"}", "خاموش", r2);
                }
            }
        });

        card3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r3) {
                    r3 = publish("{\"r3\":\"on\"}", "روشن", r3);
                } else {
                    r3 = publish("{\"r3\":\"off\"}", "خاموش", r3);
                }
            }
        });

        card4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r4) {
                    r4 = publish("{\"r4\":\"on\"}", "روشن", r4);
                } else {
                    r4 = publish("{\"r4\":\"off\"}", "خاموش", r4);
                }
            }
        });

        card5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r5) {
                    r5 = publish("{\"r5\":\"on\"}", "روشن", r5);
                } else {
                    r5 = publish("{\"r5\":\"off\"}", "خاموش", r5);
                }
            }
        });

        card6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r6) {
                    r6 = publish("{\"r6\":\"on\"}", "روشن", r6);
                } else {
                    r6 = publish("{\"r6\":\"off\"}", "خاموش", r6);
                }
            }
        });

        card7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r7) {
                    r7 = publish("{\"fan1\":\"on\"}", "روشن", r7);
                } else {
                    r7 = publish("{\"fan1\":\"off\"}", "خاموش", r7);
                }
            }
        });


        card8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (r8) {
                    r8 = publish("{\"fan2\":\"on\"}", "روشن", r8);
                } else {
                    r8 = publish("{\"fan2\":\"off\"}", "خاموش", r8);
                }
            }
        });


        card1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialog(getApplicationContext(), 5, 1, tv_1.getText().toString(), "نام دلخواه شما:");
                return true;
            }
        });

        card2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialog(getApplicationContext(), 5, 2, tv_2.getText().toString(), "نام دلخواه شما:");
                return true;
            }
        });

        card3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialog(getApplicationContext(), 5, 3, tv_3.getText().toString(), "نام دلخواه شما:");
                return true;
            }
        });

        card4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialog(getApplicationContext(), 5, 4, tv_4.getText().toString(), "نام دلخواه شما:");
                return true;
            }
        });

        card5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialog(getApplicationContext(), 5, 5, tv_5.getText().toString(), "نام دلخواه شما:");
                return true;
            }
        });

        card6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialog(getApplicationContext(), 5, 6, tv_6.getText().toString(), "نام دلخواه شما:");
                return true;
            }
        });

        card7.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialog(getApplicationContext(), 5, 7, tv_7.getText().toString(), "نام دلخواه شما:");
                return true;
            }
        });

        card8.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                dialog(getApplicationContext(), 5, 8, tv_8.getText().toString(), "نام دلخواه شما:");
                return true;
            }
        });


        card_t_1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                dialog(getApplicationContext(), 6, 1, save.load(SaveData.T_1.name(),"0"), "دمای" + " (" + tv_1.getText() + ") " + "را کالیبره کنید:");
                return true;
            }
        });

        card_t_2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog(getApplicationContext(), 6, 2, save.load(SaveData.T_2.name(),"0"), "دمای" + " (" + tv_2.getText() + ") " + "را کالیبره کنید:");
                return true;
            }
        });

        card_t_3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog(getApplicationContext(), 6, 3, save.load(SaveData.T_3.name(),"0"), "دمای" + " (" + tv_3.getText() + ") " + "را کالیبره کنید:");
                return true;
            }
        });

        card_t_4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog(getApplicationContext(), 6, 4, save.load(SaveData.T_4.name(),"0"), "دمای" + " (" + tv_4.getText() + ") " + "را کالیبره کنید:");
                return true;
            }
        });

        card_t_5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog(getApplicationContext(), 6, 5, save.load(SaveData.T_5.name(),"0"), "دمای" + " (" + tv_5.getText() + ") " + "را کالیبره کنید:");
                return true;
            }
        });

        card_t_6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog(getApplicationContext(), 6, 6, save.load(SaveData.T_6.name(),"0"), "دمای" + " (" + tv_6.getText() + ") " + "را کالیبره کنید:");
                return true;
            }
        });

        card_t_7.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog(getApplicationContext(), 6, 7, save.load(SaveData.T_7.name(),"0"), "دمای" + " (" + tv_7.getText() + ") " + "را کالیبره کنید:");
                return true;
            }
        });

        card_t_8.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                dialog(getApplicationContext(), 6, 8, save.load(SaveData.T_8.name(),"0"), "دمای" + " (" + tv_8.getText() + ") " + "را کالیبره کنید:");
                return true;
            }
        });
    }

    private void ScheduleTasks() {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(RunScheduledTasks);
    }


    private Runnable RunScheduledTasks = new Runnable() {
        public void run() {
            //This method runs in the same thread as the UI.

            //Check MQTT Connection Status

            if (pahoMqttClient.mqttAndroidClient.isConnected()) {
                img_status.setImageResource(R.drawable.round_shape_green);

                if (!pahoMqttClient.mqttAndroidClient.isConnected()) {
                }

                if (!Base.MQTT_SUBSCRIBE_TOPIC_ALL.isEmpty()) {
                    try {
                        pahoMqttClient.subscribe(client, Base.MQTT_SUBSCRIBE_TOPIC_ALL, 1);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                if (!Base.MQTT_SUBSCRIBE_TOPIC_CONFIG.isEmpty()) {
                    try {
                        pahoMqttClient.subscribe(client, Base.MQTT_SUBSCRIBE_TOPIC_CONFIG, 1);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                if (!Base.MQTT_SUBSCRIBE_TOPIC_ERROR.isEmpty()) {
                    try {
                        pahoMqttClient.subscribe(client, Base.MQTT_SUBSCRIBE_TOPIC_ERROR, 1);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }
                if (!Base.MQTT_SUBSCRIBE_TOPIC_UPDATE.isEmpty()) {
                    try {
                        pahoMqttClient.subscribe(client, Base.MQTT_SUBSCRIBE_TOPIC_UPDATE, 1);
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }
                }

            } else
                img_status.setImageResource(R.drawable.round_shape_red);
        }
    };


    // Called when a subscribed message is received
    protected void mqttCallback() {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                //msg("Connection lost...");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {

                toggleStatus();


                if (topic.equals(Base.MQTT_SUBSCRIBE_TOPIC_UPDATE)) {

                    //String msg = "topic: " + topic + "\r\nMessage: " + message.toString() + "\r\n";
                    //Log.e("UPDATE---------", msg);

                    JSONObject jsonObject = new JSONObject(message.toString());

                    if (jsonObject.getString("update_text").equals("p")) {
                        Toast.makeText(MainActivity.this, "progress...", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getString("update_text").equals("f")) {
                        Toast.makeText(MainActivity.this, "finish update --> restart", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getString("update_text").equals("s")) {
                        Toast.makeText(MainActivity.this, "start update", Toast.LENGTH_SHORT).show();

                    }


                } else if (topic.equals(Base.MQTT_SUBSCRIBE_TOPIC_ERROR)) {

                    //String msg = "topic: " + topic + "\r\nMessage: " + message.toString() + "\r\n";
                    //Log.e("ERROR---------", msg);

                    JSONObject jsonObject = new JSONObject(message.toString());

                    if (jsonObject.getString("error").equals("temp1")) {
                        Toast.makeText(MainActivity.this, "دمای بالا! دستگاه 1 خاموش شد", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getString("error").equals("temp2")) {
                        Toast.makeText(MainActivity.this, "دمای بالا! دستگاه 2 خاموش شد", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getString("error").equals("temp3")) {
                        Toast.makeText(MainActivity.this, "دمای بالا! دستگاه 3 خاموش شد", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getString("error").equals("temp4")) {
                        Toast.makeText(MainActivity.this, "دمای بالا! دستگاه 4 خاموش شد", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getString("error").equals("temp5")) {
                        Toast.makeText(MainActivity.this, "دمای بالا! دستگاه 5 خاموش شد", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getString("error").equals("temp6")) {
                        Toast.makeText(MainActivity.this, "دمای بالا! دستگاه 6 خاموش شد", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getString("error").equals("temp7")) {
                        Toast.makeText(MainActivity.this, "دمای بالا! دستگاه 7 خاموش شد", Toast.LENGTH_SHORT).show();

                    } else if (jsonObject.getString("error").equals("temp8")) {
                        Toast.makeText(MainActivity.this, "دمای بالا! دستگاه 8 خاموش شد", Toast.LENGTH_SHORT).show();
                    }


                } else if (topic.equals(Base.MQTT_SUBSCRIBE_TOPIC_CONFIG)) {


                    //String msg = "topic: " + topic + "\r\nMessage: " + message.toString() + "\r\n";
                    //Log.e("CONFIG---------", msg + "}");

                    JSONObject jsonObject = new JSONObject(message.toString() + "}");
                    //JSONObject jsonObject = new JSONObject(message.toString());

                    maxtemp = Long.parseLong(jsonObject.getString("maxtemp"));
                    checktime = Long.parseLong(jsonObject.getString("checktime"));
                    versionos = jsonObject.getString("versionos");

                    String[] all = jsonObject.getString("realtemp").split(",");

                    realtemp1 = Long.parseLong(all[0]);
                    realtemp2 = Long.parseLong(all[1]);
                    realtemp3 = Long.parseLong(all[2]);
                    realtemp4 = Long.parseLong(all[3]);
                    realtemp5 = Long.parseLong(all[4]);
                    realtemp6 = Long.parseLong(all[5]);
                    realtemp7 = Long.parseLong(all[6]);
                    realtemp8 = Long.parseLong(all[7]);



                    save.save(SaveData.MAXTEMP.name(), String.valueOf(maxtemp));
                    save.save(SaveData.CHECK_TIME.name(), checktime);

                    save.save(SaveData.T_1.name(), String.valueOf(realtemp1));
                    save.save(SaveData.T_2.name(), String.valueOf(realtemp2));
                    save.save(SaveData.T_3.name(), String.valueOf(realtemp3));
                    save.save(SaveData.T_4.name(), String.valueOf(realtemp4));
                    save.save(SaveData.T_5.name(), String.valueOf(realtemp5));
                    save.save(SaveData.T_6.name(), String.valueOf(realtemp6));
                    save.save(SaveData.T_7.name(), String.valueOf(realtemp7));
                    save.save(SaveData.T_8.name(), String.valueOf(realtemp8));/*
                    */

                } else if (topic.equals(Base.MQTT_SUBSCRIBE_TOPIC_ALL)) {


                    //String msg = "topic: " + topic + "\r\nMessage: " + message.toString() + "\r\n";
                    //Log.e("TAG--------", msg);

                    JSONObject jsonObject = new JSONObject(message.toString());

                    if (jsonObject.getString("r1").equals("on")) {
                        sw_1.setChecked(true);
                        r1 = false;
                    } else {
                        sw_1.setChecked(false);
                        r1 = true;
                    }

                    if (jsonObject.getString("r2").equals("on")) {
                        sw_2.setChecked(true);
                        r2 = false;
                    } else {
                        sw_2.setChecked(false);
                        r2 = true;
                    }

                    if (jsonObject.getString("r3").equals("on")) {
                        sw_3.setChecked(true);
                        r3 = false;
                    } else {
                        sw_3.setChecked(false);
                        r3 = true;
                    }

                    if (jsonObject.getString("r4").equals("on")) {
                        sw_4.setChecked(true);
                        r4 = false;
                    } else {
                        sw_4.setChecked(false);
                        r4 = true;
                    }

                    if (jsonObject.getString("r5").equals("on")) {
                        sw_5.setChecked(true);
                        r5 = false;
                    } else {
                        sw_5.setChecked(false);
                        r5 = true;
                    }

                    if (jsonObject.getString("r6").equals("on")) {
                        sw_6.setChecked(true);
                        r6 = false;
                    } else {
                        sw_6.setChecked(false);
                        r6 = true;
                    }

                    if (jsonObject.getString("fan1").equals("on")) {
                        swfan1.setChecked(true);
                        r7 = false;
                    } else {
                        swfan1.setChecked(false);
                        r7 = true;
                    }

                    if (jsonObject.getString("fan2").equals("on")) {
                        swfan2.setChecked(true);
                        r8 = false;
                    } else {
                        swfan2.setChecked(false);
                        r8 = true;
                    }

                    if (jsonObject.getString("temp").equals("t1")) {
                        Toast.makeText(MainActivity.this, "خطا در خواندن سنسور دما 1", Toast.LENGTH_SHORT).show();

                    }if (jsonObject.getString("temp").equals("t2")) {
                        Toast.makeText(MainActivity.this, "خطا در خواندن سنسور دما 2", Toast.LENGTH_SHORT).show();

                    }if (jsonObject.getString("temp").equals("t3")) {
                        Toast.makeText(MainActivity.this, "خطا در خواندن سنسور دما 3", Toast.LENGTH_SHORT).show();

                    }if (jsonObject.getString("temp").equals("t4")) {
                        Toast.makeText(MainActivity.this, "خطا در خواندن سنسور دما 4", Toast.LENGTH_SHORT).show();

                    }if (jsonObject.getString("temp").equals("t5")) {
                        Toast.makeText(MainActivity.this, "خطا در خواندن سنسور دما 5", Toast.LENGTH_SHORT).show();

                    }if (jsonObject.getString("temp").equals("t6")) {
                        Toast.makeText(MainActivity.this, "خطا در خواندن سنسور دما 6", Toast.LENGTH_SHORT).show();

                    }if (jsonObject.getString("temp").equals("t7")) {
                        Toast.makeText(MainActivity.this, "خطا در خواندن سنسور دما 7", Toast.LENGTH_SHORT).show();

                    }if (jsonObject.getString("temp").equals("t8")) {
                        Toast.makeText(MainActivity.this, "خطا در خواندن سنسور دما 8", Toast.LENGTH_SHORT).show();

                    } else {

                        String[] all = jsonObject.getString("temp").split(",");

                        //String t8 = all[7].substring(0, all[7].indexOf(".")).trim();

                        //Log.e("TAG---------",t1);
                        progress1.setCurrentValues(Float.parseFloat(all[0]));
                        progress2.setCurrentValues(Float.parseFloat(all[1]));
                        progress3.setCurrentValues(Float.parseFloat(all[2]));
                        progress4.setCurrentValues(Float.parseFloat(all[3]));
                        progress5.setCurrentValues(Float.parseFloat(all[4]));
                        progress6.setCurrentValues(Float.parseFloat(all[5]));
                        progress7.setCurrentValues(Float.parseFloat(all[6]));
                        progress8.setCurrentValues(Float.parseFloat(all[7]));/*
                        */


                    }


                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }

    private void toggleStatus() {

        check();

        if (isOnline) {
            isOnline = !isOnline;
            img_isonline.setImageResource(R.drawable.round_shape_yellow);
        } else {
            isOnline = !isOnline;
            img_isonline.setImageResource(R.drawable.round_shape_orange);
        }
    }

    private boolean publish(final String command, final String button, boolean bool) {

        new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE)
                .setTitleText("آیا مطمعن هستید؟")
                .setContentText("")
                .setConfirmText(button)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        if (!pahoMqttClient.mqttAndroidClient.isConnected()) {
                            Toast.makeText(MainActivity.this, "اتصال سرور قطع می باشد", Toast.LENGTH_SHORT).show();
                        }

                        if (!command.isEmpty()) {
                            try {
                                pahoMqttClient.publishMessage(client, command, 1, Base.MQTT_PUBLISH_TOPIC_ALL);

                            } catch (MqttException e) {
                                e.printStackTrace();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        }

                        if(button.equals("ریست وایفای") || button.equals("ریست کلی")) sendfakepublish();

                        sDialog.dismissWithAnimation();
                    }
                })
                .setCancelButton("لغو", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismissWithAnimation();
                    }
                })
                .show();

        return !bool;

    }

    private void dialog(Context context, final int op, final int sub, final String placeholder, String showtext) {

        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.custom_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new androidx.appcompat.app.AlertDialog.Builder(this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final TextView textView = (TextView) promptsView
                .findViewById(R.id.tvDialogUserInput);
        final EditText userInput = (EditText) promptsView
                .findViewById(R.id.editTextDialogUserInput);

        userInput.setHint(placeholder);
        textView.setText(showtext);

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("تایید",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // get user input and set it to result
                                // edit text
                                if (String.valueOf(userInput.getText()).trim().length() <= 0) {
                                    Toast.makeText(getApplicationContext(), "جهت تایید مقدار وارد کنید", Toast.LENGTH_SHORT).show();
                                } else {

                                    long result = 0;

                                    if (op == 1) {
                                        result = Long.parseLong(String.valueOf(userInput.getText()).trim());
                                        maxtemp = result;
                                        save.save(SaveData.MAXTEMP.name(), String.valueOf(maxtemp));
                                        publishconfig("{\"maxtemp\":\"" + result + "\"}");

                                    }else if (op == 2) {
                                        result = Long.parseLong(String.valueOf(userInput.getText()).trim());
                                        realtemp1 = result;
                                        publishconfig("{\"realtemp\":\"" + (result) + "\"}");

                                    } else if (op == 3) {
                                        result = Long.parseLong(String.valueOf(userInput.getText()).trim());
                                        checktime = result * 1000;
                                        save.save(SaveData.CHECK_TIME.name(), checktime);
                                        publishconfig("{\"checktime\":\"" + (result * 1000) + "\"}");

                                    } else if (op == 4) {
                                        publishconfig("{\"update\":\"" + String.valueOf(userInput.getText()).trim() + "\"}");
                                        sendfakepublish();
                                    } else if (op == 5) {
                                        String d = String.valueOf(userInput.getText()).trim();
                                        if (sub == 1) {
                                            tv_1.setText(d);
                                            progress1.setTitle(d);
                                            save.save(SaveData.TV_1.name(), d);
                                        } else if (sub == 2) {
                                            tv_2.setText(d);
                                            progress2.setTitle(d);
                                            save.save(SaveData.TV_2.name(), d);
                                        } else if (sub == 3) {
                                            tv_3.setText(d);
                                            progress3.setTitle(d);
                                            save.save(SaveData.TV_3.name(), d);
                                        } else if (sub == 4) {
                                            tv_4.setText(d);
                                            progress4.setTitle(d);
                                            save.save(SaveData.TV_4.name(), d);
                                        } else if (sub == 5) {
                                            tv_5.setText(d);
                                            progress5.setTitle(d);
                                            save.save(SaveData.TV_5.name(), d);
                                        } else if (sub == 6) {
                                            tv_6.setText(d);
                                            progress6.setTitle(d);
                                            save.save(SaveData.TV_6.name(), d);
                                        } else if (sub == 7) {
                                            tv_7.setText(d);
                                            save.save(SaveData.TV_7.name(), d);
                                        } else if (sub == 8) {
                                            tv_8.setText(d);
                                            save.save(SaveData.TV_8.name(), d);
                                        }

                                    } else if (op == 6) {
                                        String rtemp1 = save.load(SaveData.T_1.name(),"0");
                                        String rtemp2 = save.load(SaveData.T_2.name(),"0");
                                        String rtemp3 = save.load(SaveData.T_3.name(),"0");
                                        String rtemp4 = save.load(SaveData.T_4.name(),"0");
                                        String rtemp5 = save.load(SaveData.T_5.name(),"0");
                                        String rtemp6 = save.load(SaveData.T_6.name(),"0");
                                        String rtemp7 = save.load(SaveData.T_7.name(),"0");
                                        String rtemp8 = save.load(SaveData.T_8.name(),"0");
                                        String t = String.valueOf(userInput.getText()).trim();

                                        if (sub == 1) {
                                            rtemp1 = t;
                                            save.save(SaveData.T_1.name(), t);
                                        } else if (sub == 2) {
                                            rtemp2 = t;
                                            save.save(SaveData.T_2.name(), t);
                                        } else if (sub == 3) {
                                            rtemp3 = t;
                                            save.save(SaveData.T_3.name(), t);
                                        }else if (sub == 4) {
                                            rtemp4 = t;
                                            save.save(SaveData.T_4.name(), t);
                                        }else if (sub == 5) {
                                            rtemp5 = t;
                                            save.save(SaveData.T_5.name(), t);
                                        }else if (sub == 6) {
                                            rtemp6 = t;
                                            save.save(SaveData.T_6.name(), t);
                                        }else if (sub == 7) {
                                            rtemp7 = t;
                                            save.save(SaveData.T_7.name(), t);
                                        }else if (sub == 8) {
                                            rtemp8 = t;
                                            save.save(SaveData.T_8.name(), t);
                                        }



                                        rtemp1 = rtemp1 + "," + rtemp2 + "," + rtemp3 + "," + rtemp4 + "," +
                                                rtemp5 + "," + rtemp6 + "," + rtemp7 + "," + rtemp8;
                                        /*rtemp1 = rtemp1 + "," + rtemp2 + "," + rtemp3 + "," + rtemp4 + "," +
                                                rtemp5 + "," + rtemp6 + "," + rtemp7;*/
                                        //rtemp1 = rtemp1 + "," + rtemp2 + "," + rtemp3 + "," + rtemp4 + "," + rtemp5;
                                        //rtemp1 = rtemp1 + "," + rtemp2;
                                        //rtemp1 = rtemp1;


                                        //Log.e("TAG--------", rtemp1);
                                        //Log.e("TAG--------", "{\"realtemp\": \"" + (rtemp1) + "\"}");

                                        publishconfig("{\"realtemp\": \"" + (rtemp1) + "\"}");
                                    }

                                }
                            }
                        })
                .setNegativeButton("لغو",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    private void publishconfig(String command) {

        if (!pahoMqttClient.mqttAndroidClient.isConnected()) {
            Toast.makeText(this, "اتصال سرور قطع می باشد", Toast.LENGTH_SHORT).show();
        }

        if (!command.isEmpty()) {
            try {
                pahoMqttClient.publishMessage(client, command, 1, Base.MQTT_PUBLISH_TOPIC_CONFIG);

            } catch (MqttException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
