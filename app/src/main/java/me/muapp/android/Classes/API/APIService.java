package me.muapp.android.Classes.API;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.muapp.android.Classes.API.Handlers.CodeRedeemHandler;
import me.muapp.android.Classes.API.Handlers.LikeUserHandler;
import me.muapp.android.Classes.API.Handlers.MatchingUsersHandler;
import me.muapp.android.Classes.API.Handlers.MuappUserInfoHandler;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.API.Handlers.UserQualificationHandler;
import me.muapp.android.Classes.API.Handlers.UserQualificationsHandler;
import me.muapp.android.Classes.API.Handlers.UserReportHandler;
import me.muapp.android.Classes.API.Params.AlbumParam;
import me.muapp.android.Classes.Internal.CodeRedeemResponse;
import me.muapp.android.Classes.Internal.LikeUserResult;
import me.muapp.android.Classes.Internal.MatchingResult;
import me.muapp.android.Classes.Internal.MuappQualifications.UserQualifications;
import me.muapp.android.Classes.Internal.MuappUser;
import me.muapp.android.Classes.Internal.QualificationResult;
import me.muapp.android.Classes.Internal.ReportResult;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.PreferenceHelper;
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
    private static final String BASE_URL = "http://dev.muapp.me/";

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
            Log.i("getUserProfile", url);
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
                    Log.wtf("getUserProfile", responseString);
                    if (handler != null)
                        handler.onSuccess(response.code(), responseString);
                    try {
                        JSONObject serverResponse = new JSONObject(responseString);
                        if (serverResponse.has("user")) {
                            Gson gson = new Gson();
                            User u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), User.class);
                            if (u != null) {
                                Log.wtf("getUserProfile", u.toString());
                                if (handler != null)
                                    handler.onSuccess(response.code(), u);
                            } else {
                                Log.wtf("getUserProfile", "user is null");
                            }
                        }
                    } catch (Exception x) {
                        if (handler != null)
                            handler.onSuccess(response.code(), responseString);
                        Log.wtf("getUserProfile", x.getMessage());
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
        Log.i("confirmUser", url);
        Log.i("confirmUser", sendObject.toString());

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
                            Log.wtf("confirmUser", u.toString());
                            if (handler != null)
                                handler.onSuccess(response.code(), u);
                        } else {
                            Log.wtf("confirmUser", "user is null");
                        }
                    }
                } catch (Exception x) {
                    Log.wtf("confirmUser", x.getMessage());
                    x.printStackTrace();
                }
            }
        });
    }

    public void likeUser(int userId, String qbDialogId, final LikeUserHandler handler) {
        String url = BASE_URL + String.format("users/%s/like", userId);
        OkHttpClient client = new OkHttpClient();
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject sendObject = new JSONObject();
        try {
            JSONObject qbParams = new JSONObject();
            qbParams.put("dialog_id", qbDialogId != null ? qbDialogId : "");
            sendObject.put("quickblox", qbParams);
        } catch (Exception x) {
        }
        RequestBody body = RequestBody.create(mediaType, sendObject.toString());
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Log.i("likeUser", url);
        Log.i("likeUser", sendObject.toString());
        final String demoMatch = "{\"match\":{\"id\":12608229,\"user_id\":30800,\"matcher_id\":52712,\"match\":true,\"created_at\":\"2017-05-17T15:16:48.301Z\",\"updated_at\":\"2017-05-17T15:16:49.267Z\",\"from_crush\":false,\"user\":{\"education\":null,\"work\":null,\"hometown\":\"Mexico City\",\"location\":null,\"audio_id\":null,\"first_name\":\"Rulo\",\"last_name\":\"Fb\",\"photo\":\"https://s3-eu-west-1.amazonaws.com/muapp-staging//900e5e04481b6d8763aefb6de300e753.jpg\",\"id\":52712,\"album\":[\"https://s3-eu-west-1.amazonaws.com/muapp-staging//900e5e04481b6d8763aefb6de300e753.jpg\"],\"common_friendships\":0,\"longitude\":\"-99.158026\",\"latitude\":\"19.426321\",\"last_seen\":\"2017-05-25T15:14:04.000Z\",\"birthday\":\"1998-03-29\",\"quickblox_id\":\"27772302\",\"active_conversations\":0,\"hours_ago\":24}},\"quickblox_dialog\":\"No existe convesación\",\"message\":\"Tu selección ha sido registrada con éxito.\"}\n";
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
                    //TODO Remove demoMatch
                    String responseString = response.body().string();
                    Log.wtf("LikeResult", responseString);
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
        Log.i("reportUser", url);
        Log.i("reportUser", sendObject.toString());
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
                Log.i("reportUser", responseString);
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
        Log.i("dislikeUser", url);
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
                Log.i("dislikeUser", responseString);
                if (handler != null)
                    handler.onSuccess(response.code(), responseString);
                try {
                    JSONObject serverResponse = new JSONObject(responseString);
                    if (serverResponse.has("user")) {
                        Gson gson = new Gson();
                        User u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), User.class);
                        if (u != null) {
                            Log.wtf("dislikeUser", u.toString());
                            if (handler != null)
                                handler.onSuccess(response.code(), u);
                        } else {
                            Log.wtf("dislikeUser", "user is null");
                        }
                    }
                } catch (Exception x) {
                    Log.wtf("dislikeUser", x.getMessage());
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
        Log.i("patchUser", url);
        Log.i("patchUser", sendObject.toString());
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
                Log.i("patchUser response", responseString);
                if (handler != null)
                    handler.onSuccess(response.code(), responseString);
                try {
                    JSONObject serverResponse = new JSONObject(responseString);
                    if (serverResponse.has("user")) {
                        Gson gson = new Gson();
                        User u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), User.class);
                        if (u != null) {
                            Log.wtf("patchUser", u.toString());
                            if (handler != null)
                                handler.onSuccess(response.code(), u);
                        } else {
                            Log.wtf("patchUser", "user is null");
                        }
                    }

                } catch (Exception x) {
                    Log.wtf("getUserProfile", x.getMessage());
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
        Log.i("setUserFakeAccount", url);
        Log.i("setUserFakeAccount", sendObject.toString());
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
                            Log.wtf("setUserFakeAccount", u.toString());
                            if (handler != null)
                                handler.onSuccess(response.code(), u);
                        } else {
                            Log.wtf("setUserFakeAccount", "user is null");
                        }
                    }
                } catch (Exception x) {
                    Log.wtf("setUserFakeAccount", x.getMessage());
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
        Log.i("setUserQualification", url);
        Log.i("setUserQualification", sendObject.toString());
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
                Log.i("setUserQualification", responseString);
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
            Log.i("redeemInvitationCode", url);
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
            Log.i("deleteUser", url);
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
                                Log.wtf("deleteUser", u.toString());
                                if (handler != null)
                                    handler.onSuccess(response.code(), u);
                            } else {
                                Log.wtf("deleteUser", "user is null");
                                handler.onSuccess(response.code(), getNullUser);
                            }
                        }

                    } catch (Exception x) {
                        Log.wtf("deleteUser", x.getMessage());
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
                                            Log.wtf("uploadPhotos", u.toString());
                                            if (handler != null)
                                                handler.onSuccess(response.code(), u);
                                        } else {
                                            Log.wtf("uploadPhotos", "user is null");
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

    public void getFullUser(Long userId, final MuappUserInfoHandler handler) {
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
                        handler.onSuccess(response.code(), responseString);
                    try {
                        JSONObject serverResponse = new JSONObject(responseString);
                        if (serverResponse.has("user")) {
                            Gson gson = new Gson();
                            MuappUser u = gson.fromJson(serializeUser(serverResponse.getJSONObject("user")), MuappUser.class);
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
                            handler.onSuccess(response.code(), responseString);
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
                    Log.d("getMatchingUsers", x.getMessage());
                }
                Log.d("getMatchingUsers", sendObject.toString());
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
            Log.d("getMatchingUsers", x.getMessage());
            x.printStackTrace();
        }
    }

    public void sendPushNotification( String pushToken, String collapseKey, String notification_body, String body_loc_key, String[] body_loc_args) {
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

    private Request addAuthHeaders(Request mainRequest) {
        PreferenceHelper helper = new PreferenceHelper(mContext);
        String authCredentials = String.format("Token token=%s,expires_in=%s,provider=\"facebook\"", helper.getFacebookToken(), dateFormat.format(new Date(helper.getFacebookTokenExpiration())));
        Request r = mainRequest.newBuilder()
                .addHeader("authorization", authCredentials)
                .addHeader("accept-language", "es")
                .addHeader("qb", "version=1")
                .addHeader("muappapi", "version=3")
                .build();
        Log.i("authorization", authCredentials);
        Log.i("accept-language", "es");
        Log.i("qb", "version=1");
        Log.i("muappapi", "version=3");
        return r;
    }
}
