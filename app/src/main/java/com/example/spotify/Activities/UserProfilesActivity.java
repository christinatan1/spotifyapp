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
    private TextView location;
    private TextView topGenre;


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
        location = findViewById(R.id.location);
        topGenre = findViewById(R.id.topGenre);

        // user parse class
        ParseUser postUser = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getName()));
        ParseUser currentUser = ParseUser.getCurrentUser();

        // get backend info
        followerCount.setText(String.valueOf(postUser.getInt("followers")));
        followingCount.setText(String.valueOf(postUser.getInt("following")));
        tvUsername.setText(postUser.getUsername());
        location.setText("Location: " + postUser.getString("location"));
        topGenre.setText("Top Music Genre: " + postUser.getString("topGenres"));

        // get profile picture, if it exists
        ParseFile parseProfilePic = postUser.getParseFile("profilePicture");
        if (parseProfilePic != null) {
            Glide.with(this).load(parseProfilePic.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfilePic);
        }

        // fill view with songs and calculate compatibility
        topUserSongs.setText(getTopSongs(postUser, currentUser));

        // calculate score and display
        compatibilityScore.setText("Compatibility Score: " + String.valueOf(calculateScore(postUser, currentUser)) + "%");
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

        if (postUserGenre == currentUserGenre){
            compatScore += 10;
        }

        // if order matches up - full 10 points
        for (int i = 0; i < postUserSongs.size(); i++){
            if (postUserSongs.get(i).equals(currentUserSongs.get(i))){
                compatScore += 10;
            }
        }

        // if order does not match up but there are similar songs,
        // calculate weightbased on order
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
        for (int i = 0; i < postUserSongArtists.size(); i++){
            output += (Integer.toString(i+1) + ". " + postUserSongs.get(i) + " by " + postUserSongArtists.get(i) + "\n\n");
        }

        return output;
    }
}