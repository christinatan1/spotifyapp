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
        holder.bind((User) user);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void clear() {
        users.clear();
        notifyDataSetChanged();
    }

    // Add a list of items
    public void addAll(List<ParseUser> user) {
        users.addAll(user);
        notifyDataSetChanged();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView tvUsername;
        private ImageView ivProfilePicture;
        private TextView playingSong;

        // create references to views for easy access later
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tvUsername);
            ivProfilePicture = itemView.findViewById(R.id.ivProfilePicture);
            playingSong = itemView.findViewById(R.id.playingSong);
        }

        public void bind(User user) {
            // bind the post data to the view elements
            tvUsername.setText(user.getUsername());
//            ParseFile profilePhoto = post.getUser().getParseFile("profilePicture");
            //ParseFile profilePhoto = user.getParseFile("profilePicture");
            ParseFile profilePhoto = user.getProfile();
            if (profilePhoto != null){
                Glide.with(context).load(profilePhoto.getUrl()).apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
            }

            playingSong.setText(user.getString("songPlaying"));

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