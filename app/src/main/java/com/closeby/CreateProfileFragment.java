package com.closeby;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sam on 8/28/2014.
 */
public class CreateProfileFragment extends Activity {
    ParseUser currentParseUser;
    User currentUser;

    ImageView ProfilePicture;
    TextView Username, Email;
    JSONObject jaon;
    Typeface Bosun,  Bebas;
    EditText AboutYou;
    CircleImageView MainImage;
    Button DoneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_profile_step_1);

        initFonts();

        init();
        InitActionbarBar();
    }

    private void InitActionbarBar(){
        ActionBar bar = getActionBar();
        bar.setDisplayShowTitleEnabled(false);
        bar.setBackgroundDrawable(new ColorDrawable(R.color.madcar_redorange));
    }

    private void initFonts() {
        Bosun = Typeface.createFromAsset(getAssets(), "fonts/bosun03.otf");
        Bebas = Typeface.createFromAsset(getAssets(), "fonts/BebasNeue.otf");
    }

    private void init() {
        getActionBar().setDisplayShowTitleEnabled(false);
        currentParseUser = ParseUser.getCurrentUser();
        currentUser = new User(currentParseUser.getObjectId());

        ProfilePicture = (ImageView) findViewById(R.id.CreateProfileImage);
        Username = (TextView) findViewById(R.id.CreateProfileName);
        Email = (TextView) findViewById(R.id.CreateProfileEmail);

        MainImage = (CircleImageView) findViewById(R.id.CreateProfileImage);
        DoneButton = (Button) findViewById(R.id.create_profile_step1_done);

        DoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenHome();
            }
        });

        Username.setTypeface(Bebas);
        Email.setTypeface(Bosun);

        if(ParseFacebookUtils.isLinked(currentParseUser)) {
            getFacebookInfo();
        }

        AboutYou = (EditText) findViewById(R.id.create_profile_about_you);
        AboutYou.setTypeface(Bosun);

    }

    private void OpenHome(){
        Intent home = new Intent(this, MainActivity.class);
        startActivity(home);
    }

    private void getFacebookInfo() {
        new Request(
                Session.getActiveSession(),
                "/me",
                null,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                    /* handle the result */
                        jaon = response.getGraphObject().getInnerJSONObject();

                        try {
                            Username.setText(jaon.getString("name"));
                            currentUser.SaveFullName(jaon.getString("name"));

                            currentUser.SaveEmail(jaon.getString("email"));
                            Email.setText(jaon.getString("email"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
        ).executeAsync();

        Bundle params = new Bundle();
        params.putBoolean("redirect", false);
        params.putString("height", "300");
        params.putString("type", "normal");
        params.putString("width", "300");
        new Request(
                Session.getActiveSession(),
                "/me/picture",
                params,
                HttpMethod.GET,
                new Request.Callback() {
                    public void onCompleted(Response response) {
                        try {
                            JSONObject data = response.getGraphObject().getInnerJSONObject().getJSONObject("data");

                            String url = data.getString("url");
                            Response res = response;

                            new DownloadImage().execute(new String[]{url});
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
        ).executeAsync();
    }

    private class DownloadImage extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... URL) {

            String imageURL = URL[0];

            Bitmap bitmap = null;
            try {
                // Download Image from URL
                InputStream input = new java.net.URL(imageURL).openStream();
                // Decode Bitmap
                bitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            // Set the bitmap into ImageView
            MainImage.setImageBitmap(result);

            // ParseFile imageFile = new ParseFile();
            // Close progressdialog
        }
    }
}
