package com.closeby;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by Sam on 7/1/2014.
 */
public class User {
    ParseObject currentUser;
    boolean OnlineStatus;
    String UserObjectId;

    public User (String ObjectId) {
        this.UserObjectId = ObjectId;
        currentUser = getUserObject();
    }


    public boolean isOnline() {
        OnlineStatus = currentUser.getBoolean("online");
        return OnlineStatus;
    }

    public String getFullName() {
        return currentUser.getString("name");
    }

    public String getDescription() {
        return currentUser.getString("description");
    }

    public void UpdateLocation(Location location) {
        ParseGeoPoint geolocation = new ParseGeoPoint(location.getLatitude(), location.getLongitude());
        ParseObject user = getUserObject();
        user.put(Constants.ParseConstants.User.LastLocation, geolocation);
        user.saveInBackground();
    }


    public void ToggleOnlineStatus() {
        if(isOnline()) {
            currentUser.put(Constants.ParseConstants.User.Online, false);
        } else {
            currentUser.put(Constants.ParseConstants.User.Online, true);
        }

        currentUser.saveInBackground();
    }


    public ParseObject getUserObject() {

        ParseQuery<ParseObject> query = ParseQuery.getQuery(Constants.ParseConstants.User.Classname);
        query.whereEqualTo("objectId", UserObjectId);
        ParseObject user = null;
        try {
            user = query.find().get(0);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return user;
    }

    public String getUserName() {
        return currentUser.getString(Constants.ParseConstants.User.Username);
    }


    public void SaveFullName(String name) {
        currentUser.put(Constants.ParseConstants.User.Name, name);
        currentUser.saveInBackground();
    }

    public void SaveEmail(String email) {
        currentUser.put(Constants.ParseConstants.User.Email, email);
        currentUser.saveInBackground();
    }

    public void PokeUser(String RecUserObjectId) {
        // Save Poke to Database
        ParseObject poke = new ParseObject("Pings");
        poke.put(Constants.ParseConstants.Pokes.From, this.UserObjectId);
        poke.put(Constants.ParseConstants.Pokes.To, RecUserObjectId);

        // Send Notification to User
        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereEqualTo("user_objectid", RecUserObjectId);
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        try {
            JSONObject data = new JSONObject();
            data.put("action", "com.closeby.push_ping");
            data.put("name", getFullName());
            push.setData(data);
            push.send();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (com.parse.ParseException e) {
            e.printStackTrace();
        }

    }
}