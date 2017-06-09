package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.LikeUserHandler;
import me.muapp.android.Classes.API.Handlers.MatchingUsersHandler;
import me.muapp.android.Classes.Chat.Conversation;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.LikeUserResult;
import me.muapp.android.Classes.Internal.MatchingResult;
import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.ChatActivity;
import me.muapp.android.UI.Activity.MainActivity;
import me.muapp.android.UI.Activity.MatchActivity;
import me.muapp.android.UI.Fragment.Interface.OnAllUsersLoadedListener;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;
import me.muapp.android.UI.Fragment.Interface.OnMatchingInteractionListener;
import me.muapp.android.UI.Fragment.Interface.OnProfileScrollListener;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;
import static me.muapp.android.UI.Activity.ChatActivity.CONVERSATION_EXTRA;
import static me.muapp.android.UI.Activity.MatchActivity.FROM_MATCH;
import static me.muapp.android.UI.Activity.MatchActivity.MATCHING_CONVERSATION;
import static me.muapp.android.UI.Activity.MatchActivity.MATCHING_USER;


public class MatchingFragment extends Fragment implements OnFragmentInteractionListener, MatchingUsersHandler, OnMatchingInteractionListener, OnAllUsersLoadedListener, View.OnClickListener, OnProfileScrollListener, LikeUserHandler {
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    private User user;
    private OnFragmentInteractionListener mListener;
    Handler handler;
    int matchingUsersPage = 1;
    int waitTime = 10;
    List<MatchingUserProfileFragment> matchingFragmentList = new ArrayList<>();
    RelativeLayout container_actions_matching;
    ImageButton btn_muapp_matching, btn_crush_matching, btn_no_muapp_matching;
    View content_matching_profiles;
    private Fragment currentFragment;
    PreferenceHelper preferenceHelper;


    public MatchingFragment() {

    }

    public void performUnlike() {
        btn_no_muapp_matching.performClick();
    }

    public static MatchingFragment newInstance(User user) {
        MatchingFragment fragment = new MatchingFragment();
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
        preferenceHelper = new PreferenceHelper(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_matching, container, false);
        container_actions_matching = (RelativeLayout) v.findViewById(R.id.container_actions_matching);
        btn_muapp_matching = (ImageButton) v.findViewById(R.id.btn_muapp_matching);
        btn_crush_matching = (ImageButton) v.findViewById(R.id.btn_crush_matching);
        btn_no_muapp_matching = (ImageButton) v.findViewById(R.id.btn_no_muapp_matching);
        content_matching_profiles = v.findViewById(R.id.content_matching_profiles);
        return v;
    }

    public void onButtonPressed(String name, Object object) {
        if (mListener != null) {
            mListener.onFragmentInteraction(name, object);
        }
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btn_muapp_matching.setOnClickListener(this);
        btn_no_muapp_matching.setOnClickListener(this);
        if (User.Gender.getGender(user.getGender()) != User.Gender.Female)
            btn_crush_matching.setVisibility(View.GONE);
        else
            btn_crush_matching.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (!isHidden()) {
            replaceFragment(GetMatchingUsersFragment.newInstance(user));
            handler = new Handler();
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    if (((MainActivity) getContext()).getCurrentLocation() != null) {
                        getMatchingUsers();
                        preferenceHelper.putSearchPreferencesChangedDisabled();
                    } else
                        handler.postDelayed(this, waitTime);
                }
            };
            handler.postDelayed(runnable, waitTime);
        }
    }

    private void getMatchingUsers() {
        new APIService(getContext()).getMatchingUsers(matchingUsersPage, ((MainActivity) getContext()).getCurrentLocation(), this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onFragmentInteraction(String name, Object data) {

    }

    @Override
    public void onFailure(boolean isSuccessful, String responseString) {

    }

    @Override
    public void onSuccess(int responseCode, MatchingResult result) {
        if (result.getMatchingUsers().size() > 0) {
            matchingFragmentList.clear();
            for (final MatchingUser user : result.getMatchingUsers()) {
                if (!TextUtils.isEmpty(user.getDescription()))
                    uploadDescriptionToFirebase(user.getId(), user.getDescription());
                Log.wtf("Matching", user.toString());
                MatchingUserProfileFragment fragment = MatchingUserProfileFragment.newInstance(user);
                fragment.setOnMatchingInteractionListener(this);
                fragment.setOnProfileScrollListener(this);
                matchingFragmentList.add(fragment);
            }
            onAllUsersLoaded();
        }
    }

    @Override
    public void onLikeClicked(MatchingUser user) {

    }

    @Override
    public void onUnlikeClicked(MatchingUser user) {

    }

    @Override
    public void onCrushCliched(MatchingUser user) {

    }

    @Override
    public void onReportedUser() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.lbl_thank_you)
                .setCancelable(false)
                .setMessage(R.string.lbl_your_report_will_be_analized)
                .setPositiveButton(android.R.string.ok, null);
        builder.create().show();
        btn_no_muapp_matching.performClick();
    }

    private void replaceFragment(Fragment frag) {
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        if (currentFragment != null)
            ft.remove(currentFragment);
        currentFragment = frag;
        if (frag instanceof GetMatchingUsersFragment)
            showControls(false);
        else showControls(true);
        ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
        ft.replace(R.id.content_matching_profiles, frag);
        ft.commit();
    }

    private void showControls(final Boolean show) {
        Handler mainHandler = new Handler(getContext().getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                Utils.animViewScale(getContext(), container_actions_matching, show);
            }
        };
        mainHandler.post(myRunnable);

    }

    private void uploadDescriptionToFirebase(int matchingUserId, final String description) {
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("content").child(String.valueOf(matchingUserId));
        reference.orderByChild("catContent").equalTo("contentDesc").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    boolean descriptionFound = false;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        UserContent content = snapshot.getValue(UserContent.class);
                        if (!content.getComment().equals(description)) {
                            snapshot.getRef().removeValue();
                        } else {
                            descriptionFound = true;
                        }
                    }
                    if (!descriptionFound) {
                        UserContent content = new UserContent();
                        content.setCreatedAt(32535237599000L);
                        content.setCatContent("contentDesc");
                        content.setComment(description);
                        content.setLikes(0);
                        reference.child(reference.push().getKey()).setValue(content);
                    }
                } else {
                    UserContent content = new UserContent();
                    content.setCreatedAt(32535237599000L);
                    content.setCatContent("contentDesc");
                    content.setComment(description);
                    content.setLikes(0);
                    reference.child(reference.push().getKey()).setValue(content);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    @Override
    public void onAllUsersLoaded() {
        Boolean hasException = true;
        do {
            try {
                replaceFragment(matchingFragmentList.get(0));
                showControls(true);
                hasException = false;
            } catch (Exception x) {

            }
        } while (hasException);
    }

    @Override
    public void onClick(View v) {
        try {
            Bundle params = new Bundle();
            switch (v.getId()) {
                case R.id.btn_muapp_matching:
                    params.putString(Analytics.Muapp.MUAPP_PROPERTY.Type.toString(), Analytics.Muapp.MUAPP_TYPE.Button.toString());
                    params.putString(Analytics.Muapp.MUAPP_PROPERTY.Screen.toString(), Analytics.Muapp.MUAPP_SCREEN.Matching.toString());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Muapp.MUAPP_EVENT.Muapp.toString(), params);
                    new APIService(getContext()).likeUser(matchingFragmentList.get(0).getMatchingUser().getId(), null, this, null, null);
                    break;
                case R.id.btn_no_muapp_matching:
                    params.putString(Analytics.Muapp.MUAPP_PROPERTY.Type.toString(), Analytics.Muapp.MUAPP_TYPE.Button.toString());
                    params.putString(Analytics.Muapp.MUAPP_PROPERTY.Screen.toString(), Analytics.Muapp.MUAPP_SCREEN.Matching.toString());
                    FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Muapp.MUAPP_EVENT.Dismiss.toString(), params);
                    new APIService(getContext()).dislikeUser(matchingFragmentList.get(0).getMatchingUser().getId(), null);
                    break;
                case R.id.btn_crush_matching:
                    generateCrush();
                    break;
            }
        } catch (Exception x) {
            x.printStackTrace();
        }

        if (v.getId() != R.id.btn_crush_matching)
            try {
                matchingFragmentList.remove(0);
                if (matchingFragmentList.size() > 0) {
                    replaceFragment(matchingFragmentList.get(0));
                } else {
                    matchingUsersPage++;
                    showControls(false);
                    replaceFragment(GetMatchingUsersFragment.newInstance(user));
                    getMatchingUsers();
                }
            } catch (Exception x) {
                x.printStackTrace();
            }
    }

    private void generateCrush() {
        showControls(false);
        matchingUsersPage = 1;
        final int opponentId = matchingFragmentList.get(0).getMatchingUser().getId();
        DatabaseReference myConversations = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(user.getId()));
        final DatabaseReference yourConversations = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                .child("conversations")
                .child(String.valueOf(opponentId));
        final String opponentCrushId = yourConversations.push().getKey();
        final String myCrushId = myConversations.push().getKey();
        final Conversation crush = new Conversation();
        crush.setCreationDate(new Date().getTime());
        crush.setLikeByMe(false);
        crush.setLikeByOpponent(false);
        crush.setCrush(true);
        crush.setSeen(false);
        crush.setOpponentConversationId(opponentCrushId);
        crush.setOpponentId(opponentId);
        myConversations.child(myCrushId).setValue(crush).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                crush.setOpponentConversationId(myCrushId);
                crush.setOpponentId(user.getId());
                yourConversations.child(opponentCrushId).setValue(crush).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        new APIService(getContext()).crushUser(opponentId);
                        FirebaseDatabase.getInstance().getReference(DATABASE_REFERENCE).child("users").child(String.valueOf(opponentId)).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ConversationItem conversationItem = dataSnapshot.getValue(ConversationItem.class);
                                if (conversationItem != null) {
                                    Bundle params = new Bundle();
                                    params.putString(Analytics.Crush.CRUSH_PROPERTY.Screen.toString(), Analytics.Crush.CRUSH_SCREEN.Matching.toString());
                                    FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Crush.CRUSH_EVENT.Crush.toString(), params);
                                    new APIService(getContext()).sendPushNotification(conversationItem.getPushToken(), conversationItem.getKey(), null, "notif_crush", new String[]{new UserHelper(getContext()).getLoggedUser().getFirstName()});
                                    conversationItem.setKey(myCrushId);
                                    crush.setKey(myCrushId);
                                    crush.setOpponentConversationId(opponentCrushId);
                                    crush.setOpponentId(opponentId);
                                    conversationItem.setConversation(crush);
                                    Intent crushIntent = new Intent(getContext(), ChatActivity.class);
                                    crushIntent.putExtra(CONVERSATION_EXTRA, conversationItem);
                                    startActivity(crushIntent);
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                });
            }
        });
    }

    @Override
    public void onScrollToTop() {
        showControls(true);
    }

    @Override
    public void onScroll() {
        showControls(false);
    }

    @Override
    public void onSuccess(int responseCode, final LikeUserResult result) {
        Log.wtf("LikeResult", result.toString());
        if (result.getLikeUserMatch() != null) {
            Bundle matchBundle = new Bundle();
            matchBundle.putString(Analytics.Match.MATCH_PROPERTY.Type.toString(), Analytics.Match.MATCH_TYPE.New.toString());
            FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Match.MATCH_EVENT.Match.toString(), matchBundle);
            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE)
                    .child("conversations")
                    .child(String.valueOf(user.getId())).child(result.getDialogKey());
            Log.wtf("conversationReference", reference.toString());
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    final Conversation c = dataSnapshot.getValue(Conversation.class);
                    if (c != null) {
                        FirebaseDatabase.getInstance().getReference(DATABASE_REFERENCE).child("users").child(String.valueOf(result.getLikeUserMatch().getMatcherId())).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                ConversationItem conversationItem = dataSnapshot.getValue(ConversationItem.class);
                                if (conversationItem != null) {
                                    conversationItem.setKey(result.getDialogKey());
                                    conversationItem.setConversation(c);
                                    Intent matchIntent = new Intent(getContext(), MatchActivity.class);
                                    matchIntent.putExtra(MATCHING_USER, result.getLikeUserMatch().getLikeUserMatchUser());
                                    matchIntent.putExtra(MATCHING_CONVERSATION, conversationItem);
                                    matchIntent.putExtra(FROM_MATCH, true);
                                    startActivity(matchIntent);
                                    replaceFragment(GetMatchingUsersFragment.newInstance(user));
                                } else {
                                    Log.wtf("ConversationItem", "isNull");
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    } else {
                        Log.wtf("Conversation", "isNull");
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        } else {
            Log.wtf("LikeResult", "Theres not motherfucking match");
        }

    }
}
