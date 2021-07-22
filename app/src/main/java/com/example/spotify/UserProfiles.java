package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.spotify.ParseClasses.Post;
import com.example.spotify.ParseClasses.User;

import org.parceler.Parcel;
import org.parceler.Parcels;

public class UserProfiles extends AppCompatActivity {

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

//        Post post = Parcels.unwrap(getIntent().getParcelableExtra(Post.class.getName()));
//        followerCount.setText(post.getUser().getInt("followers"));
//        followingCount.setText(post.getUser().getInt("following"));
//        tvUsername.setText(post.getUser().getUsername());

        // set profile picture
    }
}