package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quickblox.chat.model.QBChatDialog;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatDialogsListener;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper;
import me.muapp.android.Classes.Quickblox.cache.CacheUtils;
import me.muapp.android.Classes.Quickblox.cache.DialogCacheHelper;
import me.muapp.android.Classes.Quickblox.cache.DialogCacheObject;
import me.muapp.android.Classes.Quickblox.messages.QuickBloxMessagesHelper;
import me.muapp.android.Classes.Quickblox.messages.QuickBloxMessagesListener;
import me.muapp.android.Classes.Util.ProgressUtil;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.CrushesAdapter;
import me.muapp.android.UI.Adapter.MatchesAdapter;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;


public class ChatFragment extends Fragment implements OnFragmentInteractionListener, QuickBloxChatDialogsListener, QuickBloxMessagesListener {
    private static final String TAG = "ChatFragment";
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    ProgressUtil progressUtil;
    private User user;
    private OnFragmentInteractionListener mListener;
    MatchesAdapter matchesAdapter;
    CrushesAdapter crushesAdapter;
    View progress_chats, content_chats;
    private Realm realm;
    RecyclerView recycler_matches, recycler_crushes;

    public ChatFragment() {

    }

    public static ChatFragment newInstance(User user) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        if (getArguments() != null) {
            user = getArguments().getParcelable(ARG_CURRENT_USER);
        }
        matchesAdapter = new MatchesAdapter(getContext());
        crushesAdapter = new CrushesAdapter(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        progress_chats = v.findViewById(R.id.progress_chats);
        content_chats = v.findViewById(R.id.content_chats);
        recycler_matches = (RecyclerView) v.findViewById(R.id.recycler_matches);
        recycler_crushes = (RecyclerView) v.findViewById(R.id.recycler_crushes);
        LinearLayoutManager linearLayoutManagerHorizontal = new LinearLayoutManager(getContext());
        linearLayoutManagerHorizontal.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_crushes.setLayoutManager(linearLayoutManagerHorizontal);
        recycler_crushes.setAdapter(crushesAdapter);
        recycler_matches.setLayoutManager(new LinearLayoutManager(getContext()));
        recycler_matches.setAdapter(matchesAdapter);
        progressUtil = new ProgressUtil(getContext(), content_chats, progress_chats);
        progressUtil.showProgress(true);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        realm = CacheUtils.getInstance(user);
        if (QuickBloxChatHelper.getInstance().isSessionActive()) {
            QuickBloxChatHelper.getInstance().getDialogs(this);
            QuickBloxMessagesHelper.getInstance().registerQbChatListeners(realm, ChatFragment.this);
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
    public void onDialogsLoaded(List<QBChatDialog> dialogs, boolean success) {
        DialogCacheHelper.setDialogs(realm, dialogs, user.getId(), success, new DialogCacheHelper.CacheDialogsHandler() {
            @Override
            public void onItemAdded(final DialogCacheObject dialogCacheObject) {
                Log.wtf("onItemAdded", dialogCacheObject.getDialogId() + "");
                if (dialogCacheObject.getCrush())
                    recycler_crushes.post(new Runnable() {
                        public void run() {
                            crushesAdapter.addDialog(dialogCacheObject);
                        }
                    });
                else
                    recycler_matches.post(new Runnable() {
                        public void run() {
                            matchesAdapter.addDialog(dialogCacheObject);
                        }
                    });
            }

            @Override
            public void onAllDialogsAdded(int dialogsSize, List<DialogCacheObject> cachedDialogs) {
                Log.wtf("onAllDialogsAdded", dialogsSize + "");
            }
        });
        progressUtil.showProgress(false);
    }

    @Override
    public void onDialogUpdated(String chatDialog) {
        DialogCacheHelper.getDialogs(realm, "", new RealmChangeListener<RealmResults<DialogCacheObject>>() {
            @Override
            public void onChange(RealmResults<DialogCacheObject> element) {
                for (DialogCacheObject o : element) {
                    DialogCacheObject dco = realm.copyFromRealm(o);
                    if (dco.getCrush())
                        crushesAdapter.addDialog(dco);
                    else
                        matchesAdapter.addDialog(dco);
                }
            }
        });
    }

    @Override
    public void onNewDialog() {
        QuickBloxChatHelper.getInstance().getDialogs(this);
    }
}
