package com.example.spotify.ParseClasses;

import com.parse.Parse;
import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.parceler.Parcel;

@ParseClassName("User")
public final class User extends ParseUser {

    public static final String KEY_PROFILE = "profilePicture";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_CREATED_KEY = "createdAt";
    public static final String KEY_SONG = "songPlaying";
    public static final String KEY_USER = "user";

//    public void setUser(ParseUser currentUser){
//        user = currentUser;
//    }

//    public String getUsername(){
//        return getString(KEY_USERNAME);
//    }
    public int getFollowers() { return getInt(KEY_FOLLOWERS); }
    public int getFollowing() { return getInt(KEY_FOLLOWING); }
    public ParseFile getProfile() { return getParseFile(KEY_PROFILE); }

    public void setSongPlaying(String song){ put(KEY_SONG, song); }

}
