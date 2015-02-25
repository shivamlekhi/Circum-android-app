package com.closeby;

/**
 * Created by Sam on 7/3/2014.
 */
public class Constants {
    public class ParseConstants {
        public class User {
            public static final String Classname = "_User";
            public static final String Name = "name";
            public static final String Username = "username";
            public static final String Email = "email";
            public static final String Pings = "pings";
            public static final String PingBacks = "ping_backs";
            public static final String Online = "online";
            public static final String LastLocation = "last_location";
        }

        public class Push {
            public static final String PushMessage = "com.closeby.push_message";
            public static final String PushPoke = "com.closeby.push_poke";
        }

        public class Pokes {
            public static final String From = "from";
            public static final String To = "to";
            public static final String Reply = "reply";

        }
    }

    public static final String LOG_TAG = "com.circum.custom";

    public class FragmentConstants {
        public static final String CurrentUserLocation_Latitude = "CurrentUserLocation_Latitude";
        public static final String CurrentUserLocation_Longitude = "CurrentUserLocation_Longitude";
        public static final String VisibleFragment = "Visible_Fragment";

        public class LoginConstants {
            public static final int FacebookActivityCode = 4001;
        }

        public class UserListItem {
            public static final String User_Name = "User_Name";
            public static final String User_ObjectId = "User_ObjectId";
        }

        public class FeedFragment {
            public static final String Tag = "FeedFragment";
        }

        public class ProfileFragment {
            public static final String Tag = "ProfileFragment";
            public static final String UserObjectId = "UserObjectId";
            public static final String LoggedInUserProfile = "LoggedInUserProfile";
        }

        public class ChatActivity {
            public static final String Tag = "ProfileFragment";
            public static final String To_UserObjectId = "UserObjectId";
        }

        public class MessageItem {
            public static final String MessageText = "MessageText";
            public static final String CurrentUserMessage = "CurrentUserMessage";
        }

    }
}
