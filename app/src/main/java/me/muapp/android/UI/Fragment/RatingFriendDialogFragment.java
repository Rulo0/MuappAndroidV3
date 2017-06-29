package me.muapp.android.UI.Fragment;


import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.MatchingUser;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnUserRatedListener;

/**
 * Created by rulo on 9/05/17.
 */

public class RatingFriendDialogFragment extends DialogFragment {
    TextView txt_rating_title;
    Button btn_rating_dialog;
    RatingBar rating_user;
    ImageButton btn_dismiss_dialog;
    MatchingUser matchingUser;
    static final String ARG_MATCHING_USER = "ARG_PICTURE_URL";
    OnUserRatedListener onUserRatedListener;

    public RatingFriendDialogFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            matchingUser = getArguments().getParcelable(ARG_MATCHING_USER);
        }
    }

    public void setOnUserRatedListener(OnUserRatedListener onUserRatedListener) {
        this.onUserRatedListener = onUserRatedListener;
    }

    public static RatingFriendDialogFragment newInstance(MatchingUser user) {
        RatingFriendDialogFragment fragment = new RatingFriendDialogFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_MATCHING_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_rating_friend, container, false);
        txt_rating_title = (TextView) v.findViewById(R.id.txt_dialog_title);
        rating_user = (RatingBar) v.findViewById(R.id.txt_dialog_content);
        btn_dismiss_dialog = (ImageButton) v.findViewById(R.id.btn_dismiss_dialog);
        btn_rating_dialog = (Button) v.findViewById(R.id.btn_rating_dialog);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        txt_rating_title.setText(String.format(getContext().getString(R.string.format_is_your_fb_friend), matchingUser.getFullName()));
        rating_user.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (rating == 0)
                    ratingBar.setRating(1);
                if (!btn_rating_dialog.isEnabled())
                    btn_rating_dialog.setEnabled(true);
                btn_rating_dialog.getBackground().clearColorFilter();
            }
        });
        btn_rating_dialog.setEnabled(false);
        btn_rating_dialog.getBackground().setColorFilter(ContextCompat.getColor(getContext(), R.color.color_inactive_tab), PorterDuff.Mode.MULTIPLY);
        btn_dismiss_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissDialog();
            }
        });
        btn_rating_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onUserRatedListener != null)
                    onUserRatedListener.onRate((int) rating_user.getRating());
                new APIService(getContext()).setUserQualification(matchingUser.getId(), (int) rating_user.getRating(), null);
                Bundle rateParams = new Bundle();
                rateParams.putString(Analytics.RateFriend.RATE_PROPERTY.Screen.toString(), Analytics.RateFriend.RATE_SCREEN.Matching.toString());
                FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.RateFriend.RATE_EVENT.RateFriend.toString(), rateParams);
                dismissDialog();
            }
        });
    }

    private void dismissDialog() {
        getDialog().dismiss();
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        getDialog().setCancelable(false);
    }
}
