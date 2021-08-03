package com.example.spotify.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.spotify.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.royrodriguez.transitionbutton.TransitionButton;

public class SignUpActivity extends AppCompatActivity {

    private TextInputLayout name;
    private TextInputLayout username;
    private TextInputLayout password;
    private TextInputLayout location;
    private TextInputLayout topGenre;
    private TransitionButton btnSignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // connect to xml layout
        setContentView(R.layout.activity_sign_up);

        name = findViewById(R.id.name);
        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        location = findViewById(R.id.location);
        topGenre = findViewById(R.id.topGenre);
        btnSignup = findViewById(R.id.btnSignup);

        // creates user in backend once user clicks "signup"
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signupUser();
            }
        });
    }

    private void signupUser() {
        String nameInput = name.getEditText().getText().toString();
        String usernameInput = username.getEditText().getText().toString();
        String passwordInput = password.getEditText().getText().toString();
        String locationInput = location.getEditText().getText().toString();
        String genreInput = topGenre.getEditText().getText().toString();

        // set up new user details - parse default
        ParseUser user = new ParseUser();
        user.setUsername(usernameInput);
        user.setPassword(passwordInput);

        // set up custom user details
        user.put("name", nameInput);
        user.put("location", locationInput);
        user.put("topGenres", genreInput);
        user.put("topGenres", genreInput);
        user.put("followers", 0);
        user.put("following", 0);

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e != null) {
                    // sign up didn't succeed (user does not exist). look at the ParseException to figure out what went wrong
                    Log.e("SignUp Activity", "Issue with signup", e);
                    Toast.makeText(SignUpActivity.this, "Invalid Credentials", Toast.LENGTH_SHORT).show();

                } else {
                    // start the loading animation when the user tap the button
                    btnSignup.startAnimation();

                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            boolean isSuccessful = true;
                            // choose a stop animation if your call was succesful or not
                            if (isSuccessful) {
                                btnSignup.stopAnimation(TransitionButton.StopAnimationStyle.EXPAND, new TransitionButton.OnAnimationStopEndListener() {
                                    @Override
                                    public void onAnimationStopEnd() {
                                        // go to main activity if user has signed in properly,
                                        goMainActivity();
                                    }
                                });
                            } else {
                                btnSignup.stopAnimation(TransitionButton.StopAnimationStyle.SHAKE, null);
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
