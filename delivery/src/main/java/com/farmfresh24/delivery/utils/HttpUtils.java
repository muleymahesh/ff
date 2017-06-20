package com.farmfresh24.delivery.utils;

import android.os.Build;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by maks on 16/2/16.
 */
public class HttpUtils {


    public static String requestWebService(String serviceUrl,String method,String data) {
        disableConnectionReuseIfNecessary();

        Response response = null;
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        Log.e("request",data);
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .url (serviceUrl)
                .post(body)
                .build();

        try {
            response = client.newCall(request).execute();

            return response.body().string();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * required in order to prevent issues in earlier Android version.
     */
    private static void disableConnectionReuseIfNecessary() {
        // see HttpURLConnection API doc
        if (Integer.parseInt(Build.VERSION.SDK)
                < Build.VERSION_CODES.FROYO) {
//            System.setProperty("http.keepAlive", "false");
        }
    }

    private static String getResponseText(InputStream inStream) {
        // very nice trick from
        // http://weblogs.java.net/blog/pat/archive/2004/10/stupid_scanner_1.html
        String s= new Scanner(inStream).useDelimiter("\\A").next();
        Log.e("response",s);
        return s;
    }
}
