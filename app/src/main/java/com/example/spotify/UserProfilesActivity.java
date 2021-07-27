package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotify.ParseClasses.Post;
import com.example.spotify.ParseClasses.User;
import com.parse.ParseFile;

import org.parceler.Parcel;
import org.parceler.Parcels;

public class UserProfilesActivity extends AppCompatActivity {

    private TextView tvUsername;
    private ImageView ivProfilePic;
    private TextView followerCount;
    private TextView followingCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profiles);

        tvUsername = findViewById(R.id.tvUsername);
        followerCount = findViewById(R.id.followerCount);
        followingCount = findViewById(R.id.followingCount);
        ivProfilePic = findViewById(R.id.ivProfilePic);

        Post post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getName()));
        Log.d("UserProfiles", post.getUser().getUsername());
        followerCount.setText(String.valueOf(post.getUser().getInt("followers")));
        followingCount.setText(String.valueOf(post.getUser().getInt("following")));
        tvUsername.setText(post.getUser().getUsername());

        // set profile picture

        ParseFile parseProfilePic = post.getUser().getParseFile("profilePicture");
        if (parseProfilePic != null) {
            Glide.with(this).load(parseProfilePic.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfilePic);
        }

    }
}