package com.example.spotify;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.spotify.sdk.android.auth.AuthorizationClient;
import com.spotify.sdk.android.auth.AuthorizationRequest;
import com.spotify.sdk.android.auth.AuthorizationResponse;

public class SpotifyLoginActivity extends AppCompatActivity {

    private static final String TAG = "SpotifyLoginActivity";
    private static final int REQUEST_CODE = 1337;
    public static final String AUTH_TOKEN = "AUTH_TOKEN";
    private static final String CLIENT_ID = "cae795cea2f94211bce48b701c1cfa40";
    private static final String REDIRECT_URL = "com.example.spotify://callback";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_spotify_login);
        Button mLoginButton = findViewById(R.id.login_button);

        mLoginButton.setOnClickListener(mListener);
    }

    private void openLoginWindow() {
//        AuthorizationRequest.Builder builder = new AuthorizationRequest.Builder(CLIENT_ID, TOKEN,REDIRECT_URL);
//        builder.setScopes(new String[]{ "streaming"});
//        AuthorizationRequest request = builder.build();
//        AuthorizationClient.openLoginActivity(this,REQUEST_CODE,request);
    }

    @Override
    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_CODE) {

            final AuthorizationResponse response = AuthorizationClient.getResponse(resultCode, data);
            switch (response.getType()) {
                // Response was successful and contains auth token
                case TOKEN:
                    Log.e(TAG,"Auth token: " + response.getAccessToken());
                    Intent intent = new Intent(SpotifyLoginActivity.this,
                            LoginActivity.class);
                    intent.putExtra(AUTH_TOKEN, response.getAccessToken());
                    startActivity(intent);
                    destroy();
                    break;
                // Auth flow returned an error
                case ERROR:
                    Log.e(TAG,"Auth error: " + response.getError());
                    break;

                // Most likely auth flow was cancelled
                default:
                    Log.d(TAG,"Auth result: " + response.getType());

            }
        }
    }


    View.OnClickListener mListener = new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.login_button:
                    openLoginWindow();
                    break;
            }
        }
    };

    public void destroy(){
        SpotifyLoginActivity.this.finish();
    }
}