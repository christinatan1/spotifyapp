package com.example.spotify.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.spotify.ParseClasses.Post;
import com.example.spotify.ParseClasses.User;
import com.example.spotify.R;
import com.example.spotify.SpotifyClient;
import com.example.spotify.VolleyCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import org.parceler.Parcels;

public class ComposeActivity extends AppCompatActivity {

    private EditText etDescription;
    private Button btnSubmit;
    private RadioButton currentSong;
    private RadioButton topSong;
    public static final String TAG = "ComposeActivity";

    // spotify info
    private static final String CLIENT_ID = "cae795cea2f94211bce48b701c1cfa40";
    private static final String REDIRECT_URI = "com.example.spotify://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    // fix this when possible
    public static String ACCESS_TOKEN = MainActivity.ACCESS_TOKEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        currentSong = findViewById(R.id.currentSong);
        topSong = findViewById(R.id.topSong);

        // submit post
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String description = etDescription.getText().toString();
                if (description.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "Description cannot be empty",
                            Toast.LENGTH_SHORT).show();
                    return;
                }
                ParseUser currentUser = ParseUser.getCurrentUser();
                savePost(description, currentUser);
            }
        });

        // get spotify info, fill it in in radio buttons
        setButtons();

        // get access token after connecting in main activity
        // ACCESS_TOKEN = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        Log.d(TAG, ACCESS_TOKEN);
    }

    private void setButtons() {
        ConnectionParams connectionParams =
                new ConnectionParams.Builder(CLIENT_ID)
                        .setRedirectUri(REDIRECT_URI)
                        .showAuthView(true)
                        .build();

        SpotifyAppRemote.connect(ComposeActivity.this, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;

                        SpotifyClient client = new SpotifyClient(ComposeActivity.this, ACCESS_TOKEN);

                        client.getCurrentTrack(new VolleyCallback() {
                            @Override
                            public void onSuccess() {
                                currentSong.setText("Current Song Playing: " + client.current_song + ", " + client.current_artist);
                            }
                        });

                        client.getTopTrack(new VolleyCallback(){
                            @Override
                            public void onSuccess(){
                                topSong.setText("Top Song This Month: " + client.top_song + ", " + client.top_artist);
                            }
                        });
                    }

                    public void onFailure(Throwable throwable) {
                        // Something went wrong when attempting to connect, Handle errors here
                        Log.e(TAG, throwable.getMessage(), throwable);
                    }
                });
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.currentSong:
                if (checked)

                    break;
            case R.id.topSong:
                if (checked)

                    break;
        }
    }

    private void savePost(String description, ParseUser currentUser) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(currentUser);
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null){
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(ComposeActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
                etDescription.setText("");

                Intent i = new Intent(ComposeActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }
}