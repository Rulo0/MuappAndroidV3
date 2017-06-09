package me.muapp.android.UI.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.shinelw.library.ColorArcProgressBar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.CodeRedeemHandler;
import me.muapp.android.Classes.API.Handlers.UserInfoHandler;
import me.muapp.android.Classes.API.Params.AlbumParam;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.CodeRedeemResponse;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.FacebookAlbumsActivity;
import me.muapp.android.UI.Activity.LocationCheckerActivity;
import me.muapp.android.UI.Activity.MainActivity;
import me.muapp.android.UI.Activity.ManGateInfoActivity;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;

import static android.app.Activity.RESULT_OK;
import static me.muapp.android.Classes.Util.Utils.serializeUser;
import static me.muapp.android.UI.Activity.FacebookPhotoDetailActivity.PHOTO_URL;

public class ManGateFragment extends Fragment implements View.OnClickListener {
    public static final int REQUEST_USER_PHOTOS_FACEBOOK = 448;
    private static final String TAG = "ManGateFragment";
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    private User user;
    private OnFragmentInteractionListener mListener;
    ColorArcProgressBar cbp_gate;
    ImageView img_man_gate, img_info_man_gate;
    TextView txt_progress_gate, txt_entrance_invitation_code, txt_man_gate_title, txt_man_invite;
    LinearLayout container_code;
    FloatingActionButton fab_man_edit_photo;
    Button btn_enter_man_gate, btn_action_man_gate;
    private ProgressDialog dialog;


    public ManGateFragment() {
        // Required empty public constructor
    }

    public static ManGateFragment newInstance(User user) {
        ManGateFragment fragment = new ManGateFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_CURRENT_USER);
        }
    }

    void showProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(getContext());
        }
        dialog.setTitle(getString(R.string.lbl_please_wait));
        dialog.setMessage(getString(R.string.lbl_loading_information));
        dialog.setIndeterminate(true);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setCancelable(false);
        dialog.show();
    }

    void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View mainView = inflater.inflate(R.layout.fragment_man_gate, container, false);
        cbp_gate = (ColorArcProgressBar) mainView.findViewById(R.id.cbp_gate);
        img_man_gate = (ImageView) mainView.findViewById(R.id.img_man_gate);
        txt_progress_gate = (TextView) mainView.findViewById(R.id.txt_progress_gate);
        txt_entrance_invitation_code = (TextView) mainView.findViewById(R.id.txt_entrance_invitation_code);
        fab_man_edit_photo = (FloatingActionButton) mainView.findViewById(R.id.fab_man_edit_photo);
        img_info_man_gate = (ImageView) mainView.findViewById(R.id.img_info_man_gate);
        container_code = (LinearLayout) mainView.findViewById(R.id.container_code);
        txt_man_gate_title = (TextView) mainView.findViewById(R.id.txt_man_gate_title);
        txt_man_invite = (TextView) mainView.findViewById(R.id.txt_man_invite);
        btn_enter_man_gate = (Button) mainView.findViewById(R.id.btn_enter_man_gate);
        btn_action_man_gate = (Button) mainView.findViewById(R.id.btn_action_man_gate);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.wtf(TAG, user.getPhoto());
        Glide.with(this).load(user.getPhoto()).placeholder(R.drawable.ic_logo_muapp_no_caption).bitmapTransform(new CropCircleTransformation(getContext())).into(img_man_gate);
        txt_entrance_invitation_code.setOnClickListener(this);
        fab_man_edit_photo.setOnClickListener(this);
        img_info_man_gate.setOnClickListener(this);
        btn_action_man_gate.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        setProgressPercent(user.getInvPercentage().intValue());
    }

    private void setProgressPercent(final int value) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                cbp_gate.setCurrentValues(value);
                txt_progress_gate.setText(value + " %");
                if (value >= 60) {
                    container_code.setVisibility(View.GONE);
                }
                if (value == 100) {
                    txt_man_invite.setText(getString(R.string.lbl_click_on_start));
                    btn_action_man_gate.setVisibility(View.INVISIBLE);
                    btn_action_man_gate.setOnClickListener(null);
                    btn_enter_man_gate.setVisibility(View.VISIBLE);
                    btn_enter_man_gate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            JSONObject userVerified = new JSONObject();
                            user.setPending(false);
                            new UserHelper(getContext()).saveUser(user);
                            try {
                                userVerified.put("pending", false);
                                new APIService(getContext()).patchUser(userVerified, new UserInfoHandler() {
                                    @Override
                                    public void onSuccess(int responseCode, String userResponse) {

                                    }

                                    @Override
                                    public void onSuccess(int responseCode, User user) {

                                    }

                                    @Override
                                    public void onFailure(boolean isSuccessful, String responseString) {

                                    }
                                });
                            } catch (Exception x) {
                            }
                            FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Gate_Man.GATE_MAN_EVENT.Gate_Man_Start.toString(), null);
                            Intent i = new Intent(getContext(), Utils.hasLocationPermissions(getContext()) ? MainActivity.class : LocationCheckerActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(i);
                            getActivity().finish();
                        }
                    });
                    txt_man_gate_title.setText(getString(R.string.txt_man_gate_youre_in));
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txt_entrance_invitation_code:
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(getString(R.string.lbl_insert_code));
                final EditText input = new EditText(getContext());
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);
                builder.setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(input.getText().toString()))
                            redeemCode(input.getText().toString());
                        dialog.dismiss();
                    }
                });
                builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                builder.show();
                break;
            case R.id.fab_man_edit_photo:
                startActivityForResult(new Intent(getContext(), FacebookAlbumsActivity.class), REQUEST_USER_PHOTOS_FACEBOOK);
                break;
            case R.id.img_info_man_gate:
                startActivity(new Intent(getContext(), ManGateInfoActivity.class));
                break;
            case R.id.btn_action_man_gate:
                attempShareCode();
                break;
        }
    }

    private void attempShareCode() {
        int numberInvitation = 3;
        try {
            if (user.getInvPercentage() == 0) {
                numberInvitation = 3;
            } else if (user.getInvPercentage() <= 20) {
                numberInvitation = 2;
            } else if (user.getInvPercentage() <= 40) {
                numberInvitation = 1;
            } else {
                numberInvitation = 0;
            }
        } catch (Exception x) {
        }
        Bundle shareBundle = new Bundle();
        shareBundle.putString(Analytics.ShareCode.SHARE_CODE_PROPERTY.Screen.toString(), Analytics.ShareCode.SHARE_CODE_SCREEN.Gate_Man.toString());
        FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.ShareCode.SHARE_CODE_EVENT.ShareCode.toString(), shareBundle);
        new AlertDialog.Builder(getContext())
                .setTitle(getString(R.string.lbl_share_dialog_title))
                .setMessage(user.getCodeUser() + " " + String.format(getString(R.string.lbl_share_dialog_content), numberInvitation, 20)).setPositiveButton(getString(R.string.lbl_share), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String message = String.format(getString(R.string.lbl_use_my_code) + " '" + user.getCodeUser() + "' " + getString(R.string.lbl_use_code_register) + "  https://app.muapp.me/download", 20);
                sharingIntent.putExtra(Intent.EXTRA_TEXT, message);
                startActivity(Intent.createChooser(sharingIntent, getString(R.string.lbl_share)));
            }
        }).setNegativeButton(getString(android.R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_USER_PHOTOS_FACEBOOK:
                    if (data.hasExtra(PHOTO_URL)) {
                        uploadPhoto(data.getStringExtra(PHOTO_URL));
                    }
            }
        }
    }

    private void uploadPhoto(final String photoUrl) {
        Handler mainHandler = new Handler(getContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                User usr = new UserHelper(getContext()).getLoggedUser();
                usr.setPhoto(photoUrl);
                new UserHelper(getContext()).saveUser(usr);
                Glide.with(getContext()).load(usr.getPhoto()).bitmapTransform(new CropCircleTransformation(getContext())).into(img_man_gate);
            }
        };
        mainHandler.post(myRunnable);
        final List<AlbumParam> albumParams = new ArrayList<>();
        AlbumParam albumParam = new AlbumParam();
        albumParam.setUrl(photoUrl);
        albumParams.add(albumParam);
        new APIService(getContext()).uploadPhotos(albumParams, new UserInfoHandler() {
            @Override
            public void onSuccess(int responseCode, String userResponse) {
                Log.wtf(TAG, userResponse);
                try {
                    JSONObject response = new JSONObject(userResponse);
                    if (response.has("user")) {
                        Gson gson = new Gson();
                        final User u = gson.fromJson(serializeUser(response.getJSONObject("user")), User.class);
                        if (u != null) {
                            Handler mainHandler = new Handler(getContext().getMainLooper());
                            Runnable myRunnable = new Runnable() {
                                @Override
                                public void run() {
                                    new UserHelper(getContext()).saveUser(u);
                                }
                            };
                            mainHandler.post(myRunnable);
                        } else {
                            Log.wtf(TAG, "user is null");
                        }
                    }

                } catch (Exception x) {
                    Log.wtf(TAG, x.getMessage());
                    x.printStackTrace();
                }
            }

            @Override
            public void onSuccess(int responseCode, User user) {

            }

            @Override
            public void onFailure(boolean isSuccessful, String responseString) {
                Log.wtf(TAG, responseString);
            }
        });
    }

    private void redeemCode(final String code) {
        FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Gate_Man.GATE_MAN_EVENT.Gate_Man_Validate_Code.toString(), null);
        showProgressDialog();
        final String TAG = "REDEEM CODE";
        new APIService(getContext()).redeemInvitationCode(code, new CodeRedeemHandler() {
            @Override
            public void onSuccess(int responseCode, CodeRedeemResponse redeemResponse) {
                Log.i(TAG, redeemResponse.toString());
                hideProgressDialog();
                if (redeemResponse.getAuthorization()) {
                    FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Gate_Man.GATE_MAN_EVENT.Gate_Man_Correct_Code.toString(), null);
                    user.setHasUseInvitation(redeemResponse.getHasUseInvitation());
                    int finalPercent = user.getInvPercentage() + redeemResponse.getGotPercentage();
                    if (finalPercent > 100) {
                        finalPercent = 100;
                    }
                    user.setInvPercentage(finalPercent);
                    new UserHelper(getContext()).saveUser(user);
                    setProgressPercent(finalPercent);
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    builder.setTitle(getString(R.string.lbl_invalid_code))
                            .setMessage(String.format(getString(R.string.lbl_invalid_code_description), code))
                            .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            builder.show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(boolean isSuccessful, String responseString) {
                Log.i(TAG, responseString);
                hideProgressDialog();
            }
        });
    }
}
