package com.closeby;

import android.app.ActionBar;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.closeby.ui.FoldingPaneLayout;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestBatch;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends FragmentActivity implements View.OnClickListener {
    TextView ActionBarTitle, DrawerName, DrawerUsername, OnlineStatusText;
    LinearLayout LogoutButton, OwnProfileButton;
    ParseUser currentParseUser;
    User currentUser;
    Typeface leagueGothic;
    LinearLayout OnlineButton, OpenMenuButton, MainLayout;
    FoldingPaneLayout paneLayout;
    GPSTracker tracker;
    ImageView OpenSettings;
    View OnlineIndicator;
    Handler mHandler;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        currentParseUser = ParseUser.getCurrentUser();
        ActionBarUI();
        drawerUI();

        // Associate the device with a user
        /*ParseInstallation installation = ParseInstallation.getCurrentInstallation();
        installation.put("user", ParseUser.getCurrentUser());
        installation.put("user_objectid", ParseUser.getCurrentUser().getObjectId());
        installation.saveInBackground();
*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        new FillUpData().execute();
    }

    private void ActionBarUI() {
        leagueGothic = Typeface.createFromAsset(getAssets(), "fonts/league_gothic.otf");

        ActionBar bar = getActionBar();
        bar.setDisplayShowTitleEnabled(true);
        bar.setIcon(R.drawable.logo_light);
        bar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // forceTabs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_activity_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void initUI() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                R.drawable.ic_drawer,
                R.string.menu_open,
                R.string.menu_closed
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getActionBar().setTitle("Circum");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);

        //OnlineButton = (LinearLayout) findViewById(R.id.action_status_button);
        // OnlineButton.setOnClickListener(this);
        // OnlineStatusText = (TextView) findViewById(R.id.online_status_text);
        // OnlineIndicator = findViewById(R.id.online_indicator_view);

        /*if (currentUser.isOnline()) {
            Toast.makeText(this, "Online", Toast.LENGTH_SHORT).show();
            OnlineStatusText.setText("ONLINE");
            OnlineButton.setBackgroundColor(Color.parseColor("#009060"));
        } else {
            Toast.makeText(this, "Offline", Toast.LENGTH_SHORT).show();
            OnlineStatusText.setText("OFFLINE");
        }*/

       //  tracker = new GPSTracker(this);
/*
        try {
            if (tracker.canGetLocation()) {
                currentUser.UpdateLocation(tracker.getLocation());
            } else {
                tracker.showSettingsAlert();
            }
        } catch (NullPointerException e) {
            Log.d("Circum: Location",e.toString());
            e.printStackTrace();
        }*/


        //OpenMenuButton = (LinearLayout) findViewById(R.id.open_menu_button);
        //OpenMenuButton.setOnClickListener(this);

        // paneLayout = (FoldingPaneLayout) findViewById(R.id.folding_pane_layout);

        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        Fragment feed = new FeedFragment();
        trans.add(R.id.MainActivityFragmentHolder, feed, Constants.FragmentConstants.VisibleFragment);
        trans.commit();
    }

    private class FillUpData extends AsyncTask<Void,Void, String> {
        String Username, FullName;
        User curuser;
        @Override
        protected String doInBackground(Void... voids) {
            curuser = new User(ParseUser.getCurrentUser().getObjectId());
            Username = curuser.getUserName();
            FullName = curuser.getFullName();
            return null;
        }

        @Override
        protected void onPostExecute(String aVoid) {
            super.onPostExecute(aVoid);
            currentUser = curuser;
            DrawerUsername.setText(Username);
            DrawerName.setText(FullName);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
    }

    private void drawerUI() {
        LogoutButton = (LinearLayout) findViewById(R.id.drawer_layout_logout);
        LogoutButton.setOnClickListener(this);

        LinearLayout Chats = (LinearLayout) findViewById(R.id.drawer_layout_chats);
        Chats.setOnClickListener(this);

        OwnProfileButton = (LinearLayout) findViewById(R.id.drawer_layout_own_profile);
        OwnProfileButton.setOnClickListener(this);

        DrawerName = (TextView) findViewById(R.id.drawer_menu_name);
        DrawerName.setTypeface(leagueGothic);

        DrawerUsername = (TextView) findViewById(R.id.drawer_menu_username);
        DrawerUsername.setTypeface(leagueGothic);

        OpenSettings = (ImageView) findViewById(R.id.OpenSettingsIcon);
        OpenSettings.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        switch (view.getId()) {
            case R.id.action_status_button:
                if(currentUser.isOnline()) {
                    currentUser.ToggleOnlineStatus();
                    OnlineStatusText.setText("OFFLINE");
                    OnlineButton.setBackgroundColor(Color.parseColor("#2e2e2e"));

                } else {
                    currentUser.ToggleOnlineStatus();
                    OnlineStatusText.setText("ONLINE");
                    OnlineButton.setBackgroundColor(Color.parseColor("#009060"));
                }

                break;
            case R.id.open_menu_button:
                paneLayout.openPane();
                break;

            case R.id.OpenSettingsIcon:
                Fragment feedFragment = getSupportFragmentManager().findFragmentByTag(Constants.FragmentConstants.VisibleFragment);
                trans.remove(feedFragment);

                mDrawerLayout.closeDrawers();
                Fragment settingsfrag = new SettingFrag();
                trans.add(R.id.MainActivityFragmentHolder, settingsfrag, Constants.FragmentConstants.VisibleFragment);
                trans.commit();
                break;
            case R.id.drawer_layout_logout:
                Toast.makeText(MainActivity.this, "Logging out", Toast.LENGTH_SHORT).show();

                ParseUser currentuser = ParseUser.getCurrentUser();
                currentuser.logOut();

                finish();
                Intent login = new Intent(MainActivity.this, LoginSignupActivity.class);
                startActivity(login);
                break;
            case R.id.drawer_layout_chats:
                Intent chatWindow = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(chatWindow);

                this.finish();
            case R.id.drawer_layout_own_profile:
                Fragment activeFragment = getSupportFragmentManager().findFragmentByTag(Constants.FragmentConstants.VisibleFragment);
                trans.remove(activeFragment);

                mDrawerLayout.closeDrawers();
                Fragment profile_fragment = new ProfileFragment();
                Bundle args = new Bundle();
                args.putBoolean(Constants.FragmentConstants.ProfileFragment.LoggedInUserProfile, true);
                profile_fragment.setArguments(args);
                trans.add(R.id.MainActivityFragmentHolder, profile_fragment, Constants.FragmentConstants.VisibleFragment);
                trans.commit();
                break;
            default:
                break;
        }
    }
}

class FeedFragment extends Fragment implements ActionBar.TabListener {
    ViewPager MainPager;
    double CurrentLatitude, CurrentLongitude;
    GPSTracker tracker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.feed_fragment, container, false);
        initUI(v);

        ActionBar bar = getActivity().getActionBar();

        ActionBar.Tab tab1 = bar.newTab()
                            .setIcon(R.drawable.user_add)
                            //.setText("Requests")
                            .setTabListener(this);


        bar.addTab(tab1);
        bar.addTab(bar.newTab().setIcon(R.drawable.range)/*.setText("Nearby")*/.setTabListener(this));
        bar.addTab(bar.newTab().setIcon(R.drawable.users_tab)/*.setText("Friends")*/.setTabListener(this));

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroy();
        getActivity().getActionBar().removeAllTabs();
    }

    private void initUI(View v) {

/*
        tracker = new GPSTracker(getActivity());
        // latitude and longitude
        CurrentLatitude = tracker.getLatitude();
        CurrentLongitude = tracker.getLongitude();
*/

        MainPager = (ViewPager) v.findViewById(R.id.MainActivity_Pager);
        Bundle info = new Bundle();
/*
        info.putDouble(Constants.FragmentConstants.CurrentUserLocation_Latitude, CurrentLatitude);
        info.putDouble(Constants.FragmentConstants.CurrentUserLocation_Longitude, CurrentLongitude);
*/
        MainViewPagerAdapter adapter = new MainViewPagerAdapter(getActivity().getSupportFragmentManager(), info);
        MainPager.setAdapter(adapter);
        MainPager.setCurrentItem(1);

        MainPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                getActivity().getActionBar().setSelectedNavigationItem(position);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {
        MainPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, android.app.FragmentTransaction fragmentTransaction) {

    }

/*
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getActivity().getFragmentManager().findFragmentById(
                    R.id.map)).getMap();

            LatLng location = new LatLng(tracker.getLatitude(), tracker.getLongitude());

            if (googleMap != null) {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);

                CameraPosition position = new CameraPosition.Builder().target(location).zoom(19.0f).build();

                // create marker
                MarkerOptions marker = new MarkerOptions().position(new LatLng(CurrentLatitude, CurrentLongitude)).title("YOU");

                // adding marker
                googleMap.addMarker(marker);

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setZoomControlsEnabled(false);
                googleMap.getUiSettings().setCompassEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);


                // adding marker
                googleMap.addMarker(marker);

                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(position));
            } else {
                Toast.makeText(getActivity(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
*/

    class MainViewPagerAdapter extends FragmentStatePagerAdapter {
        private Bundle fragmentArgs;

        public MainViewPagerAdapter(FragmentManager fm, Bundle fragArgs) {
            super(fm);
            this.fragmentArgs = fragArgs;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment frag = null;
            switch (position) {
                case 0:
                    frag = new PendingRequestsFragment();
                    break;
                case 1:
                    frag = new CurrentPeopleFragment();
                    break;
                case 2:
                    frag = new FriendsFragment();
                    break;
            }

            frag.setArguments(fragmentArgs);
            return frag;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence[] titles = {"Pending", "Around You", "Friends"};

            return titles[position];
        }
    }

}

class SettingFrag extends Fragment implements View.OnClickListener {
    Button FacebookButton, TwitterButton;
    TextView SocialText;
    Typeface Bosun, Adam, Bebas;
    boolean FacebookLinked, TwitterLinked;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.settings_fragment, container, false);

        FacebookLinked = ParseFacebookUtils.isLinked(ParseUser.getCurrentUser());
        TwitterLinked = ParseTwitterUtils.isLinked(ParseUser.getCurrentUser());

        SocialText = (TextView) v.findViewById(R.id.SettingsSocialHeaderText);

        Bosun = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bosun03.otf");
        Adam = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ADAM.otf");
        Bebas = Typeface.createFromAsset(getActivity().getAssets(), "fonts/BebasNeue.otf");


        FacebookButton = (Button) v.findViewById(R.id.ConnectedToFacebookButton);
        TwitterButton = (Button) v.findViewById(R.id.ConnectedToTwitterButton);

        FacebookButton.setOnClickListener(this);
        TwitterButton.setOnClickListener(this);

        SocialText.setTypeface(Bosun);
        FacebookButton.setTypeface(Bebas);
        TwitterButton.setTypeface(Bebas);


        if(FacebookLinked) {
            FacebookButton.setText("Connected To Facebook");
            FacebookButton.setBackgroundColor(Color.parseColor("#3b5998"));
        }

        if(TwitterLinked){
            TwitterButton.setText("Connected To Facebook");
            TwitterButton.setBackgroundColor(Color.parseColor("#3b5998"));
        }
        return v;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ConnectedToFacebookButton:
                if(FacebookLinked) {
                    ParseFacebookUtils.unlinkInBackground(ParseUser.getCurrentUser());
                    FacebookButton.setText("Connect To Facebook");
                    FacebookButton.setBackgroundColor(Color.parseColor("#963b59"));

                } else {
                    ParseFacebookUtils.link(ParseUser.getCurrentUser(), getActivity(), new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            FacebookButton.setText("Connected To Facebook");
                            FacebookButton.setBackgroundColor(Color.parseColor("#3b5998"));
                        }
                    });
                }
                break;

            case R.id.ConnectedToTwitterButton:

/*
                if(TwitterLinked) {
                    ParseTwitterUtils.unlinkInBackground(ParseUser.getCurrentUser());
                    TwitterButton.setText("Connect To Twitter");
                } else {
*/
                Toast.makeText(getActivity(), "Connect To Twitter", Toast.LENGTH_SHORT).show();
                ParseTwitterUtils.link(ParseUser.getCurrentUser(), getActivity(), new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        TwitterButton.setText("Connected to Twitter");
                    }
                });
//                 }
                break;
        }
    }
}


class PendingRequestsFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.pending_req_fragment, container, false);

        return v;
    }
}
