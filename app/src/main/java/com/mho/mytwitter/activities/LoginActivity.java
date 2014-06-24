package com.mho.mytwitter.activities;

import com.codepath.oauth.OAuthLoginActivity;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.mho.mytwitter.R;
import com.mho.mytwitter.helpers.TwitterApplication;
import com.mho.mytwitter.helpers.TwitterClient;
import com.mho.mytwitter.helpers.Utils;
import com.mho.mytwitter.models.User;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class LoginActivity extends OAuthLoginActivity<TwitterClient> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
    }

    // Inflate the menu; this adds items to the action bar if it is present.
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    // OAuth authenticated successfully, launch primary authenticated activity
    // i.e Display application "homepage"
    @Override
    public void onLoginSuccess() {

        // verify credentials to retrieve user's info
        TwitterApplication.getTwitterClient().verifyCredentials(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(JSONObject response) {
                User user = User.fromJsonObject(response);
                TwitterApplication.setUser(user);
                super.onSuccess(response);
            }

            @Override
            public void onFailure(Throwable error, String content) {
                Log.d("DEBUG", error.toString());
                Log.d("DEBUG", content);
            }
        });




        Intent i = new Intent(this, TimelineActivity.class);
        startActivity(i);
//        Toast.makeText(this, "SUCCESS!", Toast.LENGTH_SHORT).show();
    }



    // OAuth authentication flow failed, handle the error
    // i.e Display an error dialog or toast
    @Override
    public void onLoginFailure(Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "FAIL!", Toast.LENGTH_SHORT).show();
    }

    // Click handler method for the button used to start OAuth flow
    // Uses the client to initiate OAuth authorization
    // This should be tied to a button used to login
    public void loginToTwitter(View view) {
        if(!Utils.isNetworkAvailable(this)) {
            Crouton.makeText(this, getString(R.string.msg_network_unavailble), Utils.STYLE).show();
            return;
        }
        getClient().connect();
    }
}
