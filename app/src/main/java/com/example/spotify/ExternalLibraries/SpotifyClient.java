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
    private static final String GET_TOP_TRACK_URL = "https://api.spotify.com/v1/me/top/tracks?time_range=short_term&limit=20";
    private static final String GET_TRACK = "https://api.spotify.com/v1/me/top/tracks?time_range=short_term";
    private RequestQueue queue;
    public static String ACCESS_TOKEN;

    // 0: song title, 1: artist, 2: song url, 3: album cover
    public String[] current_song = new String[10];
    public String[] top_song = new String[10];

    public String[] user_top_songs = new String[8];
    public String[] user_top_song_artists = new String[8];

    public SpotifyClient(Context context, String access_token) {
        queue = Volley.newRequestQueue(context);
        ACCESS_TOKEN = access_token;
    }

    // gets current playing song, will return null strings if user is not playing a song
    public void getCurrentTrack(VolleyCallback callback) {
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, GET_CURRENT_TRACK_URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject getResponse) {
                        Log.i("VOLLEY", getResponse.toString());
                        try {
                            // get correct data that we want after getting response back
                            current_song[0] = getResponse.getJSONObject("item").getString("name");
                            current_song[1] = getResponse.getJSONObject("item").getJSONObject("album").getJSONArray("artists").getJSONObject(0).getString("name");
                            current_song[2] = getResponse.getJSONObject("item").getString("uri");
                            current_song[3] = getResponse.getJSONObject("item").getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");

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
                }) {
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

    // gets top song of the month, time range is "short term" in spotify url, which is roughly a month
    public void getTopTrack(VolleyCallback callback){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,GET_TOP_TRACK_URL, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject getResponse) {
                        Log.i("VOLLEY", getResponse.toString());
                        try {
                            // get correct data that we want after getting response back
                            top_song[0] = getResponse.getJSONArray("items").getJSONObject(0).getString("name");
                            top_song[1] = getResponse.getJSONArray("items").getJSONObject(0).getJSONArray("artists").getJSONObject(0).getString("name");
                            top_song[2] = getResponse.getJSONArray("items").getJSONObject(0).getString("uri");
                            top_song[3] = getResponse.getJSONArray("items").getJSONObject(0).getJSONObject("album").getJSONArray("images").getJSONObject(0).getString("url");

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

    // gets user's top ten songs
    public void getUserTopSongs(VolleyCallback callback){
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,GET_TOP_TRACK_URL, null,
                new Response.Listener<JSONObject>(){
                    @Override
                    public void onResponse(JSONObject getResponse) {
                        Log.i("VOLLEY", getResponse.toString());
                        try {
                            // get top 8 songs to save in parse
                            for (int i = 0; i < 8; i++){
                                user_top_songs[i] = getResponse.getJSONArray("items").getJSONObject(i).getString("name");
                                user_top_song_artists[i] = getResponse.getJSONArray("items").getJSONObject(i).getJSONArray("artists").getJSONObject(0).getString("name");
                                Log.d("Spotify Client", user_top_songs[i]);
                            }

                            // call to interface after getting data
                            callback.onSuccess();

//                            Log.d("Spotify Client", user_top_songs[0]);
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
