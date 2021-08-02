package com.example.spotify.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotify.ParseClasses.Post;
import com.example.spotify.ParseClasses.User;
import com.example.spotify.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

public class UserProfilesActivity extends AppCompatActivity {

    private TextView tvUsername;
    private ImageView ivProfilePic;
    private TextView followerCount;
    private TextView followingCount;
    private TextView topUserSongs;
    private TextView compatibilityScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profiles);

        // create references to views for easy access later
        tvUsername = findViewById(R.id.tvUsername);
        followerCount = findViewById(R.id.followerCount);
        followingCount = findViewById(R.id.followingCount);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        topUserSongs = findViewById(R.id.topUserSongs);
        compatibilityScore = findViewById(R.id.compatibilityScore);

        // user parse class
        ParseUser postUser = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getName()));
        ParseUser currentUser = ParseUser.getCurrentUser();

        // get backend info
        followerCount.setText(String.valueOf(postUser.getInt("followers")));
        followingCount.setText(String.valueOf(postUser.getInt("following")));
        tvUsername.setText(postUser.getUsername());

        // get profile picture, if it exists
        ParseFile parseProfilePic = postUser.getParseFile("profilePicture");
        if (parseProfilePic != null) {
            Glide.with(this).load(parseProfilePic.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfilePic);
        }

        // fill view with songs and calculate compatibility
        topUserSongs.setText(getTopSongs(postUser, currentUser));

        // calculate score and display
        compatibilityScore.setText("Compatibility Score: " + calculateScore());

    }

    private String calculateScore() {
        return null;
    }

    private String getTopSongs(ParseUser postUser, ParseUser currentUser){

        String output = "";
        List postUserSongs = postUser.getList("topSongs");
        List postUserSongArtists = postUser.getList("topSongArtists");
        List currentUserSongs = currentUser.getList("topSongs");
        List currentUserSongArtists = currentUser.getList("topSongArtists");

        // print top 10 songs from user's spotify
        for (int i = 0; i < postUserSongArtists.size(); i++){
            output += (Integer.toString(i+1) + ". " + postUserSongs.get(i) + " by " + postUserSongArtists.get(i) + "\n\n");
        }

//        for (int i = 0; i < postUserSongs.size(); i++){
//            for (int j = 0; j < currentUserSongs.size(); i++){
//
//            }
//        }

        return output;
    }
}