package me.muapp.android.UI.Activity;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.muapp.android.Application.MuappApplication;
import me.muapp.android.Classes.FirebaseAnalytics.Analytics;
import me.muapp.android.Classes.Internal.MuappQuote;
import me.muapp.android.Classes.Internal.UserContent;
import me.muapp.android.R;
import me.muapp.android.UI.Adapter.MuappQuotesAdapter;

public class AddQuoteActivity extends BaseActivity {
    ViewPager pager_quotes;
    MuappQuotesAdapter quotesAdapter;
    List<MuappQuote> quoteList;
    ImageButton bnt_quote_left, bnt_quote_right;
    EditText et_quote_comment;
    ArrayList<String> usedQuotes = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_quote);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pager_quotes = (ViewPager) findViewById(R.id.pager_quotes);
        bnt_quote_left = (ImageButton) findViewById(R.id.bnt_quote_left);
        bnt_quote_right = (ImageButton) findViewById(R.id.bnt_quote_right);
        et_quote_comment = (EditText) findViewById(R.id.et_quote_comment);
        quotesAdapter = new MuappQuotesAdapter(this);
        pager_quotes.setAdapter(quotesAdapter);
        quoteList = new ArrayList<>();
        DatabaseReference contentReference = FirebaseDatabase.getInstance().getReference("content").child(String.valueOf(loggedUser.getId()));
        contentReference.orderByChild("catContent").equalTo("contentQte").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot s : dataSnapshot.getChildren()) {
                    UserContent c = s.getValue(UserContent.class);
                    if (c != null) {
                        usedQuotes.add(c.getQuoteId());
                    }
                }
                FirebaseDatabase.getInstance().getReference("quotes").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            for (DataSnapshot s : dataSnapshot.getChildren()) {
                                MuappQuote q = s.getValue(MuappQuote.class);
                                if (q != null) {
                                    if (!usedQuotes.contains(s.getKey())) {
                                        q.setKey(s.getKey());
                                        quoteList.add(q);
                                    }
                                }
                            }
                            quotesAdapter.setQuotes(quoteList);
                        } catch (Exception x) {
                            x.printStackTrace();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        pager_quotes.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        bnt_quote_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager_quotes.setCurrentItem(pager_quotes.getCurrentItem() - 1, true);
            }
        });

        bnt_quote_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pager_quotes.setCurrentItem(pager_quotes.getCurrentItem() + 1, true);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.action_publish:
                publishThisQuote();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void publishThisQuote() {
        Bundle publishBundle = new Bundle();
        publishBundle.putString(Analytics.My_Profile_Add.MY_PROFILE_ADD_PROPERTY.Publish.toString(), Analytics.My_Profile_Add.MY_PROFILE_ADD_VALUES.Quote.toString());
        mFirebaseAnalytics.logEvent(Analytics.My_Profile_Add.MY_PROFILE_ADD_EVENT.My_Profile_Add_Type.toString(), publishBundle);


        if (!TextUtils.isEmpty(et_quote_comment.getText().toString())) {
            showProgressDialog();
            UserContent thisContent = new UserContent();
            thisContent.setComment(et_quote_comment.getText().toString());
            thisContent.setCreatedAt(new Date().getTime());
            thisContent.setLikes(0);
            thisContent.setCatContent("contentQte");
            thisContent.setQuoteId(quoteList.get(pager_quotes.getCurrentItem()).getKey());
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child(MuappApplication.DATABASE_REFERENCE).child("content").child(String.valueOf(loggedUser.getId()));
            String key = ref.push().getKey();
            ref.child(key).setValue(thisContent).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    hideProgressDialog();
                    setResult(RESULT_OK);
                    finish();
                }
            });
        } else {
            YoYo.with(Techniques.Wobble)
                    .duration(700)
                    .playOn(et_quote_comment);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.publish_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
}
