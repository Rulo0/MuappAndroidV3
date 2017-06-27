package me.muapp.android.Classes.API;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import me.muapp.android.Classes.API.Handlers.CandidatesHandler;
import me.muapp.android.Classes.API.Handlers.CodeRedeemHandler;
import me.muapp.android.Classes.API.Handlers.LikeUserHandler;
import me.muapp.android.Classes.API.Handlers.MatchingUsersHandler;
import me.muapp.android.Classes.API.Handlers.MuappUserInfoHandler;
import me.muapp.android.Classes.API.Handlers.MutualFriendsHandler;
import me.muapp.android.Classes.API.Handlers.StickersHandler;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.API.Handlers.UserQualificationHandler;
import me.muapp.android.Classes.API.Handlers.UserQualificationsHandler;
import me.muapp.android.Classes.API.Handlers.UserReportHandler;
import me.muapp.android.Classes.API.Params.AlbumParam;
import me.muapp.android.Classes.Chat.MuappStickers;
import me.muapp.android.Classes.Internal.CandidatesResult;
import me.muapp.android.Classes.Internal.CodeRedeemResponse;
import me.muapp.android.Classes.Internal.LikeUserResult;
import me.muapp.android.Classes.Internal.MatchingResult;
import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.Classes.Internal.MuappQualifications.UserQualifications;
import me.muapp.android.Classes.Internal.MutualFriends;
import me.muapp.android.Classes.Internal.QualificationResult;
import me.muapp.android.Classes.Internal.ReportResult;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.Log;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static me.muapp.android.Classes.Internal.User.getNullUser;
import static me.muapp.android.Classes.Util.Utils.serializeMatchingUsers;
import static me.muapp.android.Classes.Util.Utils.serializeUser;

/**
 * Created by rulo on 22/03/17.
 */

public class APIService {
    OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    Context mContext;
    SimpleDateFormat dateFormat;
    private static final String BASE_URL = "http://dev.muapp.me/"; //BuildConfig.DEBUG ? "http://dev.muapp.me/" : "https://app.muapp.me/";

    public APIService(Context mContext) {
        this.mContext = mContext;
        dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ");
    }

    public void loginToMuapp(final UserInfoHandler handler) {
        String url = BASE_URL + "user";
        PreferenceHelper helper = new PreferenceHelper(mContext);
        if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (handler != null)
                        handler.onFailure(false, e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseString = response.body().string();
                    if (handler != null)
                        handler.onSuccess(response.code(), responseString);
                    try {
                        JSONObject serverResponse = new JSONObject(responseString);
                        if (serverResponse.has("user")) {
                            Gson gson = new Gson();
                            User u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), User.class);
                            if (u != null) {
                                if (handler != null)
                                    handler.onSuccess(response.code(), u);
                            } else {
                               }
                        }
                    } catch (Exception x) {
                        if (handler != null)
                            handler.onSuccess(response.code(), responseString);
                        x.printStackTrace();
                    }

                }
            });
        } else {
            if (handler != null)
                handler.onFailure(false, "User not logged");
        }
    }

    public void confirmUser(User user, Location location, final UserInfoHandler handler) {
        String url = BASE_URL + "user";
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject sendObject = new JSONObject();
        try {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("push_token", user.getPushToken());
            jsonUser.put("birthday", user.getBirthday());
            jsonUser.put("gender", user.getGender());
            jsonUser.put("android", true);
            if (location.getLatitude() != 0 && location.getLongitude() != 0) {
                jsonUser.put("latitude", String.valueOf(location.getLatitude()));
                jsonUser.put("longitude", String.valueOf(location.getLongitude()));
            }
            sendObject.put("user", jsonUser);
        } catch (Exception x) {
        }
        RequestBody body = RequestBody.create(mediaType, sendObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (handler != null)
                    handler.onFailure(false, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                if (handler != null)
                    handler.onSuccess(response.code(), responseString);
                try {
                    JSONObject serverResponse = new JSONObject(responseString);
                    if (serverResponse.has("user")) {
                        Gson gson = new Gson();
                        User u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), User.class);
                        if (u != null) {
                            if (handler != null)
                                handler.onSuccess(response.code(), u);
                        } else {
                            }
                    }
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        });
    }

    public void likeUser(int userId, String qbDialogId, final LikeUserHandler handler, String myConversationKey, String opponentConversationKey) {
        String url = BASE_URL + String.format("users/%s/like", userId);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject sendObject = new JSONObject();
        try {
            JSONObject qbParams = new JSONObject();
         /*   qbParams.put("dialog_id", qbDialogId != null ? qbDialogId : "");
            sendObject.put("quickblox", qbParams);*/
            if (!TextUtils.isEmpty(myConversationKey) && !TextUtils.isEmpty(opponentConversationKey)) {
                JSONObject firebaseParams = new JSONObject();
                firebaseParams.put("current_user_conversation_id", myConversationKey);
                firebaseParams.put("opponent_conversation_id", opponentConversationKey);
                sendObject.put("firebase", firebaseParams);
            }
        } catch (Exception x) {
        }
        RequestBody body = RequestBody.create(mediaType, sendObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
         final String demoMatch = "{\"dialog_key\":" + myConversationKey + ",\"message\":\"Tu selección ha sido registrada con éxito.\"}";
        client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (handler != null) {
                    handler.onFailure(false, e.getMessage());
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseString = response.body().string();
                    try {
                        LikeUserResult result = new Gson().fromJson(responseString, LikeUserResult.class);
                        if (result != null)
                            if (handler != null) {
                                handler.onSuccess(response.code(), result);
                            } else {
                                if (handler != null)
                                    handler.onFailure(true, responseString);
                            }
                    } catch (Exception x) {
                        if (handler != null)
                            handler.onFailure(true, x.getMessage());
                    }
                } else {
                    if (handler != null)
                        handler.onFailure(true, response.message().toString());
                }
            }
        });
    }

    public void reportUser(int userId, int reportReason, final UserReportHandler handler) {
        String url = BASE_URL + String.format("users/%s/report", userId);
        MediaType mediaType = MediaType.parse("application/json");
        OkHttpClient client = new OkHttpClient();
        JSONObject sendObject = new JSONObject();
        try {
            sendObject.put("reason", reportReason);
        } catch (Exception x) {
        }
        RequestBody body = RequestBody.create(mediaType, sendObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
       client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (handler != null)
                    handler.onFailure(false, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                 try {
                    if (handler != null)
                        handler.onSuccess(response.code(), new Gson().fromJson(responseString, ReportResult.class));
                } catch (Exception x) {
                    if (handler != null)
                        handler.onFailure(true, x.getMessage());
                }
            }
        });
    }

    public void dislikeUser(int userId, final UserInfoHandler handler) {
        String url = BASE_URL + String.format("users/%s/dislike", userId);
        OkHttpClient client = new OkHttpClient();
        RequestBody emptyBody = RequestBody.create(null, new byte[0]);
        Request request = new Request.Builder()
                .url(url)
                .post(emptyBody)
                .build();
         client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (handler != null)
                    handler.onFailure(false, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                if (handler != null)
                    handler.onSuccess(response.code(), responseString);
                try {
                    JSONObject serverResponse = new JSONObject(responseString);
                    if (serverResponse.has("user")) {
                        Gson gson = new Gson();
                        User u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), User.class);
                        if (u != null) {
                             if (handler != null)
                                handler.onSuccess(response.code(), u);
                        } else {
                         }
                    }
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        });
    }


    public void patchUser(JSONObject userData, final UserInfoHandler handler) {
        String url = BASE_URL + "user";
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject sendObject = new JSONObject();
        try {
            userData.put("android", true);
            sendObject.put("user", userData);
        } catch (Exception x) {
        }
        RequestBody body = RequestBody.create(mediaType, sendObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .patch(body)
                .build();
      client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (handler != null)
                    handler.onFailure(false, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                if (handler != null)
                    handler.onSuccess(response.code(), responseString);
                try {
                    JSONObject serverResponse = new JSONObject(responseString);
                    if (serverResponse.has("user")) {
                        Gson gson = new Gson();
                        User u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), User.class);
                        if (u != null) {
                            new UserHelper(mContext).saveUser(u);
                            if (handler != null)
                                handler.onSuccess(response.code(), u);
                        } else {
                             }
                    }

                } catch (Exception x) {
                    x.printStackTrace();
                }


            }
        });
    }

    public void setUserFakeAccount(Boolean fakeAccount, final UserInfoHandler handler) {
        String url = BASE_URL + "user/fake_account";
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject sendObject = new JSONObject();
        try {
            JSONObject userData = new JSONObject();
            userData.put("authentication", fakeAccount);
            sendObject.put("fake_params", userData);
        } catch (Exception x) {
        }
        RequestBody body = RequestBody.create(mediaType, sendObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (handler != null)
                    handler.onFailure(false, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                if (handler != null)
                    handler.onSuccess(response.code(), responseString);
                try {
                    JSONObject serverResponse = new JSONObject(responseString);
                    if (serverResponse.has("user")) {
                        Gson gson = new Gson();
                        User u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), User.class);
                        if (u != null) {
                            if (handler != null)
                                handler.onSuccess(response.code(), u);
                        } else {
                         }
                    }
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }
        });
    }

    public void setUserQualification(int targetUserId, int stars, final UserQualificationHandler handler) {
        String url = BASE_URL + "qualification/set_qualification";
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject sendObject = new JSONObject();
        try {
            JSONObject qualificationaram = new JSONObject();
            qualificationaram.put("stars", stars);
            qualificationaram.put("qualificationed_id", targetUserId);
            sendObject.put("qualification", qualificationaram);
        } catch (Exception x) {
        }
        RequestBody body = RequestBody.create(mediaType, sendObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
         client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (handler != null)
                    handler.onFailure(false, e.getMessage());
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseString = response.body().string();
                 try {
                    if (handler != null)
                        handler.onSuccess(response.code(), new Gson().fromJson(responseString, QualificationResult.class));
                } catch (Exception x) {
                    if (handler != null)
                        handler.onFailure(true, x.getMessage());
                }
            }
        });
    }


    public void redeemInvitationCode(String code, final CodeRedeemHandler handler) {
        String url = BASE_URL + String.format("user/validate_code/%s", code);
        PreferenceHelper helper = new PreferenceHelper(mContext);
        if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (handler != null)
                        handler.onFailure(false, e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Gson gson = new Gson();
                    CodeRedeemResponse codeRedeemResponse = gson.fromJson(response.body().string(), CodeRedeemResponse.class);
                    if (handler != null)
                        handler.onSuccess(response.code(), codeRedeemResponse);

                }
            });
        } else {
            if (handler != null)
                handler.onFailure(false, "User not logged");
        }
    }

    public void deleteUser(final UserInfoHandler handler) {
        String url = BASE_URL + "user";
        PreferenceHelper helper = new PreferenceHelper(mContext);
        if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
            Request request = new Request.Builder()
                    .url(url)
                    .delete()
                    .build();
            client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (handler != null)
                        handler.onFailure(false, e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseString = response.body().string();
                    if (handler != null)
                        handler.onSuccess(response.code(), responseString);
                    try {
                        JSONObject serverResponse = new JSONObject(responseString);
                        if (serverResponse.has("user")) {
                            Gson gson = new Gson();
                            User u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), User.class);
                            if (u != null) {
                                 if (handler != null)
                                    handler.onSuccess(response.code(), u);
                            } else {
                                handler.onSuccess(response.code(), getNullUser);
                            }
                        }

                    } catch (Exception x) {
                        x.printStackTrace();
                    }
                }
            });
        } else {
            if (handler != null)
                handler.onFailure(false, "User not logged");
        }
    }

    public void uploadPhotos(List<AlbumParam> albumParams, final UserInfoHandler handler) {
        String url = BASE_URL + "user";
        final OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .cache(null)
                .readTimeout(60, TimeUnit.SECONDS).build();
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        int index = 0;
        for (AlbumParam albumParam : albumParams) {
            if (albumParam.getFileBytes() != null) {
                builder.addFormDataPart(String.format("user[album][%d][image]", index), String.format("%d.jpg", index), RequestBody.create(MediaType.parse("image/jpeg"), albumParam.getFileBytes()));
                index++;

            } else if (albumParam.getUrl() != null) {
                builder.addFormDataPart(String.format("user[album][%d][url]", index), albumParam.getUrl());
                index++;
            }
        }
        RequestBody requestBody = builder.build();
        final Request request = new Request.Builder()
                .url(url)
                .patch(requestBody).build();
        try {
            new Thread() {
                @Override
                public void run() {
                    try {
                        client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                if (handler != null)
                                    handler.onFailure(false, e.getMessage());
                                e.printStackTrace();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String responseString = response.body().string();
                                if (handler != null)
                                    handler.onSuccess(response.code(), responseString);
                                try {
                                    JSONObject serverResponse = new JSONObject(responseString);
                                    if (serverResponse.has("user")) {
                                        Gson gson = new Gson();
                                        User u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), User.class);
                                        if (u != null) {
                                             if (handler != null)
                                                handler.onSuccess(response.code(), u);
                                        } else {
                                           }
                                    }

                                } catch (Exception x) {
                                    Log.wtf("uploadPhotos", x.getMessage());
                                    x.printStackTrace();
                                }
                            }
                        });

                    } catch (Exception e) {
                        Log.e("API", e.getMessage());
                    }
                }
            }.start();
        } catch (Exception e) {
            Log.e("API", e.getMessage());
        }
    }

    public void getFullUser(int userId, final MuappUserInfoHandler handler) {
        String url = BASE_URL + String.format("users/%s/profile_view", userId);
        PreferenceHelper helper = new PreferenceHelper(mContext);
        if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Log.i("getFullUser", url);
            client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (handler != null)
                        handler.onFailure(false, e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseString = response.body().string();
                    Log.wtf("getFullUser", responseString.toString());
                    if (handler != null)
                        try {
                            JSONObject serverResponse = new JSONObject(responseString);
                            if (serverResponse.has("user")) {
                                Gson gson = new Gson();
                                MatchingUser u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), MatchingUser.class);
                                if (u != null) {
                                    Log.wtf("getFullUser", u.toString());
                                    if (handler != null)
                                        handler.onSuccess(response.code(), u);
                                } else {
                                    Log.wtf("getFullUser", "user is null");
                                }
                            }
                        } catch (Exception x) {
                            if (handler != null)
                                handler.onFailure(true, x.getMessage());
                            Log.wtf("getFullUser", x.getMessage());
                            x.printStackTrace();
                        }

                }
            });
        } else {
            if (handler != null)
                handler.onFailure(false, "User not logged");
        }
    }

    public void getUserQualifications(int userId, final UserQualificationsHandler handler) {
        try {
            String url = BASE_URL + String.format("qualification/get_qualifications/%s", userId);
            PreferenceHelper helper = new PreferenceHelper(mContext);
            if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
                Request request = new Request.Builder()
                        .url(url)
                        .get()
                        .build();
                Log.i("getUserQualifications", url);
                client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (handler != null)
                            handler.onFailure(false, e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseString = response.body().string();
                        Log.wtf("getUserQualifications", responseString.toString());
                        UserQualifications qualifications = new Gson().fromJson(responseString, UserQualifications.class);
                        if (qualifications != null) {
                            if (handler != null) {
                                handler.onSuccess(response.code(), qualifications);
                            }
                        }
                    }
                });
            } else {
                if (handler != null)
                    handler.onFailure(false, "User not logged");
            }
        } catch (Exception x) {
            Log.wtf("getUserQualifications", x.getMessage());
            x.printStackTrace();
        }
    }

    public void getMatchingUsers(int page, Location userLocation, final MatchingUsersHandler handler) {
        try {
            String url = BASE_URL + String.format("user/search?page=%s", page);
            MediaType mediaType = MediaType.parse("application/json");
            PreferenceHelper helper = new PreferenceHelper(mContext);
            if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
                JSONObject sendObject = new JSONObject();
                try {
                    JSONObject location = new JSONObject();
                    location.put("latitude", userLocation != null ? userLocation.getLatitude() : 0);
                    location.put("longitude", userLocation != null ? userLocation.getLongitude() : 0);
                    sendObject.put("user", location);
                } catch (Exception x) {
                    Log.wtf("getMatchingUsers", x.getMessage());
                }
                Log.wtf("getMatchingUsers", url);
                Log.wtf("getMatchingUsers", sendObject.toString());
                RequestBody body = RequestBody.create(mediaType, sendObject.toString());
                Request request = new Request.Builder()
                        .url(url)
                        .post(body)
                        .build();
                client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (handler != null)
                            handler.onFailure(false, e.getMessage());
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseString = response.body().string();
                        Log.wtf("getMatchingUsers", responseString);
                        MatchingResult matchingResult = new Gson().fromJson(serializeMatchingUsers(responseString), MatchingResult.class);
                        if (matchingResult != null) {
                            if (handler != null) {
                                handler.onSuccess(response.code(), matchingResult);
                            }
                        }
                    }
                });
            } else {
                if (handler != null)
                    handler.onFailure(false, "User not logged");
            }
        } catch (Exception x) {
            Log.wtf("getMatchingUsers", x.getMessage());
            x.printStackTrace();
        }
    }


    public void crushUser(int userId) {
        try {
            String url = BASE_URL + String.format("users/%s/crush", userId);
            PreferenceHelper helper = new PreferenceHelper(mContext);
            if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(null, new byte[0]))
                        .build();
                Log.i("crushUser", url);
                client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseString = response.body().string();
                        Log.wtf("crushUser", responseString.toString());
                    }
                });
            } else {

            }
        } catch (Exception x) {
            Log.wtf("crushUser", x.getMessage());
            x.printStackTrace();
        }
    }

    public void blockUser(int userId) {
        try {
            String url = BASE_URL + String.format("users/%s/block", userId);
            PreferenceHelper helper = new PreferenceHelper(mContext);
            if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
                Request request = new Request.Builder()
                        .url(url)
                        .post(RequestBody.create(null, new byte[0]))
                        .build();
                Log.i("blockUser", url);
                client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        String responseString = response.body().string();
                        Log.wtf("blockUser", responseString.toString());
                    }
                });
            } else {

            }
        } catch (Exception x) {
            Log.wtf("blockUser", x.getMessage());
            x.printStackTrace();
        }
    }

    public void sendPushNotification(String pushToken, String collapseKey, String notification_body, String body_loc_key, String[] body_loc_args) {
        if (TextUtils.isEmpty(pushToken)) {
            OkHttpClient client = new OkHttpClient();
            MediaType mediaType = MediaType.parse("application/json");
            JSONObject sendObject = new JSONObject();
            try {
                sendObject.put("to", pushToken);
                if (!TextUtils.isEmpty(collapseKey))
                    sendObject.put("collapse_key", collapseKey);
                sendObject.put("priority", "high");
                sendObject.put("content_available", true);
                JSONObject notification = new JSONObject();
                if (!TextUtils.isEmpty(collapseKey))
                    notification.put("tag", collapseKey);
                notification.put("title", mContext.getString(R.string.app_name));
                notification.put("color", "#ff666e");
                notification.put("sound", "default");
                if (body_loc_key != null && body_loc_args != null) {
                    notification.put("body_loc_key", "notif_sent_message");
                    notification.put("body_loc_args", body_loc_args);
                } else {
                    notification.put("body", notification_body);
                }
                sendObject.put("notification", notification);
            } catch (Exception x) {

            }
            Log.wtf("Push", sendObject.toString());
            RequestBody body = RequestBody.create(mediaType, sendObject.toString());
            Request request = new Request.Builder()
                    .url("https://fcm.googleapis.com/fcm/send")
                    .post(body)
                    .addHeader("authorization", "key=AIzaSyCAPtbJ8ZFXLF8ot_hyadW2_zqD9E9fMkE")
                    .addHeader("content-type", "application/json")
                    .build();
            Log.wtf("sendPushMessage", sendObject.toString());
            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                }
            });
        }

    }

    public void getCandidates(int page, final CandidatesHandler handler) {
        String url = BASE_URL + "user/candidates/" + page;
        PreferenceHelper helper = new PreferenceHelper(mContext);
        if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Log.i("getCandidates", url);
            client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (handler != null)
                        handler.onFailure(false, e.getMessage());
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseString = response.body().string();
                    Log.wtf("getCandidates", responseString);
                    try {
                        Gson gson = new Gson();
                        CandidatesResult result = gson.fromJson(serializeMatchingUsers(responseString), CandidatesResult.class);
                        if (result != null) {
                            Log.wtf("getCandidates", result.toString());
                            if (handler != null)
                                handler.onSuccess(response.code(), result);
                        } else {
                            Log.wtf("getUserProfile", "user is null");
                        }
                    } catch (Exception x) {
                     /*   if (handler != null)
                            handler.onSuccess(response.code(), responseString);*/
                        Log.wtf("getCandidates", x.getMessage());
                        x.printStackTrace();
                    }

                }
            });
        } else {
            /*if (handler != null)
                handler.onFailure(false, "User not logged");*/
        }
    }

    public void likeCandidate(int candidateId) {
        String url = BASE_URL + String.format("users/%s/candidate_like", candidateId);
        PreferenceHelper helper = new PreferenceHelper(mContext);
        if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
            Request request = new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(null, new byte[0]))
                    .build();
            Log.wtf("likeCandidate", url);
            try {
                client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.wtf("likeCandidate", "Error " + e.getMessage());
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.wtf("likeCandidate", response.body().string());
                    }
                });
            } catch (Exception x) {
                Log.wtf("likeCandidate", "Error " + x.getMessage());
                x.printStackTrace();
            }
        }

    }

    public void getStickers(final StickersHandler handler) {
        String url = BASE_URL + "stickers";
        PreferenceHelper helper = new PreferenceHelper(mContext);
        if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Log.i("getStickers", url);
            client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (handler != null) {
                        handler.onFailure(false, e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseString = response.body().string();
                    Log.wtf("getStickers", responseString);
                    MuappStickers stickers = new Gson().fromJson(responseString, MuappStickers.class);
                    if (stickers != null) {
                        if (handler != null)
                            handler.onSuccess(response.code(), stickers);
                    }
                }
            });
        } else {
            if (handler != null)
                handler.onFailure(false, "User not logged");
        }
    }

    public void getMutualFriends(Integer userId, final MutualFriendsHandler handler) {
        String url = BASE_URL + String.format("users/%s/friendships", userId);
        PreferenceHelper helper = new PreferenceHelper(mContext);
        if (helper.getFacebookToken() != null && helper.getFacebookTokenExpiration() > 0) {
            Request request = new Request.Builder()
                    .url(url)
                    .get()
                    .build();
            Log.i("getMutualFriends", url);
            client.newCall(addAuthHeaders(request)).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    if (handler != null) {
                        handler.onFailure(false, e.getMessage());
                    }
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseString = response.body().string();
                    Log.wtf("getMutualFriends", responseString);
                    MutualFriends friends = new Gson().fromJson(responseString, MutualFriends.class);
                    if (friends != null) {
                        Log.wtf("getMutualFriends", friends.getMutualFriends().size() + "");

                        if (handler != null)
                            handler.onSuccess(response.code(), friends);
                    }
                }
            });
        } else {
            if (handler != null)
                handler.onFailure(false, "User not logged");
        }
    }

    private Request addAuthHeaders(Request mainRequest) {
        PreferenceHelper helper = new PreferenceHelper(mContext);
        String authCredentials = String.format("Token token=%s,expires_in=%s,provider=\"facebook\"", helper.getFacebookToken(), dateFormat.format(new Date(helper.getFacebookTokenExpiration())));
        Request r = mainRequest.newBuilder()
                .addHeader("authorization", authCredentials)
                .addHeader("accept-language", Locale.getDefault().getLanguage())
                .addHeader("qb", "version=1")
                .addHeader("muappapi", "version=4")
                .addHeader("cache-control", "no-cache")
                .build();


        return r;
    }
}
