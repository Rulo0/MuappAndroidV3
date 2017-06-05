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
        public enum MUAPP_EVENT{
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
        public enum CRUSH_EVENT{
            Crush
        }
        public enum CRUSH_SCREEN {
            Matching
        }
        public enum CRUSH_PROPERTY {
            Screen
        }
    }
}
