package me.muapp.android.UI.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.util.SortedList;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.quickblox.chat.model.QBChatDialog;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatDialogsListener;
import me.muapp.android.Classes.Quickblox.Chats.QuickBloxChatHelper;
import me.muapp.android.Classes.Quickblox.cache.CacheUtils;
import me.muapp.android.Classes.Quickblox.cache.DialogCacheHelper;
import me.muapp.android.Classes.Quickblox.cache.DialogCacheObject;
import me.muapp.android.Classes.Quickblox.messages.QuickBloxMessagesListener;
import me.muapp.android.Classes.Util.PreferenceHelper;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.CrushesAdapter;
import me.muapp.android.UI.Adapter.MatchesAdapter;
import me.muapp.android.UI.Fragment.Interface.OnFragmentInteractionListener;

public class ChatsFragment extends Fragment implements OnFragmentInteractionListener, QuickBloxChatDialogsListener, QuickBloxMessagesListener {
    private static final String TAG = "ChatsFragment";
    private static final String ARG_CURRENT_USER = "CURRENT_USER";
    User user;
    OnFragmentInteractionListener mListener;
    PreferenceHelper preferenceHelper;
    RecyclerView recycler_matches, recycler_crushes;
    MatchesAdapter matchesAdapter;
    CrushesAdapter crushesAdapter;

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
        View v = inflater.inflate(R.layout.fragment_chat, container, false);
        recycler_matches = (RecyclerView) v.findViewById(R.id.recycler_matches);
        recycler_crushes = (RecyclerView) v.findViewById(R.id.recycler_crushes);
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        preferenceHelper = new PreferenceHelper(getContext());
        matchesAdapter = new MatchesAdapter(getContext());
        crushesAdapter = new CrushesAdapter(getContext());
        recycler_matches.setLayoutManager(new LinearLayoutManager(getContext()));
        LinearLayoutManager llmh = new LinearLayoutManager(getContext());
        llmh.setOrientation(LinearLayoutManager.HORIZONTAL);
        recycler_crushes.setLayoutManager(llmh);
        recycler_matches.setAdapter(matchesAdapter);
        recycler_crushes.setAdapter(crushesAdapter);
        updateData();
    }

    private SortedList<DialogCacheObject> initializeSortedDialogs() {
        return new SortedList<>(DialogCacheObject.class, new SortedList.Callback<DialogCacheObject>() {
            @Override
            public void onInserted(int position, int count) {
            }

            @Override
            public void onRemoved(int position, int count) {
            }

            @Override
            public void onMoved(int fromPosition, int toPosition) {
            }

            @Override
            public int compare(DialogCacheObject lhs, DialogCacheObject rhs) {
                long valueA = lhs.getLastMessageDateSent();
                if (valueA == 0) {
                    valueA = (lhs.getUpdatedAt() != null) ? lhs.getUpdatedAt().getTime() / 1000 : lhs.getCreatedAt().getTime() / 1000;
                }
                long valueB = rhs.getLastMessageDateSent();
                if (valueB == 0) {
                    valueB = (rhs.getUpdatedAt() != null) ? rhs.getUpdatedAt().getTime() / 1000 : rhs.getCreatedAt().getTime() / 1000;
                }

                if (valueB < valueA) {
                    return -1;
                } else {
                    return 1;
                }
            }

            @Override
            public void onChanged(int position, int count) {
            }

            @Override
            public boolean areContentsTheSame(DialogCacheObject oldItem, DialogCacheObject newItem) {
                return oldItem.getDialogId().equals(newItem.getDialogId());
            }

            @Override
            public boolean areItemsTheSame(DialogCacheObject item1, DialogCacheObject item2) {
                return item1.equals(item2);
            }
        });
    }

    public void updateData() {
        Log.wtf(TAG, "Updating");
        DialogCacheHelper.getDialogs(CacheUtils.getInstance(user), "", new RealmChangeListener<RealmResults<DialogCacheObject>>() {
            @Override
            public void onChange(RealmResults<DialogCacheObject> element) {
                Log.wtf(TAG, "Updating " + element.size());
                SortedList<DialogCacheObject> matches = initializeSortedDialogs();
                SortedList<DialogCacheObject> crushes = initializeSortedDialogs();
                for (DialogCacheObject d : element) {
                    if (d.getCrush() != null && d.getCrush())
                        crushes.add(d);
                    else
                        matches.add(d);
                }
                matchesAdapter.setDialogs(matches);
                crushesAdapter.setDialogs(crushes);
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
    public void onFragmentInteraction(String name, Object data) {
    }

    @Override
    public void onDialogsLoaded(List<QBChatDialog> dialogs, boolean success) {
        DialogCacheHelper.setDialogs(CacheUtils.getInstance(user), dialogs, user.getId(), true);
        ArrayList<DialogCacheObject> cache = new ArrayList<>();
        for (QBChatDialog qb : dialogs) {
            cache.add(DialogCacheHelper.dialogToCache(qb, user.getId()));
        }
        updateData();
    }

    @Override
    public void onDialogUpdated(String chatDialog) {
        QuickBloxChatHelper.getInstance().getDialogs(this);
    }

    @Override
    public void onNewDialog() {
        QuickBloxChatHelper.getInstance().getDialogs(this);
    }
}



