package com.example.spotify.Adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotify.Activities.UserProfilesActivity;
import com.example.spotify.ExternalLibraries.OnDoubleTapListenerOne;
import com.example.spotify.ParseClasses.Post;
import com.example.spotify.R;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

//import org.parceler.Parcels;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {

    // spotify info
    private static final String CLIENT_ID = "cae795cea2f94211bce48b701c1cfa40";
    private static final String REDIRECT_URI = "com.example.spotify://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    private Context context;
    private List<Post> posts;
    private TextView songArtist;
    private TextView songTitle;
    public static final String TAG = "PostsAdapter";

    // constructor
    public PostsAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    // for every visible item, inflate (create) a view
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        songArtist = parent.findViewById(R.id.songArtist);
        songTitle = parent.findViewById(R.id.songTitle);

        // pass in a blueprint of the xml file
        View view = LayoutInflater.from(context).inflate(R.layout.item_post, parent, false);
        // wrap a view into a view holder for easy access
        return new ViewHolder(view);
    }

    @Override
    // called when recycler view needs to show an item
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // get the post at current position
        Post post = posts.get(position);
        holder.bind(post);

        // go to post author's profile if current user clicked on the post author's username
        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UserProfilesActivity.class);

                i.putExtra(Post.class.getName(), Parcels.wrap(post.getUser()));
                context.startActivity(i);
            }
        });

        // see if user liked a post, immediately update the number of likes
        holder.like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                post.addLikes();
                holder.numLikes.setText(String.valueOf(post.getLikes()) + " likes");
                holder.like.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, "Error while saving", e);
                        }
                        else {
                            Log.i(TAG, "Post save was successful!");
                        }
                    }
                });
            }
        });

        // if user pressed play on a post's song, play the song
        holder.ibPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get song from parse
                String song = post.getSongUrl();

                // connect to spotify and play song
                play(song);

                // update song player details
                Log.d(TAG, post.getSongTitle());
            }
        });

        // if user double tapped a post, add a like and immediately update the number of likes
        holder.tvDescription.setOnTouchListener(new OnDoubleTapListenerOne(holder.tvDescription.getContext()) {
            @Override
            public void onDoubleTap(MotionEvent e) {
                Log.d("PostsAdapter", "success");
                post.addLikes();
                holder.numLikes.setText(String.valueOf(post.getLikes()) + " likes");
                holder.like.setBackgroundResource(R.drawable.ic_baseline_favorite_24);
                post.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e != null){
                            Log.e(TAG, "Error while saving", e);
                        }
                        else {
                            Log.i(TAG, "Post save was successful!");
                        }
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // add a list of items
    public void addAll(List<Post> post) {
        posts.addAll(post);
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvTime;
        private ImageView ivProfile;
        private TextView numLikes;
        private ImageButton like;
        private TextView descriptionSpotify;
        private LinearLayout descriptionSpotifyLayout;
        private ImageButton ibPlay;
        private ImageButton ibPlayBottom;
        private ImageButton ibPause;
        private TextView songArtist;
        private TextView songTitle;
        private ImageView albumCover;

        // create references to views for easy access later
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            numLikes = itemView.findViewById(R.id.numLikes);
            like = itemView.findViewById(R.id.like);
            descriptionSpotify = itemView.findViewById(R.id.descriptionSpotify);
            ibPlay = itemView.findViewById(R.id.ibPlay);
            ibPlayBottom = itemView.findViewById(R.id.ibPlayBottom);
            ibPause = itemView.findViewById(R.id.ibPause);
            albumCover = itemView.findViewById(R.id.albumCover);
        }

        public void bind(Post post) {
            // bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            numLikes.setText(String.valueOf(post.getLikes()) + " likes");
            String spotifyDescription = post.getDescriptionSpotify();
            if (spotifyDescription != null){
                descriptionSpotify.setText(spotifyDescription);
                descriptionSpotify.setVisibility(View.VISIBLE);
                ibPlay.setVisibility(View.VISIBLE);
                albumCover.setVisibility(View.VISIBLE);
                String albumCoverUrl = post.getSongCover();
                Glide.with(context).load(albumCoverUrl).into(albumCover);
            }
            ParseFile profilePhoto = post.getUser().getParseFile("profilePicture");
            if (profilePhoto != null){
                Glide.with(context).load(profilePhoto.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfile);
            }
        }
    }

    private void play(String song) {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        if (SpotifyAppRemote.isSpotifyInstalled(context)) {
            Log.d("Posts Adapter", "Connected! Yay!");
        }

        SpotifyAppRemote.connect(context, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;
                        Log.d(TAG, "Connected! Yay!");
                        mSpotifyAppRemote.getPlayerApi().play(song);
                    }

                    public void onFailure(Throwable throwable) {
                        // something went wrong when attempting to connect, Handle errors here
                        Log.e(TAG, throwable.getMessage(), throwable);
                    }
                });
    }
}