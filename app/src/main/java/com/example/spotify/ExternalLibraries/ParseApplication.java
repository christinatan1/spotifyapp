package com.example.spotify.ExternalLibraries;

import android.app.Application;

import com.example.spotify.ParseClasses.Post;
import com.example.spotify.ParseClasses.User;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseApplication extends Application {

    // initializes Parse SDK as soon as the application is created
    @Override
    public void onCreate() {
        super.onCreate();

        // register parse models
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(User.class);

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("cnkNfynlCgD8cTd0GLtC0RXm29dM56GeOnmB8e7Z")
                .clientKey("2PSCxZZmVzgP7Hk90DrahxnZHctSuhO7ua3LGwXP")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
