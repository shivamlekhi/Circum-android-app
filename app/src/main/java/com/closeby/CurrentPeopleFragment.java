package com.closeby;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;

public class CurrentPeopleFragment extends Fragment {
    ProgressBar loader;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.curent_people_fragment, container , false);
        loader = (ProgressBar) rootView.findViewById(R.id.around_people_loader);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        // new GetUsers().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        new GetUsers().execute();
    }

    private class GetUsers extends AsyncTask<Void, Void, Void> {
        List<ParseObject> nearbyUsers;

        @Override
        protected Void doInBackground(Void... voids) {
            ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.ParseConstants.User.Classname);
            // query.whereNear(Constants.ParseConstants.User.LastLocation, GetCurrentLocation());
            query.whereEqualTo(Constants.ParseConstants.User.Online, true);

            try {
                nearbyUsers = query.find();
            } catch(ParseException parsec) {
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (nearbyUsers != null) {
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                for (ParseObject user : nearbyUsers) {

                    if (user.getObjectId() != ParseUser.getCurrentUser().getObjectId()) {
                        Fragment UserFragment = new UserListItemFragment();
                        Bundle args = new Bundle();
                        args.putString(Constants.FragmentConstants.UserListItem.User_Name, user.getString(Constants.ParseConstants.User.Name));
                        args.putString(Constants.FragmentConstants.UserListItem.User_ObjectId, user.getObjectId());
                        UserFragment.setArguments(args);
                        trans.add(R.id.current_people_holder, UserFragment);
                    }
                }
                trans.commit();
            }

            loader.setVisibility(View.GONE);
        }
    }


    private ParseGeoPoint GetCurrentLocation() {
        double Latitude = getArguments().getDouble(Constants.FragmentConstants.CurrentUserLocation_Latitude);
        double Longitude = getArguments().getDouble(Constants.FragmentConstants.CurrentUserLocation_Longitude);

        return new ParseGeoPoint(Latitude, Longitude);
    }
}

class UserListItemFragment extends Fragment {
    TextView NameView,  DescriptionView;
    LinearLayout MainLayout;
    Typeface Bosun, Bebas;
    User currentUser;
    ProgressBar DescriptionLoader, MainImageLoader;
    ImageView MainImage;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.user_list_item, container, false);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        initUI();
    }

    private void initUI() {
        Bosun = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bosun03.otf");
        Bebas = Typeface.createFromAsset(getActivity().getAssets(), "fonts/BebasNeue.otf");

        MainLayout = (LinearLayout) rootView.findViewById(R.id.user_item_layout);
        MainImage = (ImageView) rootView.findViewById(R.id.user_list_item_picture);
        MainImageLoader = (ProgressBar) rootView.findViewById(R.id.user_list_item_picture_loader);
        NameView = (TextView) rootView.findViewById(R.id.user_list_item_name);
        DescriptionView = (TextView) rootView.findViewById(R.id.user_list_item_description);
        DescriptionLoader = (ProgressBar) rootView.findViewById(R.id.user_list_item_description_loader);

        DescriptionView.setTypeface(Bosun);
        NameView.setTypeface(Bebas);
        NameView.setText(getArguments().getString(Constants.FragmentConstants.UserListItem.User_Name));

        new GetData().execute();

        MainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
                Fragment feedFragment = getActivity().getSupportFragmentManager().findFragmentByTag(Constants.FragmentConstants.VisibleFragment);
                trans.remove(feedFragment);

                Fragment ProfileFrag = new ProfileFragment();
                Bundle args = new Bundle();
                args.putString(Constants.FragmentConstants.ProfileFragment.UserObjectId, getArguments().getString(Constants.FragmentConstants.UserListItem.User_ObjectId));
                ProfileFrag.setArguments(args);
                trans.add(R.id.MainActivityFragmentHolder, ProfileFrag, Constants.FragmentConstants.VisibleFragment);
                trans.commit();
            }
        });

    }
    class GetData extends AsyncTask<Void, Void, Void> {
        String Description;
        Bitmap bitmap;
        @Override
        protected Void doInBackground(Void... voids) {
            currentUser = new User(getArguments().getString(Constants.FragmentConstants.UserListItem.User_ObjectId));
            Description = currentUser.getDescription();

            ParseFile image = (ParseFile) currentUser.getUserObject().get("profile_picture");

            try {
                byte[] data = image.getData();

                bitmap = BitmapFactory
                        .decodeByteArray(
                                data, 0,
                                data.length);

            } catch(ParseException e) {

            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            DescriptionView.setVisibility(View.VISIBLE);
            DescriptionView.setText(Description);
            DescriptionLoader.setVisibility(View.GONE);

            MainImage.setImageBitmap(bitmap);
            MainImageLoader.setVisibility(View.GONE);
            MainImage.setVisibility(View.VISIBLE);
        }
    }
}
