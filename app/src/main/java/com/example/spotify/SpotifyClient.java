package com.example.spotify;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotify.Activities.MainActivity;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import retrofit.http.GET;
import retrofit.http.Headers;
import retrofit.http.Path;

public class SpotifyClient {

    private static final String GET_CURRENT_TRACK_URL = "https://api.spotify.com/v1/me/player";
    private RequestQueue queue;
    public static String ACCESS_TOKEN;

    public SpotifyClient(Context context, String access_token) {
        queue = Volley.newRequestQueue(context);
        ACCESS_TOKEN = access_token;
    }

    public Map<String, String> getCurrentTrack() {

//        Response.ErrorListener errorListener = new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                Log.d("SpotifyClient", error.toString());
//            }
//
//            {
//                @Override
//                public Map<String, String> getHeaders () throws AuthFailureError {
//                Map<String, String> headers = new HashMap<>();
//                String auth = "Bearer " + ACCESS_TOKEN;
//                headers.put("Authorization: ", auth);
//                return headers;
//            }
//            }
//        };
//
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GET_CURRENT_TRACK_URL, null, listener,errorListener);
//        queue.add(jsonObjectRequest);
        
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,GET_CURRENT_TRACK_URL, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject getResponse) {
                        Log.i("VOLLEY", getResponse.toString());
                        String response = getResponse.toString();

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("TAG", error.getMessage(), error);
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                String auth = "Bearer " + ACCESS_TOKEN;
                headers.put("Authorization: ", auth);
                return headers;
            }

        };
        queue.add(jsonObjectRequest);

        Map<String, String> output = new HashMap<>();

        // add artist + song name to output
        return output;
    }


//    public void getCurrentTrack2(){
//        OkHttpClient client = new OkHttpClient();
//
//        Request request = new Request.Builder()
//                .header("Authorization", ACCESS_TOKEN)
//                .url(GET_CURRENT_TRACK_URL)
//                .build();
//    }

}

/* 1. Define the endpoint URL with getApiUrl and pass a relative path to the endpoint
 * 	  i.e getApiUrl("statuses/home_timeline.json");
 * 2. Define the parameters to pass to the request (query or body)
 *    i.e RequestParams params = new RequestParams("foo", "bar");
 * 3. Define the request method and make a call to the client
 *    i.e client.get(apiUrl, params, handler);
 *    i.e client.post(apiUrl, params, handler);
 */
