package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import me.muapp.android.R;

public class ChatActivity extends BaseActivity {
    public static final String DIALOG_EXTRA = "DIALOG_EXTRA";
    Toolbar toolbar;
    TextView toolbar_opponent_fullname;
    int opponentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        toolbar = (Toolbar) findViewById(R.id.chat_toolbar);
        toolbar_opponent_fullname = (TextView) findViewById(R.id.toolbar_opponent_fullname);

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    public void setupLayout() {


    }

    private void setOpponentId() {

    }

}


