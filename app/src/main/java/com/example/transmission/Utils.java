package com.example.transmission;

import java.time.Instant;

public class Utils {
    static String timestampToText(long timestamp) {

        long now = Instant.now().getEpochSecond();

        long difference = now - timestamp;

        if (difference < 60) {
            return "Now";
        }

        if (difference < 3600) {
            return (difference / 60) + "m";
        }

        if (difference < 86400) {
            return (difference / 3600) + "h";
        }

        if (difference < 604800) {
            return (difference / 86400) + "d";
        }

        return Instant.ofEpochSecond(timestamp).toString();
    }

    static int rssiToIconId(int rssi){
        if (rssi >= -55){
            return R.drawable.signal_cellular_4_bar_24;
        }

        if (rssi >= -66){
            return R.drawable.signal_cellular_3_bar_24;
        }

        if (rssi >= -77){
            return R.drawable.signal_cellular_2_bar_24;
        }

        if (rssi >= -88){
            return R.drawable.signal_cellular_1_bar_24;
        }
        return R.drawable.signal_cellular_0_bar_24;
    }

    public static int setFlag(int source, int flag) {
        return source | flag;
    }

    public static int unsetFlag(int source, int flag) {
        return source & ~flag;
    }

    public static boolean getFlag(int source, int flag) {
        return (source & flag) == flag;
    }
}
