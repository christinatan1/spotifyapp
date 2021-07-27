package com.example.spotify.ExternalLibraries;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.spotify.ExternalLibraries.VolleyCallback;

import org.json.JSONException;
import org.json.JSONObject;

public class LyricsClient {

    private RequestQueue queue;
    private static final String GET_LYRICS = "https://api.lyrics.ovh/v1/"; // + artist + song
    public String lyrics;

    public LyricsClient(Context context) {
        queue = Volley.newRequestQueue(context);
    }

    public void getLyrics(String artist, String title, VolleyCallback callback) {
        artist = artist.replaceAll("\\s", "");
        title = title.replaceAll("\\s", "");
        String GET_URL = GET_LYRICS + artist + "/" + title;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,GET_URL, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject getResponse) {
                        Log.i("VOLLEY", getResponse.toString());
                        try {
                            // get correct data that we want after getting response back
                            lyrics = getResponse.getString("lyrics");

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
                });

        queue.add(jsonObjectRequest);
    }
}
