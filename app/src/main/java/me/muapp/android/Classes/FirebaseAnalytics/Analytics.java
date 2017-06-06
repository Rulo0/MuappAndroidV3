package me.muapp.android.Classes.FirebaseAnalytics;

/**
 * Created by rulo on 5/06/17.
 */

public class Analytics {
    public static class Login {
        public enum LOGIN_EVENTS {
            Login_FB,
            Login_Error,
            Login_Confirm_Confirm,
            Login_Confirm_Synchronise,
            Login_Loading
        }

        public enum LOGIN_TYPE {
            Type
        }

        public enum LOGIN_ERROR_TYPE {
            Friends, Photo, Birthday, Minor, Gender, FBPermissions
        }
    }

    public static class Muapp {
        public enum MUAPP_EVENT {
            Muapp,
            Dismiss
        }

        public enum MUAPP_SCREEN {
            Gate_Woman,
            Matching,
            User_Profile_New,
            User_Profile_Crushed,
            Conversation_Crush
        }

        public enum MUAPP_PROPERTY {
            Type,
            Screen
        }

        public enum MUAPP_TYPE {
            Button
        }
    }

    public static class Crush {
        public enum CRUSH_EVENT {
            Crush
        }

        public enum CRUSH_SCREEN {
            Matching
        }

        public enum CRUSH_PROPERTY {
            Screen
        }
    }

    public static class VoiceNote {
        public enum VOICENOTE_EVENT {
            Voice_Note_Listening
        }

        public enum VOICENOTE_SCREEN {
            My_Profile
        }

        public enum VOICENOTE_PROPERTY {
            Screen
        }
    }

    public static class Match {
        public enum MATCH_EVENT {
            Match,
            Match_Notification
        }

        public enum MATCH_PROPERTY {
            Type,
            Button
        }

        public enum MATCH_TYPE {
            New,
            Crush
        }

        public enum MATCH_BUTTON {
            Ok,
            Conversation
        }
    }

    public static class Report {
        public enum REPORT_EVENT {
            Report
        }

        public enum REPORT_PROPERTY {
            Screen
        }

        public enum REPORT_SCREEN {
            User_Profile_New,
            User_Profile_Matched,
            User_Profile_Crushed
        }

    }

    public static class Phone {
        public enum PHONE_EVENT {
            Phone
        }

        public enum PHONE_PROPERTY {
            Screen
        }

        public enum PHONE_SCREEN {
            Login,
            Settings
        }
    }

    public static class RateFriend {
        public enum RATE_EVENT {
            RateFriend
        }

        public enum RATE_PROPERTY {
            Screen
        }

        public enum RATE_SCREEN {
            Matching,
            User_Profile_Crushed,
            User_Profile_Matched
        }
    }
}
