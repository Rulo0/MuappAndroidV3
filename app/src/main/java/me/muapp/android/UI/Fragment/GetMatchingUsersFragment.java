package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.Timer;
import java.util.TimerTask;

import jp.wasabeef.glide.transformations.CropCircleTransformation;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.Utils;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;


public class GetMatchingUsersFragment extends Fragment implements OnFragmentInteractionListener {
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    ImageView img_matching_load_1, img_matching_load_2, img_matching_load_3, img_loading_face;
    private User user;
    private OnFragmentInteractionListener mListener;
    private TimerTask timerTask;
    final Handler handler = new Handler();
    private Timer timer;
    int animState = 3;
    boolean minus = true;

    public GetMatchingUsersFragment() {

    }

    public static GetMatchingUsersFragment newInstance(User user) {
        GetMatchingUsersFragment fragment = new GetMatchingUsersFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_get_matching_users, container, false);
        img_matching_load_1 = (ImageView) v.findViewById(R.id.img_matching_load_1);
        img_matching_load_2 = (ImageView) v.findViewById(R.id.img_matching_load_2);
        img_matching_load_3 = (ImageView) v.findViewById(R.id.img_matching_load_3);
        img_loading_face = (ImageView) v.findViewById(R.id.img_loading_face);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Glide.with(this).load(user.getPhoto()).placeholder(R.drawable.ic_placeholder).bitmapTransform(new CropCircleTransformation(getContext())).skipMemoryCache(true).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.ALL).into(img_loading_face);
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
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        if (minus)
                            animState--;
                        else
                            animState++;
                        if ((animState == 1 && minus) || (animState == 3 && !minus))
                            minus = !minus;

                        switch (animState) {
                            case 1:
                                Utils.animViewFade(img_matching_load_1, true, 1);
                                Utils.animViewFade(img_matching_load_2, false, .6f);
                                Utils.animViewFade(img_matching_load_3, false, .4f);
                                break;
                            case 2:
                                Utils.animViewFade(img_matching_load_1, true, 1);
                                Utils.animViewFade(img_matching_load_2, true, .6f);
                                Utils.animViewFade(img_matching_load_3, false, .4f);
                                break;
                            case 3:
                                Utils.animViewFade(img_matching_load_1, true, 1);
                                Utils.animViewFade(img_matching_load_2, true, .6f);
                                Utils.animViewFade(img_matching_load_3, true, .4f);
                                break;
                        }
                    }
                });
            }
        };
        timer.schedule(timerTask, 700, 700);
    }

    @Override
    public void onStop() {
        super.onStop();
        timer.cancel();
    }

    @Override
    public void onFragmentInteraction(String name, Object data) {

    }
}
