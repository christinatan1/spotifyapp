package com.example.spotify.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotify.R;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.royrodriguez.transitionbutton.TransitionButton;

public class LoginActivity extends AppCompatActivity {

    public static final String TAG = "LoginActivity";
    private EditText etUsername;
    private EditText etPassword;
    private TransitionButton btnLogin;
    private TransitionButton btnSignup;
    private TextView textSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // connect to xml layout
        setContentView(R.layout.activity_login);

        // if user is already logged in on their device, skip login
        if (ParseUser.getCurrentUser() != null) {
            goMainActivity();
        }

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        textSignup = findViewById(R.id.textSignup);

        // checks with backend to see if user exists once user clicks "submit"
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = etUsername.getText().toString();
                String password = etPassword.getText().toString();
                loginUser(username, password);
            }
        });

        // creates user in backend once user clicks "signup"
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // send user to custom sign up page
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(i);
            }
        });
    }

    private void loginUser(String username, String password) {
        ParseUser.logInInBackground(username, password, new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                // checks if user exists in backend
                if (e != null) {
                    Log.e(TAG, "Issue with login", e);
                    Toast.makeText(LoginActivity.this, "Invalid Login Credentials", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    // start the loading animation when the user tap the button
                    btnLogin.startAnimation();
                    btnSignup.setVisibility(View.GONE);
                    btnSignup.setText("");
                    textSignup.setVisibility(View.GONE);

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boolean isSuccessful = true;
                            // choose a stop animation if the call was succesful or not
                            if (isSuccessful) {
                                btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                    @Override
                                    public void onAnimationStopEnd() {
                                        // go to main activity if user has signed in properly,
                                        goMainActivity();
                                    }
                                });
                            } else {
                                // button shakes if call was unsuccessful
                                btnLogin.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
                            }
                        }
                    }, 2000);
                }
            }
        });
    }

    private void goMainActivity() {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }

}
