package com.example.spotify.Fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spotify.Adapters.CardsAdapter;
import com.example.spotify.ParseClasses.Post;
import com.example.spotify.ParseClasses.User;
import com.example.spotify.R;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DashboardFragment extends Fragment {
    private RecyclerView rvCards;
    public static final String TAG = "DashboardFragment";
    protected CardsAdapter adapter;
    protected List<ParseUser> allUsers;


    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    public DashboardFragment() {
        // Required empty public constructor
    }

    public static DashboardFragment newInstance(String param1, String param2) {
        DashboardFragment fragment = new DashboardFragment();
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
//        return inflater.inflate(R.layout.fragment_dashboard, container, false);
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        rvCards = view.findViewById(R.id.rvUserCards);

        // initialize the array that will hold posts and create a PostsAdapter
        allUsers = new ArrayList<>();
        adapter = new CardsAdapter(getContext(), allUsers);

        // set the adapter on the recycler view
        rvCards.setAdapter(adapter);
        // set the layout manager on the recycler view
//        rvCards.setLayoutManager(new LinearLayoutManager(getContext()));
        rvCards.setLayoutManager(new GridLayoutManager(getContext(), 2));

        queryPosts();
    }

    private void queryPosts() {

//        ParseQuery<ParseUser> query = ParseUser.getQuery();
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.include(User.KEY_USER);
        // limit query to latest 20 items
        query.setLimit(20);
        // order posts by creation date (newest first)
//        query.addDescendingOrder(Post.KEY_CREATED_KEY);
        query.findInBackground(new FindCallback<ParseUser>() {
            public void done(List<ParseUser> users, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Issue with getting users", e);
                    return;
                }
//                List<User> userList = new ArrayList<>();
                for (ParseUser user : users) {
                    Log.i(TAG, "User: " + user.getUsername());
//                    userList.add((User) user);
                }
                Log.i(TAG, "Users: " + users);

                // save received posts to list and notify adapter of new data, invalidates existing items and rebinds data
                allUsers.addAll(users);
                adapter.notifyDataSetChanged();
            }
        });

//        ParseQuery<ParseUser> query = ParseUser.getQuery();
//        query.findInBackground(new FindCallback<ParseUser>() {
//            public void done(List<ParseUser> users, ParseException e) {
//                if (e != null) {
//                    Log.e(TAG, "Issue with getting users", e);
//                    return;
//                }
//                for (ParseUser user : users) {
//                    Log.i(TAG, "User: " + user.getUsername());
//
//                }
//
//                // save received posts to list and notify adapter of new data, invalidates existing items and rebinds data
//                allUsers.addAll(users);
//                adapter.notifyDataSetChanged();
//            }
//        });
    }
}




