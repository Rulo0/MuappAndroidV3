package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatDialogUpdateListener;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatDialogsListener;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatLoginListener;
import me.muapp.android.Classes.Quickblox.cache.CacheUtils;
import me.muapp.android.Classes.Quickblox.cache.DialogCacheHelper;
import me.muapp.android.Classes.Quickblox.cache.DialogCacheObject;
import me.muapp.android.Classes.Quickblox.cache.MessageCacheHelper;
import me.muapp.android.Classes.Quickblox.messages.QuickBloxMessagesHelper;
import me.muapp.android.Classes.Quickblox.messages.QuickBloxMessagesListener;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;


public class ChatsFragment extends Fragment implements OnFragmentInteractionListener, QuickBloxMessagesListener,
        QuickBloxChatLoginListener {
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    User user;
    OnFragmentInteractionListener mListener;
    Realm realm;
    RealmResults<DialogCacheObject> dialogsQuery;
    boolean isLoadingMatches;
    PreferenceHelper preferenceHelper;
    DeleteMessagesTask deleteMessagesTask;

    public ChatsFragment() {
        // Required empty public constructor
    }

    public static ChatsFragment newInstance(User user) {
        ChatsFragment fragment = new ChatsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_CURRENT_USER, user);
        Log.wtf("ChatFragment", user.toString());
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preferenceHelper = new PreferenceHelper(getContext());
        realm = CacheUtils.getInstance(user);
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

    protected void addToContactlist(List<QBChatDialog> dialogs) {
        QuickBloxChatHelper.getInstance().addToContactList(dialogs);
    }

    @Override
    public void onDialogUpdated(String chatDialog) {
        loadCachedMatches();
    }

    @Override
    public void onNewDialog() {
        reload();
    }

    @Override
    public void onChatSessionCreated(boolean success) {
        if (success) {
            QuickBloxMessagesHelper.getInstance().registerQbChatListeners(realm, ChatsFragment.this);
            reload();
        }
    }

    public void reload() {
        if (isAdded()) {
            if (QuickBloxChatHelper.getInstance().isSessionActive()) {
                isLoadingMatches = true;
                loadDialogsFromQb();
            }
        }
    }

    protected void loadDialogsFromQb() {
        if (isAdded()) {
            QuickBloxChatHelper.getInstance().getDialogs(new QuickBloxChatDialogsListener() {
                @Override
                public void onDialogsLoaded(List<QBChatDialog> dialogs, boolean success) {
                    isLoadingMatches = false;
                    if (preferenceHelper.getFirstTimeChat())
                        for (QBChatDialog d : dialogs) {
                            Log.wtf("Chat", d.getDialogId());
                        }

                    if (isAdded()) {
                        if (success) {
                            DialogCacheHelper.setDialogs(realm, dialogs, user.getId(), true);
                        } else {
                            DialogCacheHelper.setDialogs(realm, dialogs, user.getId(), false);
                        }
                        updateQbList(dialogs);
                        addToContactlist(dialogs);
                    }
                }
            });
        }
    }//loadDialogsFromQb

    protected void updateQbList(List<QBChatDialog> dialogs) {
        if (isAdded()) {
            ArrayList<DialogCacheObject> cache = new ArrayList<>();
            for (QBChatDialog qb : dialogs) {
                cache.add(DialogCacheHelper.dialogToCache(qb, user.getId()));
            }
            Collections.sort(cache, new DialogCacheHelper.LastMessageDateSentComparator());
            List<DialogCacheObject> matches = new ArrayList<>();
            List<DialogCacheObject> crushes = new ArrayList<>();
            for (DialogCacheObject d : cache) {
                if (d.getCrush() != null && d.getCrush())
                    crushes.add(d);
                else
                    matches.add(d);
            }
            Log.wtf("Entering", "UpdateQBList");
            //  matchesAdapter.setMatches(matches);
            // crushesAdapter.setCrushes(crushes);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCachedMatches();
        isLoadingMatches = true;
        QuickBloxChatHelper.getInstance().addLoginListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (dialogsQuery != null) {
            dialogsQuery.removeAllChangeListeners();
            dialogsQuery = null;
        }
        QuickBloxChatHelper.getInstance().removeLoginListener(this);
        QuickBloxMessagesHelper.getInstance().unregisterQbChatListeners(this);
    }

    @Override
    public void onDestroyView() {
        if (deleteMessagesTask != null) {
            deleteMessagesTask.cancel(true);
            deleteMessagesTask = null;
        }
        realm.close();
        super.onDestroyView();
    }

    private void loadCachedMatches() {
        if (dialogsQuery != null) {
            dialogsQuery.removeAllChangeListeners();
            dialogsQuery = null;
        }
        String query = "";//searchViewText.getText().toString();
        dialogsQuery = DialogCacheHelper.getDialogs(realm, query, new RealmChangeListener<RealmResults<DialogCacheObject>>() {
            @Override
            public void onChange(RealmResults<DialogCacheObject> element) {
                if (isAdded()) {
                    updateList(new ArrayList<>(element));
                }
            }
        });
    }

    protected void updateList(List<DialogCacheObject> dialogs) {
        if (isAdded()) {
            Collections.sort(dialogs, new DialogCacheHelper.LastMessageDateSentComparator());
            List<DialogCacheObject> matches = new ArrayList<>();
            List<DialogCacheObject> crushes = new ArrayList<>();
            for (DialogCacheObject d : dialogs) {
                if (d.getCrush() != null && d.getCrush())
                    crushes.add(d);
                else
                    matches.add(d);
            }
            Log.wtf("Entering", "updateList");
            Log.wtf("Entering", "updateList " + dialogs.size());
        }
    }

    private class DeleteMessagesTask extends AsyncTask<String, Void, Boolean> {

        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... ids) {

            boolean result = false;
            Realm realm = CacheUtils.getInstance(user);
            try {
                result = QuickBloxChatHelper.getInstance().deleteMessagesFromDialog(ids[0], user.getId(),
                        new QuickBloxChatDialogUpdateListener() {
                            @Override
                            public void onDialogUpdated(QBChatDialog dialog) {
                                if (dialog != null) {
                                    //   setDeletedDialogCache(dialog.getDialogId());
                                }
                            }
                        });
                if (result) { //delete cache
                    realm.beginTransaction();
                    MessageCacheHelper.deleteDialogMessages(realm, ids[0]);
                    realm.commitTransaction();
                }
            } finally {
                realm.close();
            }
            return result;
        }

        protected void onPostExecute(Boolean result) {
            if (!result) { //error

            }
            deleteMessagesTask = null;
        }
    }//DeleteMessagesTask
}
