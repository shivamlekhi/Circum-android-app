package com.closeby;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.parse.GetDataCallback;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.signpost.http.HttpResponse;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Sam on 7/6/2014.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {
    String UserObjectid;
    Typeface leagueGothic, NewsCycle, Bosun, Adam, Bebas;
    TextView PingsView, FriendsView, NameView, IntrestsHeader, FacebookHeaderText, TwitterHeaderText, DescriptionHeader, DescriptionText;
    ViewPager FacebookSlider, TweetsSlider;
    LinearLayout PingButton, ChatButton;
    User currentUser, loggedInUser;
    CircleImageView ProfilePicture;


    /*  EditProfileButton   */

    LinearLayout EditProfilePicture, EditDescription;
    /*  -/EditProfileButton */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment_main, container, false);
        if(!getArguments().getBoolean(Constants.FragmentConstants.ProfileFragment.LoggedInUserProfile)) {
            currentUser = new User(getArguments().getString(Constants.FragmentConstants.ProfileFragment.UserObjectId));
        } else {
            loggedInUser = new User(ParseUser.getCurrentUser().getObjectId());
            currentUser = loggedInUser;
            PingButton.setVisibility(View.GONE);
        }

        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);

        init();
        initUI(v);
        initEdit(v);
        //FacebookSetup();
        // TwitterSetup();
        FacebookSlider = (ViewPager) v.findViewById(R.id.facebook_view_pager);
        TweetsSlider = (ViewPager) v.findViewById(R.id.TweetsSlider);

        setHasOptionsMenu(true);

        return v;
    }

    private void init() {
        UserObjectid = getArguments().getString(Constants.FragmentConstants.ProfileFragment.UserObjectId);
        leagueGothic = Typeface.createFromAsset(getActivity().getAssets(), "fonts/league_gothic.otf");
        NewsCycle = Typeface.createFromAsset(getActivity().getAssets(), "fonts/newscycle-regular.ttf");
        Bosun = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bosun03.otf");
        Adam = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ADAM.otf");
        Bebas = Typeface.createFromAsset(getActivity().getAssets(), "fonts/BebasNeue.otf");
    }

    private void initEdit(View rootView) {
        EditProfilePicture = (LinearLayout) rootView.findViewById(R.id.edit_profile_picture_button);
        EditDescription = (LinearLayout) rootView.findViewById(R.id.edit_profile_description_button);

        EditProfilePicture.setOnClickListener(this);
        EditDescription.setOnClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main_activity_menu, menu);
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()) {
            case R.id.profile_ping_button:
                loggedInUser.PokeUser(currentUser.UserObjectId);
                Toast.makeText(getActivity() , "Push Sent" , Toast.LENGTH_SHORT).show();
                break;

            /*case R.id.profile_chat_button:
                Intent ChatWindow = new Intent(getActivity(), ChatActivity.class);
                Bundle args = new Bundle();
                args.putString(Constants.FragmentConstants.ChatActivity.To_UserObjectId,  getArguments().getString(Constants.FragmentConstants.ProfileFragment.UserObjectId));
                ChatWindow.putExtras(args);
                startActivity(ChatWindow);
                break;*/
        }
    }

    private void TwitterSetup() {
        new GetTweets().execute();
    }



    class GetTweets extends AsyncTask<Void, Void, Void> {
        String Tweets;

        @Override
        protected Void doInBackground(Void... voids) {

            HttpClient client = new DefaultHttpClient();
            HttpGet verifyGet = new HttpGet("https://api.twitter.com/1.1/statuses/user_timeline.json");
            ParseTwitterUtils.getTwitter().signRequest(verifyGet);
            try {
                org.apache.http.HttpResponse response = client.execute(verifyGet);

                BufferedReader rd = new BufferedReader(
                        new InputStreamReader(response.getEntity().getContent()));

                StringBuffer result = new StringBuffer();
                String line = "";
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }

                this.Tweets = result.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            try {

                JSONArray tweets = new JSONArray(Tweets);
                TweetsAdapter adapter = new TweetsAdapter(getActivity().getSupportFragmentManager(), tweets);
                TweetsSlider.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    private void FacebookSetup() {
        RequestBatch batch = new RequestBatch();
        batch.add(new Request(Session.getActiveSession(), "/me/statuses", null, HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                try {
                    JSONArray data = response.getGraphObject()
                            .getInnerJSONObject()
                            .getJSONArray("data");

                    FacebookPostsAdapter adapter = new FacebookPostsAdapter(getActivity().getSupportFragmentManager(), data);

                    FacebookSlider.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }));
        batch.executeAsync();
    }

    private void initUI(View v) {
        NameView = (TextView) v.findViewById(R.id.ProfileFragmentNameView);
        PingsView = (TextView) v.findViewById(R.id.ProfileFragmentPingsView);
        FriendsView = (TextView) v.findViewById(R.id.ProfileFragmentFriendsView);
        IntrestsHeader = (TextView) v.findViewById(R.id.IntrestskHeaderText);
        FacebookHeaderText = (TextView) v.findViewById(R.id.FacebookHeaderText);
        TwitterHeaderText = (TextView) v.findViewById(R.id.TwitterHeaderText);
        // ChatButton = (LinearLayout) v.findViewById(R.id.profile_chat_button);
        PingButton = (LinearLayout) v.findViewById(R.id.profile_ping_button);
        //DescriptionHeader = (TextView) v.findViewById(R.id.DescriptionHeaderText);
        DescriptionText = (TextView) v.findViewById(R.id.ProfileDescriptionText);
        ProfilePicture = (CircleImageView) v.findViewById(R.id.profile_main_image);

        NameView.setTypeface(Bebas);
        PingsView.setTypeface(NewsCycle);
        FriendsView.setTypeface(NewsCycle);
        IntrestsHeader.setTypeface(Bosun);
        FacebookHeaderText.setTypeface(Bosun);
        TwitterHeaderText.setTypeface(Bosun);
        // DescriptionHeader.setTypeface(Bosun);


        // DescriptionHeader.setText("About " + currentUser.getFullName());
        NameView.setText(currentUser.getFullName());
        DescriptionText.setText(currentUser.getDescription());

        ParseFile image = (ParseFile) currentUser.getUserObject().get("profile_picture");

        try {
            image.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, com.parse.ParseException e) {
                    Bitmap bmp = BitmapFactory
                            .decodeByteArray(
                                    data, 0,
                                    data.length);
                    ProfilePicture.setImageBitmap(bmp);
                }
            });
        } catch(NullPointerException e) {

        }
        PingButton.setOnClickListener(this);
        // ChatButton.setOnClickListener(this);
    }
}

class IntrestsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.intrests_fragment, container ,false);
        return v;
    }
}

class FacebookPostItemFragment extends Fragment{
    TextView MainText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.facebook_profile_item, container, false);

        MainText = (TextView) v.findViewById(R.id.facebook_post_mainText);

        try {
            JSONObject StatusData = new JSONObject(getArguments().getString("status_data"));

            MainText.setText(StatusData.getString("message"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return v;
    }
}

class TweetItemFragment extends Fragment {
    TextView TweetText;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.tweet_layout, container, false);
        TweetText = (TextView) v.findViewById(R.id.tweet_text);

        Typeface Bosun = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bosun03.otf");
        TweetText.setTypeface(Bosun);
        TweetText.setText(getArguments().getString("tweet"));
        return v;
    }
}

class TweetsAdapter extends FragmentStatePagerAdapter {
    JSONArray Tweets;

    public TweetsAdapter(FragmentManager fm, JSONArray tweets) {
        super(fm);
        this.Tweets = tweets;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment Tweet = new TweetItemFragment();
        try {

            JSONObject status = Tweets.getJSONObject(position);
            Bundle args = new Bundle();
            args.putString("tweet", status.getString("text"));
            Tweet.setArguments(args);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return Tweet;
    }

    @Override
    public int getCount() {
        return Tweets.length();
    }
}

class FacebookPostsAdapter extends FragmentStatePagerAdapter {
    JSONArray Data;

    public FacebookPostsAdapter(FragmentManager fm, JSONArray data) {
        super(fm);
        this.Data = data;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment PostItem = new FacebookPostItemFragment();
        try {

            JSONObject status = Data.getJSONObject(position);
            Bundle args = new Bundle();
            args.putString("status_data", status.toString());
            PostItem.setArguments(args);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return PostItem;
    }

    @Override
    public int getCount() {
        return Data.length();
    }
}