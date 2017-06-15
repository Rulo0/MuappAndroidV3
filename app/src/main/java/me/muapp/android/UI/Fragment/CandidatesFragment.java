package me.muapp.android.UI.Fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.google.firebase.analytics.FirebaseAnalytics;

import org.apache.commons.lang3.ArrayUtils;

import java.util.Arrays;

import me.muapp.android.Classes.API.APIService;
import me.muapp.android.Classes.API.Handlers.CandidatesHandler;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.Candidate;
import me.muapp.android.Classes.Internal.CandidatesResult;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.Classes.Util.ProgressUtil;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.CandidatesAdapter;
import me.muapp.android.UI.Fragment.Interface.OnCandidateInteractionListener;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;

import static android.app.Activity.RESULT_OK;
import static me.muapp.android.UI.Adapter.CandidatesAdapter.TYPE_LOADING;
import static me.muapp.android.UI.Adapter.CandidatesAdapter.TYPE_TUTORIAL;


public class CandidatesFragment extends Fragment implements OnFragmentInteractionListener, CandidatesHandler, OnCandidateInteractionListener {
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    CandidatesAdapter candidatesAdapter;
    private User user;
    RecyclerView recycler_candidates;
    private OnFragmentInteractionListener mListener;
    StaggeredGridLayoutManager slm;
    Boolean loadingCandidates = false;
    int candidatesPage = 1;
    ProgressUtil progressUtil;
    LinearLayout container_candidates_layout;
    public static final int CANDIDATE_PROFILE_CODE = 951;
    public static final String CANDIDATE_PROFILE_VIEW_RESULT = "CANDIDATE_PROFILE_VIEW_RESULT";
    public static final String CANDIDATE_KEY_RESULT = "CANDIDATE_KEY_RESULT";

    public CandidatesFragment() {
        // Required empty public constructor
    }

    public static CandidatesFragment newInstance(User user) {
        CandidatesFragment fragment = new CandidatesFragment();
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
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        candidatesAdapter = new CandidatesAdapter(getContext());
        candidatesAdapter.setCandidateInteractionListener(this);
        candidatesAdapter.setCandidatesFragment(this);
        slm = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recycler_candidates.setLayoutManager(slm);
        recycler_candidates.setAdapter(candidatesAdapter);
        recycler_candidates.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] lastVisibleItem = slm.findLastVisibleItemPositions(null);
                int searchingFor = candidatesAdapter.getItemCount() - 1;
                if (Arrays.asList(ArrayUtils.toObject(lastVisibleItem)).contains(searchingFor))
                    getCandidates();
            }
        });
        getCandidates();
        progressUtil = new ProgressUtil(getContext(), recycler_candidates, container_candidates_layout);
    }

    private void getCandidates() {
        if (!loadingCandidates) {
            FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Gate_Woman.GATE_WOMAN_EVENT.Gate_Woman_Loading.toString(), null);
            new APIService(getContext()).getCandidates(candidatesPage, this);
            loadingCandidates = true;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_gate, container, false);
        recycler_candidates = (RecyclerView) v.findViewById(R.id.recycler_candidates);
        container_candidates_layout = (LinearLayout) v.findViewById(R.id.container_candidates_layout);
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
    public void onSuccess(final int responseCode, final CandidatesResult result) {

        if (result.getCurrentPage() == 1 && new PreferenceHelper(getContext()).getCandidatesTutorial()) {
            if (new PreferenceHelper(getContext()).getCandidatesTutorial()) {
                Candidate tutorialCandidate = new Candidate();
                tutorialCandidate.setId(TYPE_TUTORIAL);
                result.getCandidates().add(1, tutorialCandidate);
            }
        }

        ((Activity) getContext()).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                candidatesAdapter.removeCandidate(TYPE_LOADING);
                progressUtil.showProgress(false);
                for (Candidate c : result.getCandidates()) {
                    Log.wtf("Candidate", c.toString());
                    candidatesAdapter.addCandidate(c);
                }
                Candidate loadingCandidate = new Candidate();
                loadingCandidate.setId(TYPE_LOADING);
                candidatesAdapter.addCandidate(loadingCandidate);
                loadingCandidates = false;
                candidatesPage++;
            }
        });
      /*  for (Candidate c : result.getCandidates()) {
            Log.wtf("Candidate", c.toString());
            candidatesAdapter.addCandidate(c);
        }*/
    }

    @Override
    public void onLike(Candidate candidate) {
        Log.wtf("Candidate", "Like " + candidate);
        Bundle params = new Bundle();
        params.putString(Analytics.Muapp.MUAPP_PROPERTY.Type.toString(), Analytics.Muapp.MUAPP_TYPE.Button.toString());
        params.putString(Analytics.Muapp.MUAPP_PROPERTY.Screen.toString(), Analytics.Muapp.MUAPP_SCREEN.Gate_Woman.toString());
        FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Muapp.MUAPP_EVENT.Muapp.toString(), params);
        new APIService(getContext()).likeCandidate(candidate.getId());
    }

    @Override
    public void onUnlike(Candidate candidate) {
        Log.wtf("Candidate", "Unlike " + candidate);
        Bundle params = new Bundle();
        params.putString(Analytics.Muapp.MUAPP_PROPERTY.Type.toString(), Analytics.Muapp.MUAPP_TYPE.Button.toString());
        params.putString(Analytics.Muapp.MUAPP_PROPERTY.Screen.toString(), Analytics.Muapp.MUAPP_SCREEN.Gate_Woman.toString());
        FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Muapp.MUAPP_EVENT.Dismiss.toString(), params);
        new APIService(getContext()).dislikeUser(candidate.getId(), null);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.wtf("CandidateResult", requestCode + " " + resultCode);
        if (requestCode == CANDIDATE_PROFILE_CODE && resultCode == RESULT_OK) {
            int returnedCandidateId = data.getIntExtra(CANDIDATE_KEY_RESULT, -1);
            candidatesAdapter.removeCandidate(returnedCandidateId);
            Bundle muappBundleProfile = new Bundle();
            muappBundleProfile.putString(Analytics.Muapp.MUAPP_PROPERTY.Type.toString(), Analytics.Muapp.MUAPP_TYPE.Button.toString());
            muappBundleProfile.putString(Analytics.Muapp.MUAPP_PROPERTY.Screen.toString(), Analytics.Muapp.MUAPP_SCREEN.User_Profile_New.toString());
            if (data.getBooleanExtra(CANDIDATE_PROFILE_VIEW_RESULT, false)) {
                FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Muapp.MUAPP_EVENT.Muapp.toString(), muappBundleProfile);
                new APIService(getContext()).likeCandidate(returnedCandidateId);
            } else {
                FirebaseAnalytics.getInstance(getContext()).logEvent(Analytics.Muapp.MUAPP_EVENT.Dismiss.toString(), muappBundleProfile);
                new APIService(getContext()).dislikeUser(returnedCandidateId, null);
            }
        }
    }
}
