package com.example.spotify.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.spotify.ParseClasses.Post;
import com.example.spotify.Adapters.PostsAdapter;
import com.example.spotify.R;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView rvPosts;
    public static final String TAG = "HomeFragment";
    protected PostsAdapter adapter;
    protected List<Post> allPosts;
    private SwipeRefreshLayout swipeContainer;
    private TextView noFollowing;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public HomeFragment() {
        // required empty public constructor
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // create references to views for easy access later
        rvPosts = view.findViewById(R.id.rvPosts);
        swipeContainer = view.findViewById(R.id.swipeContainer);
        noFollowing = view.findViewById(R.id.noFollowing);

        // initialize the array that will hold posts and create a PostsAdapter
        allPosts = new ArrayList<>();
        adapter = new PostsAdapter(getContext(), allPosts);
        rvPosts.setVisibility(View.VISIBLE);

        // setup refresh listener when user swipes up to refresh, which triggers new data loading
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                fetchTimelineAsync(0);
            }
        });

        // set the adapter on the recycler view
        rvPosts.setAdapter(adapter);
        // set the layout manager on the recycler view
        rvPosts.setLayoutManager(new LinearLayoutManager(getContext()));

        queryPosts(0);
    }

    public void fetchTimelineAsync(int page) {
        // send the network request to fetch the updated data
        queryPosts(1);
        // CLEAR OUT old items before appending in the new ones
        // call setRefreshing(false) to signal refresh has finished
        swipeContainer.setRefreshing(false);
    }


    protected void queryPosts(int i) {
        // specify what type of data we want to query - Post.class
        ParseQuery<Post> query = ParseQuery.getQuery(Post.class);
        // include data referred by user key
        query.include(Post.KEY_USER);

        ParseUser currentUser = ParseUser.getCurrentUser();
        List<String> following = currentUser.getList("usersFollowing");
        Log.d(TAG, String.valueOf(following));

        if (following != null) {
            query.whereContainsAll("userID", following);
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
                    if (i == 1) {
                        // clear adapter when user swipes up to refresh
                        adapter.clear();
                    }

                    // save received posts to list and notify adapter of new data, invalidates existing items and rebinds data
                    allPosts.addAll(posts);
                    adapter.notifyDataSetChanged();

                    // show notice that user is not following anyone
                    if (allPosts.size() == 0){
                        swipeContainer.setVisibility(View.GONE);
                        rvPosts.setVisibility(View.GONE);
                        noFollowing.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}