package com.example.spotify.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.spotify.ExternalLibraries.SpotifyClient;
import com.example.spotify.ExternalLibraries.VolleyCallback;
import com.example.spotify.Fragments.AllPostsFragment;
import com.example.spotify.Fragments.DashboardFragment;
import com.example.spotify.Fragments.HomeFragment;
import com.example.spotify.Fragments.ProfileFragment;
import com.example.spotify.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

import kaaes.spotify.webapi.android.SpotifyApi;

public class MainActivity extends AppCompatActivity {

    // spotify info
    private static final String CLIENT_ID = "cae795cea2f94211bce48b701c1cfa40";
    private static final String REDIRECT_URI = "com.example.spotify://callback";
    private static final String[] scope = {"user-read-playback-state", "user-top-read", "playlist-read-private", "user-read-private",
            "user-read-email", "playlist-read-private", "user-modify-playback-state", "user-read-email", "user-read-currently-playing"};
    private static final int REQUEST_CODE = 1337;
    public static String ACCESS_TOKEN;

    final FragmentManager fragmentManager = getSupportFragmentManager();
    private BottomNavigationView bottomNavigationView;
    public static TextView songArtist;
    public static TextView songTitle;
    public static ImageView songAlbumCover;
    public static ImageButton ibPause;
    public static ImageButton ibPlayBottom;
    public static View bottomSheetParent;
    public static FloatingActionButton logoutBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // sets up toolbar at top with button to compose activity
         Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
         setSupportActionBar(toolbar);
//         getSupportActionBar().setDisplayShowTitleEnabled(false);

        bottomNavigationView = findViewById(R.id.nav_view);
        bottomNavigationView.setItemIconTintList(null);

        songArtist = findViewById(R.id.songArtist);
        songTitle = findViewById(R.id.songTitle);
        songAlbumCover = findViewById(R.id.songAlbumCover);
        ibPause = findViewById(R.id.ibPause);
        ibPlayBottom = findViewById(R.id.ibPlayBottom);
        bottomSheetParent = findViewById(R.id.bottom_sheet_parent);
        logoutBtn = findViewById(R.id.logoutBtn);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment;
                // check which icon has been selected
                switch (item.getItemId()) {
                    case R.id.navigation_dashboard:
                        fragment = new DashboardFragment();
                        break;
                    case R.id.navigation_home:
                        fragment = new HomeFragment();
                        break;
                    case R.id.navigation_allPosts:
                        fragment = new AllPostsFragment();
                        break;
                    case R.id.navigation_profile:
                    default:
                        fragment = new ProfileFragment(ACCESS_TOKEN);
                        break;
                }
                // replace frame layout with the icon(fragment) selected
                fragmentManager.beginTransaction().replace(R.id.flContainer, fragment).commit();
                return true;
            }
        });
        bottomNavigationView.setSelectedItemId(R.id.navigation_allPosts);

        // spotify authorization - ask user for login information and ask specific permissions (scopes)
        AuthorizationRequest.Builder builder =
                new AuthorizationRequest.Builder(CLIENT_ID, AuthorizationResponse.Type.TOKEN, REDIRECT_URI);
        builder.setScopes(scope);
        AuthorizationRequest request = builder.build();
        AuthorizationClient.openLoginActivity(this, REQUEST_CODE, request);

        ibPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotifyClient client = new SpotifyClient(MainActivity.this, ACCESS_TOKEN);
                client.pauseSong(new VolleyCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("MainActivity", "Paused!");
                    }
                });
            }
        });

        // connects to play button on bottom sheet (not on individual posts)
        ibPlayBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SpotifyClient client = new SpotifyClient(MainActivity.this, ACCESS_TOKEN);
                client.playSong(new VolleyCallback() {
                    @Override
                    public void onSuccess() {
                        Log.d("MainActivity", "Paused!");
                    }
                });
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // inflate the menu; this adds items to the tool bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // see what user presses on the toolbar items
        switch (item.getItemId()) {
            case R.id.miCompose:
                composePost();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void composePost() {
        Intent i = new Intent(MainActivity.this, ComposeActivity.class);

        // send access token to compose activity, which gets spotify user data
        i.putExtra("user", ACCESS_TOKEN);
        startActivity(i);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        // check if result comes from the correct activity
        if (requestCode == REQUEST_CODE) {
            AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, intent);

            switch (response.getType()) {
                // response was successful and contains auth token
                case TOKEN:
                    // handle successful response
                    Log.d("MainActivity", "Token successful " + response.getAccessToken());
                    ACCESS_TOKEN = response.getAccessToken();
                    break;

                // auth flow returned an error
                case ERROR:
                    Log.d("MainActivity", "Token unsuccessful");
                    break;

                // most likely auth flow was cancelled
                default:
                    // handle other cases
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

}