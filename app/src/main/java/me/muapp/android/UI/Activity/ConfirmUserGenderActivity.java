package me.muapp.android.UI.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import me.muapp.android.Classes.Internal.User;
import me.muapp.android.R;

import static me.muapp.android.UI.Activity.ConfirmUserActivity.CONFIRMED_GENDER;

public class ConfirmUserGenderActivity extends BaseActivity implements View.OnClickListener {
    ImageView img_male, img_female;
    FloatingActionButton fab_gender_continue;
    User.Gender currentGender = User.Gender.Unknown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_user_gender);
        getSupportActionBar().hide();
        img_female = (ImageView) findViewById(R.id.img_female);
        img_male = (ImageView) findViewById(R.id.img_male);
        fab_gender_continue = (FloatingActionButton) findViewById(R.id.fab_gender_continue);
        img_female.setColorFilter(ContextCompat.getColor(this, R.color.color_muapp_dark));
        img_male.setColorFilter(ContextCompat.getColor(this, R.color.color_muapp_dark));
        img_female.setOnClickListener(this);
        img_male.setOnClickListener(this);
        fab_gender_continue.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_male:
                img_female.setColorFilter(ContextCompat.getColor(this, R.color.color_muapp_dark));
                img_male.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
                currentGender = User.Gender.Male;
                break;
            case R.id.img_female:
                img_male.setColorFilter(ContextCompat.getColor(this, R.color.color_muapp_dark));
                img_female.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
                currentGender = User.Gender.Female;
                break;
            case R.id.fab_gender_continue:
                if (currentGender == User.Gender.Unknown) {
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .playOn(img_female);
                    YoYo.with(Techniques.Wobble)
                            .duration(700)
                            .playOn(img_male);
                } else {
                    Intent returnIntent = new Intent();
                    setResult(RESULT_OK, returnIntent);
                    returnIntent.putExtra(CONFIRMED_GENDER, currentGender.getValue());
                    finish();
                }
                break;
        }

    }
}
