package com.example.spotify.ExternalLibraries;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class SpotifyClient {

    private static final String GET_CURRENT_TRACK_URL = "https://api.spotify.com/v1/me/player";
    private static final String GET_TOP_TRACK_URL = "https://api.spotify.com/v1/me/top/tracks?time_range=short_term";
    private RequestQueue queue;
    public static String ACCESS_TOKEN;

    public String current_artist;
    public String current_song;

    public String top_artist;
    public String top_song;

    public SpotifyClient(Context context, String access_token) {
        queue = Volley.newRequestQueue(context);
        ACCESS_TOKEN = access_token;
    }

    public void getCurrentTrack(VolleyCallback callback) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,GET_CURRENT_TRACK_URL, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject getResponse) {
                        Log.i("VOLLEY", getResponse.toString());
                        try {
                            // get correct data that we want after getting response back
                            current_artist = getResponse.getJSONObject("item").getJSONObject("album").getJSONArray("artists").getJSONObject(0).getString("name");
                            current_song = getResponse.getJSONObject("item").getString("name");

                            // call to interface after getting data
                            callback.onSuccess();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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

    }


    public void getTopTrack(VolleyCallback callback){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,GET_TOP_TRACK_URL, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject getResponse) {
                        Log.i("VOLLEY", getResponse.toString());
                        try {
                            // get correct data that we want after getting response back
                            top_artist = getResponse.getJSONArray("items").getJSONObject(0).getJSONArray("artists").getJSONObject(0).getString("name");
                            top_song = getResponse.getJSONArray("items").getJSONObject(0).getString("name");

                            // call to interface after getting data
                            callback.onSuccess();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
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
    }
}
