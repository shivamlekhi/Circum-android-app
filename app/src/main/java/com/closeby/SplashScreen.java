package com.closeby;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.parse.ParseUser;

/**
 * Created by Sam on 6/28/2014.
 */
public class SplashScreen extends Activity {
    TextView Title, Desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

            @Override
            public void run() {
                checkUser();
                //ParseUser.getCurrentUser().logOut();
                finish();
            }
        }, 1000);

        Title = (TextView) findViewById(R.id.splash_title);
        Desc = (TextView) findViewById(R.id.splash_desc);

        Typeface leagueGothic = Typeface.createFromAsset(getAssets(), "fonts/league_gothic.otf");
        Title.setTypeface(leagueGothic);
        Desc.setTypeface(leagueGothic);

        getActionBar().hide();
    }

    private void checkUser() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null) {
            Intent in = new Intent(this, MainActivity.class);
            startActivity(in);
        } else {
            Intent in = new Intent(this, LoginSignupActivity.class);
            startActivity(in);
        }
    }
}
