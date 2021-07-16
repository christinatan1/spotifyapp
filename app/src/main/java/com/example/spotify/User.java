package com.example.spotify;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;

@ParseClassName("User")
public class User extends ParseObject {

    public static final String KEY_PROFILE = "profilePicture";

    public ParseFile getProfile() { return getParseFile(KEY_PROFILE); }

}
