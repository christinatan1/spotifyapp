package com.example.spotify.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotify.ParseClasses.Post;
import com.example.spotify.Activities.UserProfilesActivity;
import com.example.spotify.ParseClasses.User;
import com.example.spotify.R;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.parceler.Parcels;

import java.util.List;

import pl.droidsonroids.gif.GifImageView;

public class CardsAdapter extends RecyclerView.Adapter<CardsAdapter.ViewHolder> {

    private Context context;
    private List<ParseUser> users;
    public static final String TAG = "CardsAdapter";

    // constructor
    public CardsAdapter(Context context, List<ParseUser> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    // for every visible item, inflate (create) a view
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // pass in a blueprint of the xml file
        View view = LayoutInflater.from(context).inflate(R.layout.item_card, parent, false);
        // wrap a view into a view holder for easy access
        return new ViewHolder(view);
    }

    @Override
    // called when recycler view needs to show an item
    public void onBindViewHolder(@NonNull CardsAdapter.ViewHolder holder, int position) {
        // get the post at current position
        ParseUser user = users.get(position);
        holder.bind(user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    // add a list of items
    public void addAll(List<User> user) {
        users.addAll(user);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private TextView tvName;
        private ImageView ivProfilePicture;
        private TextView playingSong;
        private GifImageView musicGif;
        private TextView tvLocation;

        // create references to views for easy access later
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            playingSong = itemView.findViewById(R.id.playingSong);
            musicGif = itemView.findViewById(R.id.musicGif);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvName = itemView.findViewById(R.id.tvName);
        }

        public void bind(ParseUser user) {
            // bind the post data to the view elements
            tvUsername.setText("@" + user.getUsername());
            tvName.setText(user.getString("name"));
            playingSong.setText(user.getString("songPlaying"));
            tvLocation.setText(user.getString("location"));


            ParseFile profilePhoto = user.getParseFile("profilePicture");
            if (profilePhoto != null){
                Glide.with(context).load(profilePhoto.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
            } else if (user.getString("spotifyProfilePicture") != null){
                Glide.with(context).load(user.getString("spotifyProfilePicture")).apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
            }

            if (user.getString("songPlaying") == null || user.getString("songPlaying").equals("")){
                musicGif.setVisibility(View.GONE);
            }
            // go to post author's profile if current user clicked on the post author's username
            tvUsername.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(context, UserProfilesActivity.class);
                    i.putExtra(Post.class.getName(), Parcels.wrap(user));
                    context.startActivity(i);
                }
            });
        }
    }
}
