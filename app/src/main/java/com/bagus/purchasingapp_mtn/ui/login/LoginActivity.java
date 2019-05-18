package com.bagus.purchasingapp_mtn.ui.login;

import android.Manifest;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;

import com.bagus.purchasingapp_mtn.BR;
import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.base.BaseActivity;
import com.bagus.purchasingapp_mtn.databinding.ActivityLoginBinding;
import com.bagus.purchasingapp_mtn.ui.SplashScreen;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;
import com.google.gson.JsonObject;

public class LoginActivity extends BaseActivity<ActivityLoginBinding, LoginViewModel> implements LoginNavigator {

    private ActivityLoginBinding binding;
    private LoginViewModel viewModel;

    public static Intent newIntent(Context context) {
        return new Intent(context, LoginActivity.class);
    }

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public LoginViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new LoginViewModel.ViewModelFactory(this)).get(LoginViewModel.class);
        viewModel.setNavigator(this);
        binding = getViewDataBinding();
        binding.setViewModel(viewModel);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("LOGIN - Maintenance");
        }

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(),
                        Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE},
                        101);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{
                                Manifest.permission.CAMERA,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        101);
            }
        }
    }

    @Override
    public void openStartActivity() {
        Intent intent = new Intent(LoginActivity.this, SplashScreen.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); /*(optional)*/
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); /*(optional)*/
        startActivity(intent);
        finish();
    }

    @Override
    public void onLoginClick() {
        hideKeyboard();

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.USER_NIK, binding.nik.getText().toString());
        jsonObject.addProperty(Constants.USER_PASSWORD, binding.password.getText().toString());

        viewModel.processLogin(jsonObject);
    }

    @Override
    public void onLoginFail(String message) {
        showAlertDialog("Failed", message);
    }

    @Override
    protected void onStart() {
        super.onStart();


        binding.nik.setText(Utils.getStringPreference(this, Constants.USER_NIK));
    }
}
