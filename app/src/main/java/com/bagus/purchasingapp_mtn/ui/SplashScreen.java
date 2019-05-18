package com.bagus.purchasingapp_mtn.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.ui.login.LoginActivity;
import com.bagus.purchasingapp_mtn.ui.user_main.UserMainActivity;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;

public class SplashScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent;
        if (!Utils.getBooleanPreference(this, Constants.IS_LOGIN)) {
            intent = new Intent(this, LoginActivity.class);
        } else {
            intent = new Intent(this, UserMainActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
        finish();
    }
}
