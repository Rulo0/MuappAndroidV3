package me.muapp.android.Classes.API;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import me.muapp.android.Classes.API.Handlers.CodeRedeemHandler;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.API.Params.AlbumParam;
import me.muapp.android.Classes.Internal.CodeRedeemResponse;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.PreferenceHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static me.muapp.android.Classes.Internal.User.getNullUser;
import static me.muapp.android.Classes.Util.Utils.serializeUser;

/**
 * Created by rulo on 22/03/17.
 */

public class APIService {
    OkHttpClient client = new OkHttpClient();
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

    public void confirmUser(User user, final UserInfoHandler handler) {
        String url = BASE_URL + "user";
        MediaType mediaType = MediaType.parse("application/json");
        JSONObject sendObject = new JSONObject();
        try {
            JSONObject jsonUser = new JSONObject();
            jsonUser.put("push_token", user.getPushToken());
            jsonUser.put("birthday", user.getBirthday());
            jsonUser.put("gender", user.getGender());
            jsonUser.put("android", true);
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
                                if (handler != null)
                                    handler.onSuccess(response.code(), response.body().string());
                                try {
                                    JSONObject serverResponse = new JSONObject(response.body().string());
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
