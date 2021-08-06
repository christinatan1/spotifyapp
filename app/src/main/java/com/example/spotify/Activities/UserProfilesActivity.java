package com.example.spotify.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotify.ParseClasses.Post;
import com.example.spotify.ParseClasses.User;
import com.example.spotify.R;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.parceler.Parcel;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UserProfilesActivity extends AppCompatActivity {

    private TextView tvUsername;
    private ImageView ivProfilePic;
    private TextView followerCount;
    private TextView followingCount;
    private TextView topUserSongs;
    private TextView compatibilityScore;
    private TextView location;
    private TextView topGenre;
    private ImageButton btnFollow;
    private ImageButton btnBack;
    private TextView tvName;
    public static final String TAG = "UserProfilesActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profiles);

        // create references to views for easy access later
        tvUsername = findViewById(R.id.tvUsername);
        followerCount = findViewById(R.id.followerCount);
        followingCount = findViewById(R.id.followingCount);
        ivProfilePic = findViewById(R.id.ivProfilePic);
        topUserSongs = findViewById(R.id.topSongs);
        compatibilityScore = findViewById(R.id.compatibilityScore);
        tvName = findViewById(R.id.tvName);
        location = findViewById(R.id.location);
        topGenre = findViewById(R.id.topGenre);
        btnFollow = findViewById(R.id.btnFollow);
        btnBack = findViewById(R.id.btnBack);
        btnFollow.setClickable(true);
        btnFollow.setPressed(false);

        // user parse class
        ParseUser postUser = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getName()));
        ParseUser currentUser = ParseUser.getCurrentUser();

        // get backend info
        int numFollowers = postUser.getInt("followers");
        tvUsername.setText("@" + postUser.getUsername());
        tvName.setText(postUser.getString("name"));
        location.setText(postUser.getString("location"));
        topGenre.setText(postUser.getString("topGenres"));

        // get profile picture, if it exists
        ParseFile parseProfilePic = postUser.getParseFile("profilePicture");
        if (parseProfilePic != null) {
            Glide.with(this).load(parseProfilePic.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfilePic);
        } else if (currentUser.getString("spotifyProfilePicture") != null){
            Glide.with(this).load(currentUser.getString("spotifyProfilePicture")).apply(RequestOptions.circleCropTransform()).into(ivProfilePic);
        }

        // fill view with songs and calculate compatibility
        topUserSongs.setText(getTopSongs(postUser, currentUser));

        // calculate score and display
        compatibilityScore.setText("Compatibility Score: " + String.valueOf(calculateScore(postUser, currentUser)) + "%");

        // add a follower when follow button is pressed
        btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentUser.put("following", postUser.getInt("following")+1 );
                currentUser.addAllUnique("usersFollowing", Arrays.asList(postUser.getObjectId()));

                btnFollow.setPressed(true);
                btnFollow.setEnabled(true);
                btnFollow.setImageResource(R.drawable.ic_round_check_circle_outline_24);
                btnFollow.setBackgroundResource(R.drawable.transparent_button_border);
                btnFollow.setBackgroundColor(Color.parseColor("#ffffff"));

                currentUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, "Error while saving", e);
                        }
                        else {
                            Log.i(TAG, "Save was successful!");
                        }
                    }
                });
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserProfilesActivity.super.onBackPressed();
            }
        });
    }

    private int calculateScore(ParseUser postUser, ParseUser currentUser) {
        int compatScore = 0;

        // get user info to compare
        List postUserSongs = postUser.getList("topSongs");
        List currentUserSongs = currentUser.getList("topSongs");
        String postUserLocation = postUser.getString("location");
        String currentUserLocation = currentUser.getString("location");
        String postUserGenre = postUser.getString("topGenres");
        String currentUserGenre = currentUser.getString("topGenres");

        if (postUserLocation.equals(currentUserLocation)){
            compatScore += 10;
        }

        if (postUserGenre.equals(currentUserGenre)){
            compatScore += 10;
        }

        // if order matches up - full 10 points
        for (int i = 0; i < postUserSongs.size(); i++){
            if (postUserSongs.get(i).equals(currentUserSongs.get(i))){
                compatScore += 10;
            }
        }

        // if order does not match up but there are similar songs,
        // calculate weight based on order
        for (int i = 0; i < postUserSongs.size(); i++){
            for (int j = 0; j < currentUserSongs.size(); j++){
                if (i != j && (postUserSongs.get(i).equals(currentUserSongs.get(j)))){
                    int orderPostUser = (10 - i);
                    int orderCurrentUser = (10 - j);
                    int average = (orderPostUser + orderCurrentUser)/2;
                    compatScore += average;
                }
            }
        }

        return compatScore;
    }

    private String getTopSongs(ParseUser postUser, ParseUser currentUser){
        String output = "";

        // get user info to compare
        List postUserSongs = postUser.getList("topSongs");
        List postUserSongArtists = postUser.getList("topSongArtists");
        List currentUserSongs = currentUser.getList("topSongs");
        List currentUserSongArtists = currentUser.getList("topSongArtists");

        // print top 8 songs from post user's spotify
        for (int i = 0; i < postUserSongArtists.size() - 1; i++){
            output += (Integer.toString(i+1) + ". " + postUserSongs.get(i) + " by " + postUserSongArtists.get(i) + "\n\n");
        }
        output += (Integer.toString(postUserSongArtists.size()) + ". " + postUserSongs.get(postUserSongArtists.size()-1) + " by " + postUserSongArtists.get(postUserSongArtists.size()-1));

        return output;
    }
}