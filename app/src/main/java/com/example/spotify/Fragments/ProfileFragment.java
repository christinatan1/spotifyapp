package com.example.spotify.Fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.RequestParams;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.spotify.Activities.LoginActivity;
import com.example.spotify.ParseClasses.Post;
import com.example.spotify.ParseClasses.User;
import com.example.spotify.PostsAdapter;
import com.example.spotify.R;
import com.example.spotify.SpotifyClient;
import com.example.spotify.VolleyCallback;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.json.JSONException;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

public class ProfileFragment extends Fragment {
    // spotify info
    private static final String CLIENT_ID = "cae795cea2f94211bce48b701c1cfa40";
    private static final String REDIRECT_URI = "com.example.spotify://callback";
    private SpotifyAppRemote mSpotifyAppRemote;
    public static String ACCESS_TOKEN;
    public static final String TAG = "ProfileFragment";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    protected PostsAdapter adapter;
    protected List<Post> allPosts;

    private Button logoutBtn;
    private Button connectBtn;
    private RecyclerView rvPosts;
    private TextView tvUsername;
    private ImageView ivProfilePic;
    private TextView followerCount;
    private TextView followingCount;
    private TextView currentSong;
    private TextView currentSongArtist;


    public ProfileFragment() {
        // required empty public constructor
    }

    public ProfileFragment(String access_token) {
        ACCESS_TOKEN = access_token;
    }

    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment(ACCESS_TOKEN);
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // connect to xml file
        logoutBtn = view.findViewById(R.id.logoutBtn);
        connectBtn = view.findViewById(R.id.connectBtn);
        ivProfilePic = view.findViewById(R.id.ivProfilePic);
        rvPosts = view.findViewById(R.id.rvPosts);
        tvUsername = view.findViewById(R.id.tvUsername);
        followerCount = view.findViewById(R.id.followerCount);
        followingCount = view.findViewById(R.id.followingCount);
        currentSong = view.findViewById(R.id.currentSong);
        currentSongArtist = view.findViewById(R.id.currentSongArtist);

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                Intent i = new Intent(getContext(), LoginActivity.class);
                startActivity(i);
            }
        });

        connect();

//        connectBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // gets user data and changes the default text to current song info
//                connect();
//            }
//        });

        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        queryPosts(0);

        // **figure out how to convert ParseUser to user class
        ParseUser currentUser = ParseUser.getCurrentUser();
//        ParseFile parseProfilePic = currentUser.getParseFile("profilePicture");
        ParseFile parseProfilePic = currentUser.getParseFile("profilePicture");
        if (parseProfilePic != null) {
            Glide.with(getContext()).load(parseProfilePic.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfilePic);
        }
        tvUsername.setText(currentUser.getUsername());
        followerCount.setText(String.valueOf(currentUser.getInt("followers")));
//        followerCount.setText(String.valueOf(currentUser.getFollowers()));
//        followingCount.setText(String.valueOf(currentUser.getFollowing()));
        followingCount.setText(String.valueOf(currentUser.getInt("following")));

    }

    protected void queryPosts(int i) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);
        query.whereEqualTo(Post.KEY_USER, ParseUser.getCurrentUser());
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
        query.addDescendingOrder(Post.KEY_CREATED_KEY);
        // start an asynchronous call for posts
        query.findInBackground(new FindCallback<Post>() {
            @Override
            public void done(List<Post> posts, ParseException e) {
                // check for errors
                if (e != null) {
                    Log.e(TAG, "Issue with getting posts", e);
                    return;
                }
                for (Post post : posts) {
                    Log.i(TAG, "Post: " + post.getDescription() + ", username: " + post.getUser().getUsername());
                }
                if (i == 1) {
                    adapter.clear();
                }
                // save received posts to list and notify adapter of new data, invalidates existing items and rebinds data
                allPosts.addAll(posts);
                adapter.notifyDataSetChanged();
            }
        });
    }

    private void connect() {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        if (SpotifyAppRemote.isSpotifyInstalled(getContext())) {
            Log.d("Profile Fragment", "Connected! Yay!");
        }

        SpotifyAppRemote.connect(getContext(), connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d(TAG, "Connected! Yay!");
//                        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:37i9dQZF1DX2sUQwD7tbmL");

                        SpotifyClient client = new SpotifyClient(getContext(), ACCESS_TOKEN);
                        client.getCurrentTrack(new VolleyCallback() {
                            @Override
                            public void onSuccess() {
                                currentSong.setText(client.current_song);
                                currentSongArtist.setText(client.current_artist);
                            }
                        });
                    }

                    public void onFailure(Throwable throwable) {
                        // Something went wrong when attempting to connect, Handle errors here
                        Log.e(TAG, throwable.getMessage(), throwable);
                    }
                });
    }

}