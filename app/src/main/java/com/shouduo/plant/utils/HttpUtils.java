package com.shouduo.plant.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by 刘亨俊 on 17.2.4.
 */

public class HttpUtils {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
