package com.closeby;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseInstallation;
import com.parse.ParsePush;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Sam on 8/1/2014.
 */
public class ChatActivity extends FragmentActivity {
    BroadcastReceiver receiver;
    LinearLayout MessagesContainer;
    EditText MessageTextInput;
    ImageView SendMessageButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_activity);
        MessagesContainer = (LinearLayout) findViewById(R.id.chat_activity_messages_container);
        MessageTextInput = (EditText) findViewById(R.id.chat_message_text);
        SendMessageButton = (ImageView) findViewById(R.id.chat_send_message_button);


        SendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SendMessage(getIntent().getExtras().getString(Constants.FragmentConstants.ChatActivity.To_UserObjectId), MessageTextInput.getText().toString());
            }
        });
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    private void SendMessage(String ReceiverObjectId, String MessageText) {
        ParseQuery pushQuery = ParseInstallation.getQuery();

        AppendNewMessage(MessageText, true);

        pushQuery.whereEqualTo("user_objectid", ReceiverObjectId);

        ParsePush push = new ParsePush();

        push.setQuery(pushQuery);
        try {
            JSONObject data = new JSONObject();
            data.put("action", "com.closeby.push");
            data.put("msg", MessageText);
            push.setData(data);
            push.sendInBackground();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Toast.makeText(this, "Receiver Registered", Toast.LENGTH_SHORT).show();
        IntentFilter intentFilter = new IntentFilter("com.closeby.push");
        intentFilter.addAction("com.closeby.push");

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(ChatActivity.this, "Received on Activity", Toast.LENGTH_SHORT).show();

                try {
                    JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

                    AppendNewMessage(json.getString("msg"), false);
                } catch (JSONException e) {

                }
            }
        };
        //registering our receiver
        this.registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(this.receiver);
    }

    private void AppendNewMessage(String text, Boolean MessageType) {
        Fragment MessageFragment = new MessageItem();
        Bundle args = new Bundle();
        args.putString(Constants.FragmentConstants.MessageItem.MessageText, text);
        args.putBoolean(Constants.FragmentConstants.MessageItem.CurrentUserMessage, MessageType);
        MessageFragment.setArguments(args);

        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.add(R.id.chat_activity_messages_container, MessageFragment);
        trans.commit();
    }

    class MessageItem extends Fragment {
        TextView MainText;
        LinearLayout MainLayout, RightCaret , LeftCaret;

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.message_item, container, false);

            Typeface Bosun = Typeface.createFromAsset(getActivity().getAssets(), "fonts/bosun03.otf");

            MainLayout = (LinearLayout) v.findViewById(R.id.message_item_main_layout);
            MainText = (TextView) v.findViewById(R.id.message_item_text);
            RightCaret = (LinearLayout) v.findViewById(R.id.message_item_rightCaret);
            LeftCaret = (LinearLayout) v.findViewById(R.id.message_item_leftCaret);

            String message = getArguments().getString(Constants.FragmentConstants.MessageItem.MessageText);
            MainText.setText(message);
            MainText.setTypeface(Bosun);

            Boolean CurrentUserMessage = getArguments().getBoolean(Constants.FragmentConstants.MessageItem.CurrentUserMessage);

            if(!CurrentUserMessage) {
                MainLayout.setGravity(Gravity.LEFT);
                MainLayout.setBackgroundColor(Color.parseColor("#e74c3c"));
                RightCaret.setVisibility(View.GONE);
            } else {
                LeftCaret.setVisibility(View.GONE);
            }
            return v;
        }
    }

}

