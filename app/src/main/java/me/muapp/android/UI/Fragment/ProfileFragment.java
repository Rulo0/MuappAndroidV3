package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.getkeepsafe.taptargetview.TapTargetView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.UserQualificationsHandler;
import me.muapp.android.Classes.Chat.ConversationItem;
import me.muapp.android.Classes.Internal.MuappQualifications.UserQualifications;
import me.muapp.android.Classes.Internal.MuappQuote;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.Tutorials;
import me.muapp.android.Classes.Util.UserHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.MainActivity;
import me.muapp.android.UI.Activity.ManGateActivity;
import me.muapp.android.UI.Adapter.UserContentAdapter;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;

import static me.muapp.android.Application.MuappApplication.DATABASE_REFERENCE;

public class ProfileFragment extends Fragment implements OnFragmentInteractionListener, ChildEventListener, ValueEventListener {
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    User user;
    private OnFragmentInteractionListener mListener;
    DatabaseReference myUserContentReference;
    DatabaseReference myUserInfoReference;
    UserContentAdapter adapter;
    RecyclerView recycler_my_content;
    FloatingActionButton fab_add_content;
    Toolbar toolbar_current_user_profile;
    TextView toolbar_current_user_name;
    AppBarLayout app_bar_user_profile;
    int counter = 1;
    private boolean isToolbarPrepared = false;
    private boolean isTutorialShowing = false;
    PreferenceHelper preferenceHelper;

    public ProfileFragment() {
    }

    public static ProfileFragment newInstance(User user) {
        ProfileFragment fragment = new ProfileFragment();
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
        adapter = new UserContentAdapter(getContext(), new UserHelper(getContext()).getLoggedUser());
        adapter.setFragmentManager(getChildFragmentManager());
        myUserContentReference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("content").child(String.valueOf(user.getId()));
        myUserInfoReference = FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("users").child(String.valueOf(user.getId()));
        FirebaseDatabase.getInstance().getReference().child(DATABASE_REFERENCE).child("quotes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<MuappQuote> quoteList = new ArrayList<>();
                try {
                    for (DataSnapshot s : dataSnapshot.getChildren()) {
                        MuappQuote q = s.getValue(MuappQuote.class);
                        if (q != null) {
                            q.setKey(s.getKey());
                            quoteList.add(q);
                        }
                    }
                    adapter.setQuoteList(quoteList);
                } catch (Exception x) {
                    x.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        new APIService(getContext()).getUserQualifications(user.getId(), new UserQualificationsHandler() {
            @Override
            public void onSuccess(int responseCode, UserQualifications qualifications) {
                adapter.setQualifications(qualifications.getQualifications());
            }

            @Override
            public void onFailure(boolean isSuccessful, String responseString) {

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        toolbar_current_user_profile = (Toolbar) v.findViewById(R.id.toolbar_current_user_profile);
        toolbar_current_user_name = (TextView) v.findViewById(R.id.toolbar_current_user_name);
        app_bar_user_profile = (AppBarLayout) v.findViewById(R.id.app_bar_user_profile);
        recycler_my_content = (RecyclerView) v.findViewById(R.id.recycler_my_content);
        if (getContext() instanceof MainActivity) {
            this.fab_add_content = ((MainActivity) getContext()).getFab_add_content();
        } else if (getContext() instanceof ManGateActivity) {
            this.fab_add_content = ((ManGateActivity) getContext()).getFab_add_content();
        }
        LinearLayoutManager llm = new LinearLayoutManager(getContext());
        //llm.setStackFromEnd(true);
        recycler_my_content.setLayoutManager(llm);
        recycler_my_content.setHasFixedSize(true);
        recycler_my_content.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (fab_add_content != null) {
                    if (dy > 0) {
                        if (fab_add_content.isShown())
                            fab_add_content.hide();
                    } else {
                        if (!fab_add_content.isShown())
                            fab_add_content.show();
                    }
                }
            }
        });
        return v;
    }


    private void prepareToolbar() {
        if (!isToolbarPrepared) {
            toolbar_current_user_profile.getMenu().clear();
            toolbar_current_user_profile.inflateMenu(R.menu.profile_menu);
            isToolbarPrepared = true;
        }
        if (getContext() instanceof ManGateActivity)
            ((ManGateActivity) getContext()).setSupportActionBar(toolbar_current_user_profile);

        if (user.getFakeAccount() && preferenceHelper.getTutorialProfileCounter() == 2) {
            preferenceHelper.addCounterToProfile();
        }

        if (preferenceHelper.getTutorialProfileCounter() == 3 && !preferenceHelper.getTutorialAddContent()) {
            preferenceHelper.addCounterToProfile();
        }
        String title = "";
        String content = "";
        TapTargetView.Listener listener = new TapTargetView.Listener() {
            @Override
            public void onTargetDismissed(TapTargetView view, boolean userInitiated) {
                super.onTargetDismissed(view, userInitiated);
                preferenceHelper.addCounterToProfile();
                ((MainActivity) getContext()).setSupportActionBar(toolbar_current_user_profile);
                isToolbarPrepared = false;
                isTutorialShowing = false;
                counter++;
            }
        };
        if (getContext() instanceof MainActivity) {
            switch (preferenceHelper.getTutorialProfileCounter()) {
                case 1:
                    if (!isTutorialShowing && !isHidden()) {
                        title = getString(R.string.lbl_tutorial_personalize);
                        content = getString(R.string.lbl_tutorial_personalize_content);
                        new Tutorials((MainActivity) getContext()).showTutorialForMenuItem(toolbar_current_user_profile, R.id.action_edit_profile, title, content, 25, listener);
                        isTutorialShowing = true;
                    }
                    break;
                case 2:
                    if (!isTutorialShowing && !isHidden()) {
                        title = getString(R.string.lbl_tutorial_verify);
                        content = getString(R.string.lbl_tutorial_verify_content);
                        new Tutorials((MainActivity) getContext()).showTutorialForMenuItem(toolbar_current_user_profile, R.id.action_settings_profile, title, content, 25, listener);
                        isTutorialShowing = true;
                    }
                    break;
                case 3:
                    if (!isTutorialShowing && !isHidden()) {
                        title = getString(R.string.lbl_tutorial_history);
                        content = getString(R.string.lbl_tutorial_history_content);
                        if (!fab_add_content.isShown())
                            fab_add_content.show();
                        new Tutorials((MainActivity) getContext()).showTutorialForView(fab_add_content, true, title, content, 50, listener);
                        isTutorialShowing = true;
                    }
                    break;
                default:
                    ((MainActivity) getContext()).setSupportActionBar(toolbar_current_user_profile);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        recycler_my_content.setAdapter(adapter);
        if (getContext() instanceof MainActivity) {
            if (new UserHelper(getContext()).getLoggedUser().getFakeAccount() != null && new UserHelper(getContext()).getLoggedUser().getFakeAccount())
                toolbar_current_user_name.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_verified_profile, 0, 0, 0);
        }
        myUserContentReference.addChildEventListener(this);
        myUserInfoReference.addValueEventListener(this);
        if (!TextUtils.isEmpty(user.getDescription())) {
            UserContent content = new UserContent();
            content.setCreatedAt(32535237599000L);
            content.setCatContent("contentDesc");
            content.setKey("contentDesc");
            content.setComment(user.getDescription());
            content.setLikes(0);
            adapter.removeAllDescriptions();
            adapter.addContent(content);
        } else {
            adapter.removeAllDescriptions();
        }
    }


    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        ConversationItem conversationItem = dataSnapshot.getValue(ConversationItem.class);
        if (conversationItem != null) {
            Log.wtf("SettingName", conversationItem.getFullName());
            toolbar_current_user_name.setText(conversationItem.getFullName());
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        prepareToolbar();

        String previousDescription = adapter.getCurrentDescription();

        User loggedUser = new UserHelper(getContext()).getLoggedUser();
        adapter.setUser(loggedUser);
        Log.wtf("LeDescription", previousDescription + " - " + loggedUser.getDescription());
        if (!previousDescription.equals(loggedUser.getDescription())) {
            adapter.removeAllDescriptions();
            UserContent content = new UserContent();
            content.setCreatedAt(32535237599000L);
            content.setCatContent("contentDesc");
            content.setKey("contentDesc");
            content.setComment(loggedUser.getDescription());
            content.setLikes(0);
            adapter.removeAllDescriptions();
            if (!TextUtils.isEmpty(loggedUser.getDescription()))
                adapter.addContent(content);
        }
    }


    @Override
    public void onStop() {
        super.onStop();
        adapter.stopMediaPlayer();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        adapter.releaseMediaPlayer();
        myUserContentReference.removeEventListener((ChildEventListener) this);
        myUserInfoReference.removeEventListener((ValueEventListener) this);
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
    public void onFragmentInteraction(String name, Object data) {

    }

    @Override
    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
        Log.wtf("onChildAdded", dataSnapshot.getKey());
        if (preferenceHelper.getTutorialAddContent()) {
            preferenceHelper.putTutorialAddContentDisabled();
        }
        UserContent c = dataSnapshot.getValue(UserContent.class);
        if (c != null) {
            c.setKey(dataSnapshot.getKey());
            adapter.addContent(c);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
        UserContent c = dataSnapshot.getValue(UserContent.class);
        if (c != null) {
            c.setKey(dataSnapshot.getKey());
            adapter.removeContent(dataSnapshot.getKey());
            adapter.addContent(c);
        }
    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {
        UserContent c = dataSnapshot.getValue(UserContent.class);
        if (c != null) {
            adapter.removeContent(dataSnapshot.getKey());
        }
    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
        UserContent c = dataSnapshot.getValue(UserContent.class);
        if (c != null) {
            c.setKey(dataSnapshot.getKey());
            adapter.addContent(c);
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden)
            adapter.stopMediaPlayer();
        else
            adapter.setUser(new UserHelper(getContext()).getLoggedUser());
    }
}
