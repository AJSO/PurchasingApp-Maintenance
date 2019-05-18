package com.bagus.purchasingapp_mtn.ui.user_profile;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.bagus.purchasingapp_mtn.BR;
import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.base.BaseActivity;
import com.bagus.purchasingapp_mtn.databinding.UserActivityProfileBinding;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;
import com.google.gson.JsonObject;

public class UserProfileActivity extends BaseActivity<UserActivityProfileBinding, UserProfileViewModel> implements UserProfileNavigator {

    private UserActivityProfileBinding binding;
    private UserProfileViewModel viewModel;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.user_activity_profile;
    }

    @Override
    public UserProfileViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = ViewModelProviders.of(this, new UserProfileViewModel.ViewModelFactory(this)).get(UserProfileViewModel.class);
        viewModel.setNavigator(this);
        binding = getViewDataBinding();
        binding.setViewModel(viewModel);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return true;
    }

    @Override
    public void onUpdatePhone() {
        binding.phone.setEnabled(false);
        binding.buttonUpdatePhone.setEnabled(false);

        hideKeyboard();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(Constants.USER_SID, Utils.getStringPreference(this, Constants.USER_SID));
        jsonObject.addProperty(Constants.USER_NIK, Utils.getStringPreference(this, Constants.USER_NIK));
        jsonObject.addProperty(Constants.USER_NAME, Utils.getStringPreference(this, Constants.USER_NAME));
        jsonObject.addProperty(Constants.USER_PHONE, binding.phone.getText().toString()); //update phone only
        jsonObject.addProperty(Constants.USER_POSITION, Utils.getStringPreference(this, Constants.USER_POSITION));
        jsonObject.addProperty(Constants.USER_LEVEL, Utils.getStringPreference(this, Constants.USER_LEVEL));
        jsonObject.addProperty(Constants.USER_STATUS, Utils.getStringPreference(this, Constants.USER_STATUS));
        jsonObject.addProperty(Constants.USER_PASSWORD, Utils.getStringPreference(this, Constants.USER_PASSWORD));
        viewModel.update(jsonObject);
    }

    @Override
    public void onUpdatePassword() {
        hideKeyboard();

        String savedCurrentPassword = Utils.getStringPreference(this, Constants.USER_PASSWORD);
        String currentPassword = binding.passwordcurrent.getText().toString();
        String newPassword = binding.passwordnew.getText().toString();
        String confirmPassword = binding.passwordconfirm.getText().toString();

        if (currentPassword.equals(savedCurrentPassword)) {
            if (newPassword.equals(confirmPassword) || confirmPassword.equals(newPassword)) {

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty(Constants.USER_SID, Utils.getStringPreference(this, Constants.USER_SID));
                jsonObject.addProperty(Constants.USER_NIK, Utils.getStringPreference(this, Constants.USER_NIK));
                jsonObject.addProperty(Constants.USER_NAME, Utils.getStringPreference(this, Constants.USER_NAME));
                jsonObject.addProperty(Constants.USER_PHONE, Utils.getStringPreference(this, Constants.USER_PHONE));
                jsonObject.addProperty(Constants.USER_POSITION, Utils.getStringPreference(this, Constants.USER_POSITION));
                jsonObject.addProperty(Constants.USER_LEVEL, Utils.getStringPreference(this, Constants.USER_LEVEL));
                jsonObject.addProperty(Constants.USER_STATUS, Utils.getStringPreference(this, Constants.USER_STATUS));
                jsonObject.addProperty(Constants.USER_PASSWORD, binding.passwordconfirm.getText().toString());

                viewModel.update(jsonObject);

            } else {
                showAlertDialog("", "invalid your confirmation password!");
            }
        } else {
            showAlertDialog("", "invalid your current password!");
        }


    }

    @Override
    public void onError(String message) {
        showAlertDialog("Error", message);
    }

    @Override
    public void onSuccess(JsonObject jsonObject, String message) {
        showAlertDialog("Success", message);
        Utils.putPreference(this, Constants.USER_SID, jsonObject.get("userSID").getAsString());
        Utils.putPreference(this, Constants.USER_NIK, jsonObject.get("userNIK").getAsString());
        Utils.putPreference(this, Constants.USER_NAME, jsonObject.get("userName").getAsString());
        Utils.putPreference(this, Constants.USER_PHONE, jsonObject.get("userPhone").getAsString());
        Utils.putPreference(this, Constants.USER_POSITION, jsonObject.get("userPosition").getAsString());
        Utils.putPreference(this, Constants.USER_LEVEL, jsonObject.get("userLevel").getAsString());
        Utils.putPreference(this, Constants.USER_STATUS, jsonObject.get("userStatus").getAsString());
        Utils.putPreference(this, Constants.USER_PASSWORD, jsonObject.get("userPassword").getAsString());
    }
}
