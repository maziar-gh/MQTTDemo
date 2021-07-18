package com.internetofhomethings.common.utiles;

import org.eclipse.paho.client.mqttv3.internal.websocket.Base64;

public class Crypto {

    public static String encrypt(String string,  String key) {
        String result = "";
        char ch;
        char keychar;
        int i=0;
        for(int k= string.length(); i<k; ) {
            ch = string.substring(i,1).charAt(0);
            keychar = key.substring((i % key.length())-1,1).charAt(0);
            ch = (char) ((int)(ch)+ (int)(keychar));
            result  += ch;

            i++;
        }
        return Base64.encodeBytes(result.getBytes());
    }

    public static String  decrypt(String string,  String key) {
        String result = "";
        string = Base64.encodeBytes(string.getBytes());
        char ch;
        char keychar;
        int i=0;
        for(int k= string.length(); i<k; ) {
            ch = string.substring(i,1).charAt(0);
            keychar = key.substring((i % key.length())-1,1).charAt(0);
            ch = (char) ((int)(ch)- (int)(keychar));
            result  += ch;
            i++;
        }
        return result;
    }
}
