package me.muapp.android.UI.Adapter;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.daimajia.androidanimations.library.YoYo;
import com.eralp.circleprogressview.CircleProgressView;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.List;

import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.Candidate;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Activity.ViewProfileActivity;
import me.muapp.android.UI.Fragment.CandidatesFragment;
import me.muapp.android.UI.Fragment.Interface.OnCandidateInteractionListener;

import static com.daimajia.androidanimations.library.Techniques.Pulse;
import static com.daimajia.androidanimations.library.Techniques.Tada;
import static me.muapp.android.UI.Activity.ViewProfileActivity.CANDIDATE_PROGRESS;
import static me.muapp.android.UI.Activity.ViewProfileActivity.FROM_GATE;
import static me.muapp.android.UI.Activity.ViewProfileActivity.USER_ID;
import static me.muapp.android.UI.Activity.ViewProfileActivity.USER_NAME;
import static me.muapp.android.UI.Fragment.CandidatesFragment.CANDIDATE_PROFILE_CODE;


/**
 * Created by rulo on 28/03/17.
 */

public class CandidatesAdapter extends RecyclerView.Adapter<CandidatesAdapter.BaseCandidateHolder> {
    private final LayoutInflater mInflater;
    private List<Candidate> candidates;
    private Context mContext;
    public static final int TYPE_LOADING = -99;
    public static final int TYPE_TUTORIAL = -1;
    private static final int TYPE_CANDIDATE = 0;
    OnCandidateInteractionListener candidateInteractionListener;
    CandidatesFragment candidatesFragment;
    RecyclerView recyclerView;
    int animPos = 0;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        this.recyclerView = recyclerView;
    }

    public void setCandidatesFragment(CandidatesFragment candidatesFragment) {
        this.candidatesFragment = candidatesFragment;
    }

    public CandidatesAdapter(Context context) {
        this.mInflater = LayoutInflater.from(context);
        this.candidates = new ArrayList<>();
        this.mContext = context;
    }

    public void removeCandidate(int candidateId) {
        for (int i = 0; i < candidates.size(); i++) {
            if (candidates.get(i).getId() == candidateId) {
                candidates.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    public void setCandidateInteractionListener(OnCandidateInteractionListener candidateInteractionListener) {
        this.candidateInteractionListener = candidateInteractionListener;
    }

    public void addCandidate(Candidate candidate) {
        candidates.add(candidate);
        notifyItemInserted(candidates.size());
    }

    @Override
    public BaseCandidateHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case TYPE_LOADING:
                View loadingView = mInflater.inflate(R.layout.candidate_loading_layout, parent, false);
                return new LoadingViewHolder(loadingView);
            case TYPE_TUTORIAL:
                View tutorialView = mInflater.inflate(R.layout.candidate_tutorial_layout, parent, false);
                return new TutorialViewHolder(tutorialView);
            default:
                View itemView = mInflater.inflate(R.layout.candidate_item_layout, parent, false);
                return new CandidateViewHolder(itemView);

        }
    }

    @Override
    public void onBindViewHolder(BaseCandidateHolder holder, int position) {
        holder.bind(candidates.get(position));
    }

    @Override
    public int getItemCount() {
        return candidates.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (candidates.get(position).getId() == TYPE_TUTORIAL)
            return TYPE_TUTORIAL;
        else if (candidates.get(position).getId() == TYPE_LOADING)
            return TYPE_LOADING;
        return TYPE_CANDIDATE;
    }

    public class BaseCandidateHolder extends RecyclerView.ViewHolder {
        Candidate currentCandidate;

        public BaseCandidateHolder(View itemView) {
            super(itemView);
        }

        public void bind(final Candidate candidate) {
            this.currentCandidate = candidate;
        }
    }

    public class TutorialViewHolder extends BaseCandidateHolder implements View.OnClickListener {
        ImageButton btn_demo_clear;

        public TutorialViewHolder(View itemView) {
            super(itemView);
            btn_demo_clear = (ImageButton) itemView.findViewById(R.id.btn_demo_clear);
        }

        @Override
        public void bind(final Candidate candidate) {
            super.bind(candidate);
            btn_demo_clear.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            new PreferenceHelper(mContext).putCandidatesTutorialDisabled();
            candidates.remove(getAdapterPosition());
            notifyItemRemoved(getAdapterPosition());
        }
    }

    public class LoadingViewHolder extends BaseCandidateHolder {
        public LoadingViewHolder(View itemView) {
            super(itemView);

        }

        @Override
        public void bind(final Candidate candidate) {
            super.bind(candidate);

        }
    }

    public class CandidateViewHolder extends BaseCandidateHolder implements View.OnClickListener {
        ImageView img_photo_candidate;
        TextView txt_candidate_name;
        TextView txt_candidate_age;
        TextView txt_candidate_distance;
        TextView txt_candidate_friends;
        TextView txt_candidate_progress;
        View itemView;
        CircleProgressView candidate_progress;
        ImageButton btn_candidate_unlike;
        ImageButton btn_candidate_like;
        ImageButton btn_candidate_clear;
        boolean userInteraction;

        public CandidateViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.img_photo_candidate = (ImageView) itemView.findViewById(R.id.img_photo_candidate);
            this.txt_candidate_name = (TextView) itemView.findViewById(R.id.txt_candidate_name);
            this.txt_candidate_age = (TextView) itemView.findViewById(R.id.txt_candidate_age);
            this.txt_candidate_distance = (TextView) itemView.findViewById(R.id.txt_candidate_distance);
            this.txt_candidate_friends = (TextView) itemView.findViewById(R.id.txt_candidate_friends);
            this.txt_candidate_progress = (TextView) itemView.findViewById(R.id.txt_candidate_progress);
            this.candidate_progress = (CircleProgressView) itemView.findViewById(R.id.candidate_progress);
            this.btn_candidate_clear = (ImageButton) itemView.findViewById(R.id.btn_candidate_clear);
            this.btn_candidate_unlike = (ImageButton) itemView.findViewById(R.id.btn_candidate_unlike);
            this.btn_candidate_like = (ImageButton) itemView.findViewById(R.id.btn_candidate_like);
        }

        @Override
        public void bind(final Candidate candidate) {
            super.bind(candidate);
            try {
                userInteraction = false;
                btn_candidate_unlike.setEnabled(true);
                btn_candidate_unlike.setEnabled(true);
                candidate_progress.setProgress(candidate.getPercentage().floatValue());
                txt_candidate_progress.setText(candidate.getPercentage() + "%");
                getLocationString(candidate.getLatitude(), candidate.getLongitude());
                txt_candidate_friends.setText(String.valueOf(candidate.getCommonFriendships()));
                txt_candidate_name.setText(candidate.getFirstName());
                txt_candidate_age.setText(String.format(mContext.getString(R.string.format_user_years), candidate.getAge()));
                Glide.with(mContext).load(candidate.getPhoto()).asBitmap().centerCrop().placeholder(R.drawable.ic_placeholder).priority(Priority.IMMEDIATE).error(R.drawable.ic_placeholder_error).diskCacheStrategy(DiskCacheStrategy.RESULT).into(img_photo_candidate);
                btn_candidate_clear.setOnClickListener(this);
                btn_candidate_like.setOnClickListener(this);
                btn_candidate_unlike.setOnClickListener(this);
                img_photo_candidate.setOnClickListener(this);
            } catch (Exception x) {
                x.printStackTrace();
            }
        }

        private void getLocationString(String latitude, String longitude) {
            Location locationMatching = new Location("MATCHING");
            try {
                locationMatching.setLatitude(Double.parseDouble(latitude));
            } catch (Exception x) {
                locationMatching.setLatitude(0);
            }
            try {
                locationMatching.setLongitude(Double.parseDouble(longitude));
            } catch (Exception x) {
                locationMatching.setLongitude(0);
            }
            Float distance = locationMatching.distanceTo(new PreferenceHelper(mContext).getLocation());
            if (distance > 1000) {
                txt_candidate_distance.setText(String.format("%s km", Math.round(distance / 1000)));
            } else {
                txt_candidate_distance.setText(String.format("%s m", Math.round(distance)));
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btn_candidate_clear:
                case R.id.btn_candidate_unlike:
                    if (candidateInteractionListener != null && !userInteraction)
                        candidateInteractionListener.onUnlike(currentCandidate);
                    break;
                case R.id.btn_candidate_like:
                    if (candidateInteractionListener != null && !userInteraction)
                        candidateInteractionListener.onLike(currentCandidate);
                    break;
                case R.id.img_photo_candidate:
                    if (!userInteraction) {
                        FirebaseAnalytics.getInstance(mContext).logEvent(Analytics.Gate_Woman.GATE_WOMAN_EVENT.Gate_Woman_Enlarge_Candidate.toString(), null);
                        try {
                            Intent profileIntent = new Intent(mContext, ViewProfileActivity.class);
                            profileIntent.putExtra(USER_ID, currentCandidate.getId());
                            profileIntent.putExtra(USER_NAME, currentCandidate.getFullName());
                            profileIntent.putExtra(FROM_GATE, true);
                            profileIntent.putExtra(CANDIDATE_PROGRESS, currentCandidate.getPercentage());
                            candidatesFragment.startActivityForResult(profileIntent, CANDIDATE_PROFILE_CODE);
                        } catch (Exception x) {
                            x.printStackTrace();
                        }
                    }
                    break;
            }

            if (v.getId() != R.id.img_photo_candidate) {
                YoYo.with(v.getId() == R.id.btn_candidate_like ? Pulse : Tada)
                        .duration(700)
                        .onStart(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                userInteraction = true;
                            }
                        })
                        .onEnd(new YoYo.AnimatorCallback() {
                            @Override
                            public void call(Animator animator) {
                                candidates.remove(getAdapterPosition());
                                notifyItemRemoved(getAdapterPosition());
                            }
                        })
                        .playOn(itemView);
            }
        }
    }
}
