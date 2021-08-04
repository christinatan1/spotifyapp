package com.example.spotify.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
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
import com.example.spotify.ParseClasses.User;
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
    private String songArtist;
    private String songTitle;
//    private String songUrl_current;
//    private String songUrl_top;
    private String songUrl;

//    public String client_current_artist;
//    public String client_current_song;
//    public String client_current_album_cover;
//
//    public String client_top_artist;
//    public String client_top_song;
//    public String client_top_album_cover;

    // 0: song title, 1: artist, 2: song url, 3: album cover
    public String[] submitSong = new String[10];

    private final boolean checkCurrent = false;
    private final boolean checkTop = false;
    public static final String TAG = "ComposeActivity";

    // spotify info
    private static final String CLIENT_ID = "cae795cea2f94211bce48b701c1cfa40";
    private static final String REDIRECT_URI = "com.example.spotify://callback";
    private SpotifyAppRemote mSpotifyAppRemote;

    public static String ACCESS_TOKEN = MainActivity.ACCESS_TOKEN;
    SpotifyClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = new SpotifyClient(ComposeActivity.this, ACCESS_TOKEN);

        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        currentSong = findViewById(R.id.currentSong);
        topSong = findViewById(R.id.topSong);

        // submit post
        submitListener();

        // get spotify info, fill it in in radio buttons
        setButtons();

        // display lyrics depending on which song is chosen
        // on hold since lyrics api is under maintenance
        // onRadioButtonClicked(currentSong);
        // onRadioButtonClicked(topSong);

        Log.d(TAG, ACCESS_TOKEN);
    }


    // user post on timeline, runs once submit button is clicked
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

                if (onRadioButtonSelected(topSong) == "current song" || onRadioButtonSelected(currentSong) == "current song") {
                    descriptionSpotify = description_currentSong;
                    savePostSpotify(description, descriptionSpotify, currentUser, client.current_song);
                } else if (onRadioButtonSelected(topSong) == "top song" || onRadioButtonSelected(currentSong) == "top song") {
                    submitSong = client.top_song.clone();
                    descriptionSpotify = description_topSong;
                    savePostSpotify(description, descriptionSpotify, currentUser, client.top_song);
                } else { // no song was chosen
                    descriptionSpotify = null;
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

        // connect to spotify and set text on buttons once connected
        SpotifyAppRemote.connect(ComposeActivity.this, connectionParams,
                new Connector.ConnectionListener() {
                    public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                        mSpotifyAppRemote = spotifyAppRemote;

                        client.getCurrentTrack(new VolleyCallback() {
                            @Override
                            public void onSuccess() {
                                // get info from client, set it as a global variable in compose activity for other methods
//                                client_current_song = client.current_song;
//                                client_current_artist = client.current_artist;
//                                songUrl_current = client.current_song_url;
                                description_currentSong = "Current Song Playing: " + client.current_song[0] + ", " + client.current_song[1];
                                currentSong.setText(description_currentSong);
                            }
                        });

                        client.getTopTrack(new VolleyCallback() {
                            @Override
                            public void onSuccess() {
                                // get info from client, set it as a global variable in compose activity for other methods
//                                client_top_artist = client.top_artist;
//                                client_top_song = client.top_song;
//                                songUrl_top = client.top_song_url;
                                description_topSong = "Top Song This Month: " + client.top_song[0] + ", " + client.top_song[1];
                                topSong.setText(description_topSong);
                            }
                        });
                    }

                    public void onFailure(Throwable throwable) {
                        // something went wrong when attempting to connect, Handle errors here
                        Log.e(TAG, throwable.getMessage(), throwable);
                    }
                });
    }

    // check which button was clicked once submit button is clicked
    public String onRadioButtonSelected(View view) {
        // is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // check which radio button was clicked
        switch (view.getId()) {
            case R.id.currentSong:
                if (checked) {
                    return "current song";
                }
                break;
            case R.id.topSong:
                if (checked) {
                    return "top song";
                }
                break;
        }
        return null;
    }

    // displays lyrics if one of the songs (buttons) is clicked
    public String onRadioButtonClicked(View view) {
        // is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // check which radio button was clicked
        switch (view.getId()) {
            case R.id.currentSong:
                if (checked) {
                    currentSong.setText(description_currentSong);
                    return description_currentSong;
                }
                break;
            case R.id.topSong:
                if (checked) {
                    topSong.setText(description_topSong);
                    return description_topSong;
                }
                break;
        }
        return null;
    }

    private String getLyrics(String artist, String title) {
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
    // this method creates post w/o extra spotify info; regular post
    private void savePost(String description, ParseUser currentUser) {
        Post post = new Post();
        post.setDescription(description);
        post.setUser(currentUser);

        // progress bar
        ProgressDialog pd = new ProgressDialog(ComposeActivity.this);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.show();
        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(ComposeActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                pd.dismiss();
                Log.i(TAG, "Post save was successful!");
                etDescription.setText("");

                Intent i = new Intent(ComposeActivity.this, MainActivity.class);
                startActivity(i);
            }
        });
    }

    // creates post w/ extra spotify info
    private void savePostSpotify(String description, String spotifyDescription, ParseUser currentUser, String[] songDetails) {
        Log.d(TAG, songDetails[0]);

        Post post = new Post();
        post.setDescription(description);
        post.setDescriptionSpotify(spotifyDescription);
        post.setUser(currentUser);
        post.setSongUrl(songDetails[2]);
        post.setSongArtist(songDetails[1]);
        post.setSongTitle(songDetails[0]);
        post.setSongCover(songDetails[3]);
        post.setUserID(currentUser.getObjectId());

        ProgressDialog pd = new ProgressDialog(ComposeActivity.this);
        pd.setTitle("Loading...");
        pd.setMessage("Please wait.");
        pd.setCancelable(false);
        pd.show();

        post.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Error while saving", e);
                    Toast.makeText(ComposeActivity.this, "Error while saving!", Toast.LENGTH_SHORT).show();
                }
                Log.i(TAG, "Post save was successful!");
                etDescription.setText("");

                Intent i = new Intent(ComposeActivity.this, MainActivity.class);
                startActivity(i);
                pd.dismiss();
            }
        });
    }
}