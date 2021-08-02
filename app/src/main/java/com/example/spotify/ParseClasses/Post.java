package com.example.spotify.ParseClasses;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

import java.util.Date;

@ParseClassName("Post")

public class Post extends ParseObject {

    public static final String KEY_DESCRIPTION = "description";
    public static final String KEY_DESCRIPTION_SPOTIFY = "descriptionSpotify";
    public static final String KEY_SONG_URL = "songUrl";
    public static final String KEY_IMAGE = "image";
    public static final String KEY_USER = "user";
    public static final String KEY_CREATED_KEY = "createdAt";
    public static final String KEY_PROFILE = "profilePicture";
    public static final String KEY_LIKES = "likes";
    public static final String KEY_SONG_TITLE = "songTitle";
    public static final String KEY_SONG_ARTIST = "songArtist";
    public static final String KEY_COVER = "albumCover";

    public String getDescription(){
        return getString(KEY_DESCRIPTION);
    }
    public void setDescription(String description){
        put(KEY_DESCRIPTION, description);
    }

    public void setDescriptionSpotify(String description){ put(KEY_DESCRIPTION_SPOTIFY, description); }
    public String getDescriptionSpotify() { return getString(KEY_DESCRIPTION_SPOTIFY); }

    public String getSongUrl() { return getString(KEY_SONG_URL); }
    public void setSongUrl(String url) { put(KEY_SONG_URL, url); }

    public ParseFile getImage(){
        return getParseFile(KEY_IMAGE);
    }
    public void setImage(ParseFile parseFile){
        put(KEY_IMAGE, parseFile);
    }

    public ParseUser getUser(){
        return getParseUser(KEY_USER);
    }
    public void setUser(ParseUser user){ put(KEY_USER, user); }

    public ParseFile getProfile() { return getParseFile(KEY_PROFILE); }

    public String getSongCover() { return getString(KEY_COVER); }
    public void setSongCover(String url) { put(KEY_COVER, url); }

    public int getLikes() {return getInt(KEY_LIKES); }
    public void addLikes(){ put(KEY_LIKES, getLikes()+1); }

    public String getSongTitle() { return getString(KEY_SONG_TITLE); }
    public void setSongTitle(String song) { put(KEY_SONG_TITLE, song); }

    public String getSongArtist() { return getString(KEY_SONG_ARTIST); }
    public void setSongArtist(String artist) { put(KEY_SONG_ARTIST, artist); }
    public String getSongAlbumCover() { return getString(KEY_COVER); }

    // calculate time since user has posted/updated something
    public static String calculateTimeAgo(Date createdAt) {

        int SECOND_MILLIS = 1000;
        int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        int DAY_MILLIS = 24 * HOUR_MILLIS;

        try {
            createdAt.getTime();
            long time = createdAt.getTime();
            long now = System.currentTimeMillis();

            final long diff = now - time;
            if (diff < MINUTE_MILLIS) {
                return "just now";
            } else if (diff < 2 * MINUTE_MILLIS) {
                return "a minute ago";
            } else if (diff < 50 * MINUTE_MILLIS) {
                return diff / MINUTE_MILLIS + " m";
            } else if (diff < 90 * MINUTE_MILLIS) {
                return "an hour ago";
            } else if (diff < 24 * HOUR_MILLIS) {
                return diff / HOUR_MILLIS + " h";
            } else if (diff < 48 * HOUR_MILLIS) {
                return "yesterday";
            } else {
                return diff / DAY_MILLIS + " d";
            }
        } catch (Exception e) {
            Log.i("Error:", "getRelativeTimeAgo failed", e);
            e.printStackTrace();
        }

        return "";
    }
}
