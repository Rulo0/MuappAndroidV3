package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rd.PageIndicatorView;

import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.ProfilePicturesAdapter;
import me.muapp.android.UI.Adapter.UserContentAdapter;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;


public class ProfileFragment extends Fragment implements OnFragmentInteractionListener, ChildEventListener {
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    User user;
    private OnFragmentInteractionListener mListener;
    DatabaseReference myUserReference;
    ViewPager pager_profile_pictures;
    ProfilePicturesAdapter profilePicturesAdapter;
    PageIndicatorView indicator_profile_pictures;
    TextView txt_profile_info;
    UserContentAdapter adapter;
    RecyclerView recycler_my_content;

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
       /* List<String> testing = new ArrayList<>();
        for (int i = 0; i < 6; i++) {
            testing.add(user.getAlbum().get(0));
        }
        profilePicturesAdapter = new ProfilePicturesAdapter(getContext(), testing);*/
        adapter = new UserContentAdapter(getContext());
        myUserReference = FirebaseDatabase.getInstance().getReference().child("content").child(String.valueOf(user.getId()));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        recycler_my_content = (RecyclerView) v.findViewById(R.id.recycler_my_content);
        recycler_my_content.setLayoutManager(new LinearLayoutManager(getContext()));
       /* pager_profile_pictures = (ViewPager) v.findViewById(R.id.pager_profile_pictures);
        indicator_profile_pictures = (PageIndicatorView) v.findViewById(R.id.indicator_profile_pictures);
        indicator_profile_pictures.setAnimationType(AnimationType.SWAP);
        indicator_profile_pictures.setRadius(5);
        txt_profile_info = (TextView) v.findViewById(R.id.txt_profile_info);*/
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
     /*   pager_profile_pictures.setAdapter(profilePicturesAdapter);
        indicator_profile_pictures.setViewPager(pager_profile_pictures);
        createHeader(user);*/
        recycler_my_content.setAdapter(adapter);
    }

    /*private void createHeader(User user) {
        String steps = "Hello Everyone";
        String userAge = String.format(getContext().getString(R.string.format_user_years), user.getAge());
        SpannableString ssAge = new SpannableString(userAge);
        ssAge.setSpan(new StyleSpan(Typeface.BOLD), 0, ssAge.length(), 0);
        txt_profile_info.append(ssAge);
        if (!TextUtils.isEmpty(user.getHometown())) {
            txt_profile_info.append(getContext().getString(R.string.format_user_hometown));
            String userHomeTown = user.getHometown();
            SpannableString ssHomeTown = new SpannableString(userHomeTown);
            ssHomeTown.setSpan(new StyleSpan(Typeface.BOLD), 0, ssHomeTown.length(), 0);
            txt_profile_info.append(ssHomeTown);
        }
        txt_profile_info.append("\n");
        txt_profile_info.append(steps);
    }*/

    @Override
    public void onStart() {
        super.onStart();
        myUserReference.addChildEventListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        myUserReference.removeEventListener(this);
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
        UserContent c = dataSnapshot.getValue(UserContent.class);
        if (c != null) {
            c.setKey(dataSnapshot.getKey());
            Log.wtf("Content", c.toString());

            //  adapter.addContent(c);
        }
    }

    @Override
    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onChildRemoved(DataSnapshot dataSnapshot) {

    }

    @Override
    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {

    }
}
