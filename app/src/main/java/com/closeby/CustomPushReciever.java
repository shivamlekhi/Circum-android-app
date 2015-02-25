package com.closeby;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.parse.ParseUser;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sam on 7/30/2014.
 */
public class CustomPushReciever extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        try {

            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));
            CreateNotification(json, context);
            Log.d("CloseBy Push Notifications: " , "json data: \n\n\n" + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void CreateNotification(JSONObject json, Context context) {
        // build notification
        // the addAction re-use the same intent to keep the example short
        Notification n  = null;
        try {
            Intent intent = new Intent(context, MainActivity.class);
            PendingIntent pIntent = PendingIntent.getActivity(context, 0, intent, 0);
            n = new NotificationCompat.Builder(context)
                    .setContentTitle(json.getString("name") + " says hi !")
                    .setContentText("nothing")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setTicker("Closeby " + json.getString("name") + " says hi !")
                    .setContentIntent(pIntent)
                    .setAutoCancel(true).build();
                    //.addAction(R.drawable.polaroids, "Say Hi Too ! ", pIntent).build();

            NotificationManager notificationManager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            notificationManager.notify(0, n);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void SavePokeToDatabase() {
        User currentUser = new User(ParseUser.getCurrentUser().getObjectId());


    }
}
