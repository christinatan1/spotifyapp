package com.example.spotify;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotify.Activities.ComposeActivity;
import com.example.spotify.Activities.MainActivity;
import com.example.spotify.ParseClasses.Post;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;

//import org.parceler.Parcels;

import org.parceler.Parcels;

import java.util.List;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.ViewHolder> {
    private Context context;
    private List<Post> posts;
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

        holder.tvUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, UserProfiles.class);
                i.putExtra(Post.class.getName(), Parcels.wrap(post));
                context.startActivity(i);
            }
        });

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

    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void clear() {
        posts.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<Post> post) {
        posts.addAll(post);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private ImageView ivImage;
        private TextView tvDescription;
        private TextView tvTime;
        private ImageView ivProfile;
        private TextView numLikes;
        private ImageButton like;
        private TextView descriptionSpotify;
        private LinearLayout descriptionSpotifyLayout;

        // create references to views for easy access later
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
//            ivImage = itemView.findViewById(R.id.ivImage);
            tvDescription = itemView.findViewById(R.id.tvDescription);
//            tvTime = itemView.findViewById(R.id.tvTime);
            ivProfile = itemView.findViewById(R.id.ivProfile);
            numLikes = itemView.findViewById(R.id.numLikes);
            like = itemView.findViewById(R.id.like);
            descriptionSpotify = itemView.findViewById(R.id.descriptionSpotify);
        }

        public void bind(Post post) {
            // bind the post data to the view elements
            tvDescription.setText(post.getDescription());
            tvUsername.setText(post.getUser().getUsername());
            numLikes.setText(String.valueOf(post.getLikes()) + " likes");
//            ParseFile image = post.getImage();
//            if (image != null) {
//                Glide.with(context).load(image.getUrl()).into(ivImage);
//            }
            String spotifyDescription = post.getDescriptionSpotify();
            if (spotifyDescription != null){
                descriptionSpotify.setText(spotifyDescription);
                descriptionSpotify.setVisibility(View.VISIBLE);;
            }
            ParseFile profilePhoto = post.getUser().getParseFile("profilePicture");
            if (profilePhoto != null){
                Glide.with(context).load(profilePhoto.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfile);
            }
        }
    }
}