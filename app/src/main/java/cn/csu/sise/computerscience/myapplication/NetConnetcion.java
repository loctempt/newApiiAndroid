package cn.csu.sise.computerscience.myapplication;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Cookie;
import okhttp3.CookieJar;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class NetConnetcion {
    private static final String TAG = "NetConnection";
    private static Context context;
    private static final HashMap<String, List<Cookie>> cookieStore = new HashMap<>();
    private static final OkHttpClient client = new OkHttpClient.Builder().cookieJar(new CookieJar() {
        @Override
        public void saveFromResponse(HttpUrl httpUrl, List<Cookie> list) {
            cookieStore.put(httpUrl.host(), list);
        }

        @Override
        public List<Cookie> loadForRequest(HttpUrl httpUrl) {
            List<Cookie> cookies = cookieStore.get(httpUrl.host());
            return cookies != null ? cookies : new ArrayList<Cookie>();
        }
    }).build();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    public NetConnetcion(Context context) {
        this.context = context;
    }

    String Get(String url) throws IOException {
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful()) {
            return response.body().string();
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

    JSONObject Post(String url, String json) throws IOException, JSONException {
        RequestBody body = RequestBody.create(JSON, json);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Log.d(TAG, body.toString());
        Log.d(TAG, "Post: " + json);
        Response response = client.newCall(request).execute();
        Log.d(TAG, response.toString());

        if (response.isSuccessful()) {
            String responseStr = response.body().string();
            JSONObject responseJson = new JSONObject(responseStr);
            return responseJson;
        } else {
            throw new IOException("Unexpected code " + response);
        }
    }

}
