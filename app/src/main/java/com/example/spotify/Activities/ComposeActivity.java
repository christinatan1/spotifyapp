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

import com.example.spotify.ExternalLibraries.LyricsClient;
import com.example.spotify.ParseClasses.Post;
import com.example.spotify.R;
import com.example.spotify.ExternalLibraries.SpotifyClient;
import com.example.spotify.ExternalLibraries.VolleyCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

public class ComposeActivity extends AppCompatActivity {

    private EditText etDescription;
    private Button btnSubmit;
    private RadioButton currentSong;
    private RadioButton topSong;
    private String description_topSong;
    private String description_currentSong;
    private String descriptionSpotify;
    private String songUrl_current;
    private String songUrl_top;
    private String songUrl;

    public String client_current_artist;
    public String client_current_song;

    public String client_top_artist;
    public String client_top_song;

    private boolean checkCurrent = false;
    private boolean checkTop = false;
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
        submitListener();

        // get spotify info, fill it in in radio buttons
        setButtons();

        // display lyrics depending on which song is chosen
//        onRadioButtonClicked(currentSong);
//        onRadioButtonClicked(topSong);

        // get access token after connecting in main activity
        // ACCESS_TOKEN = Parcels.unwrap(getIntent().getParcelableExtra("user"));
        Log.d(TAG, ACCESS_TOKEN);
    }


    // user - post on timeline, runs once submit button is clicked
    private void submitListener() {
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

                descriptionSpotify = onRadioButtonSelected(topSong);
                descriptionSpotify = onRadioButtonSelected(currentSong);

                if (descriptionSpotify != null){
                    savePostSpotify(description, descriptionSpotify, currentUser);
                } else {
                    savePost(description, currentUser);
                }
            }
        });
    }

    // method called once user navigates to compose, fills in text in buttons
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
                                // get info from client, set it as a global variable in compose activity for other methods
                                client_current_song = client.current_song;
                                client_current_artist = client.current_artist;
                                songUrl_current = client.current_song_url;
                                description_currentSong = "Current Song Playing: " + client.current_song + ", " + client.current_artist;
                                currentSong.setText(description_currentSong);
                            }
                        });

                        client.getTopTrack(new VolleyCallback(){
                            @Override
                            public void onSuccess(){
                                // get info from client, set it as a global variable in compose activity for other methods
                                client_top_artist = client.top_artist;
                                client_top_song = client.top_song;
                                songUrl_top = client.top_song_url;
                                description_topSong = "Top Song This Month: " + client.top_song + ", " + client.top_artist;
                                topSong.setText(description_topSong);
                            }
                        });
                    }

                    public void onFailure(Throwable throwable) {
                        // Something went wrong when attempting to connect, Handle errors here
                        Log.e(TAG, throwable.getMessage(), throwable);
                    }
                });
    }

    // check which button was clicked once submit button is clicked
    public String onRadioButtonSelected(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.currentSong:
                if (checked) {
                    return description_currentSong;
                }
                    break;
            case R.id.topSong:
                if (checked) {
                    return description_topSong;
                }
                    break;
        }
        return null;
    }

    // displays lyrics if one of the songs (buttons) is clicked
    public String onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.currentSong:
                if (checked) {
                    songUrl = songUrl_current;
                    String description = getLyrics(client_current_artist, client_current_song);
                    currentSong.setText(description_currentSong + description);
                    return description_currentSong;
                }
                break;
            case R.id.topSong:
                if (checked) {
                    songUrl = songUrl_top;
                    String description = getLyrics(client_top_artist, client_top_song);
                    topSong.setText(description_topSong + description);
                    return description_topSong;
                }
                break;
        }
        return null;
    }

    private String getLyrics(String artist, String title){
        LyricsClient client = new LyricsClient(ComposeActivity.this);

        client.getLyrics(artist, title, new VolleyCallback() {
            @Override
            public void onSuccess() {
                Log.d(TAG, "Success!");
            }
        });
        return client.lyrics;
    }

    // user has choice whether or not to post with or without spotify info
    // creates post w/o extra spotify info, regular post
    private void savePost(String description, ParseUser currentUser) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(currentUser);

//        post.setSongUrl();
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

    // creates post w/ extra spotify info
    private void savePostSpotify(String description, String spotifyDescription, ParseUser currentUser) {
        Post post = new Post();
        post.setDescription(description);
        post.setDescriptionSpotify(spotifyDescription);
        post.setUser(currentUser);

        post.setSongUrl(songUrl);

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