package me.muapp.android.Classes.Quickblox.dialog;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import io.realm.Realm;
import me.muapp.android.Classes.Internal.User;
import me.muapp.android.Classes.Quickblox.cache.CacheUtils;
import me.muapp.android.Classes.Quickblox.cache.MessageCacheHelper;

public class DeleteOldMessagesTask extends AsyncTask<String, Void, Void> {

    User currentUser = null;

    public DeleteOldMessagesTask(@NonNull User currentUser){
        this.currentUser = currentUser;
    }

    @Override
    protected Void doInBackground(String... data) {
        Realm realm = CacheUtils.getInstance(currentUser);

        final String dialogId = data[0];
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                MessageCacheHelper.deleteOldDialogMessages(realm, dialogId);
            }
        });

        realm.close();
        return null;
    }
}
