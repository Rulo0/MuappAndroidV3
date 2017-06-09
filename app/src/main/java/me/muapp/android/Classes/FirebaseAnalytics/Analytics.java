package me.muapp.android.Classes.FirebaseAnalytics;

import me.muapp.android.BuildConfig;

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
            Login_Loading;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
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
            Dismiss;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
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
            Crush;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
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
            Voice_Note_Listening;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
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
            Match_Notification;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
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
            Report;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
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
            Phone;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
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
            RateFriend;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
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

    public static class ShareCode {
        public enum SHARE_CODE_EVENT {
            ShareCode;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
        }

        public enum SHARE_CODE_PROPERTY {
            Screen
        }

        public enum SHARE_CODE_SCREEN {
            Gate_Man,
            My_Profile
        }
    }

    public static class MyProfileVoiceNote {
        public enum MY_PROFILE_VOICENOTE_EVENT {
            My_Profile_VoiceNote;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
        }

        public enum MY_PROFILE_VOICENOTE_PROPERTY {
            Action
        }

        public enum MY_PROFILE_VOICENOTE_ACTION {
            Record,
            Delete,
            Publish
        }

    }

    public static class EditInfo {
        public enum EDIT_INFO_EVENT {
            Edit_Info_Photos,
            Edit_Info_Description;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
        }
    }

    public static class Settings {
        public enum SETTINGS_EVENT {
            Settings_Distance,
            Settings_Age,
            Settings_PersonalInfo,
            Settings_Notifications,
            Settings_Rate,
            Settings_Terms,
            Settings_Help,
            Settings_LogOut,
            Settings_DeleteAccount;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
        }

        public enum SETTINGS_PROPERTY {
            Status,
            Type
        }

        public enum SETTINGS_STATUS {
            On,
            Off
        }

        public enum SETTINGS_TYPE {
            LastName,
            Education,
            Work,
            Matches,
            Messages
        }
    }

    public static class Gate_Woman {
        public enum GATE_WOMAN_EVENT {
            Gate_Woman_Loading,
            Gate_Woman,
            Gate_Woman_Enlarge_Candidate;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
        }
    }

    public static class Gate_Man {
        public enum GATE_MAN_EVENT {
            Gate_Man_Start,
            Gate_Man_Validate_Code,
            Gate_Man_Correct_Code;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
        }
    }

    public static class Conversation_Profile {
        public enum CONVERSATION_PROFILE_EVENT {
            Conversation_Profile;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
        }

        public enum CONVERSATION_PROFILE_PROPERTY {
            Type
        }

        public enum CONVERSATION_PROFILE_TYPE {
            Crushed,
            Matched
        }
    }

    public static class Messages {
        public enum MESSAGES_EVENT {
            Messages;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
        }

        public enum MESSAGE_PROPERTY {
            Conversation,
            Type,
            First
        }

        public enum MESSAGE_CONVERSATION {
            Crush,
            Match
        }

        public enum MESSAGE_TYPE {
            Text,
            Photo,
            VoiceNote
        }
    }

    public static class My_Profile_Add {
        public enum MY_PROFILE_ADD_EVENT {
            My_Profile_Add,
            My_Profile_Delete;

            @Override
            public String toString() {
                if (BuildConfig.DEBUG) {
                    return "dev_" + super.toString();
                } else {
                    return super.toString();
                }
            }
        }

        public enum MY_PROFILE_ADD_PROPERTY {
            Type,
            Comment,
            Publish
        }

    }
}
