package com.example.spotify.ParseClasses;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject {

    public static final String KEY_PROFILE = "profilePicture";
    public static final String KEY_FOLLOWERS = "followers";
    public static final String KEY_FOLLOWING = "following";


    public int getFollowers() { return getInt(KEY_FOLLOWERS); }
    public int getFollowing() { return getInt(KEY_FOLLOWING); }

    public ParseFile getProfile() { return getParseFile(KEY_PROFILE); }

}
