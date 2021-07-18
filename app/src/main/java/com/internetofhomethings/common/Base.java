package com.internetofhomethings.common;

import android.app.Application;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class Base extends Application {


    public static final String MQTT_URL = "tcp://broker.emqx.io:1883";
    public static final String MQTT_USERNAME = "emqx";
    public static final String MQTT_PASSWORD = "public";

    //farm name
    public static String FARM_NAME = "******";

    public static final String MQTT_PUBLISH_TOPIC_ALL = FARM_NAME + "/suball";
    public static final String MQTT_PUBLISH_TOPIC_CONFIG = FARM_NAME + "/subconf";

    public static final String MQTT_SUBSCRIBE_TOPIC_ALL = FARM_NAME + "/puball";
    public static final String MQTT_SUBSCRIBE_TOPIC_CONFIG = FARM_NAME + "/pubconf";
    public static final String MQTT_SUBSCRIBE_TOPIC_ERROR = FARM_NAME + "/puberr";
    public static final String MQTT_SUBSCRIBE_TOPIC_UPDATE = FARM_NAME + "/pubup";


    @Override
    public void onCreate() {
        super.onCreate();

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("fonts/Yekan.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );


    }
}
