package com.bagus.purchasingapp_mtn.ui.login;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.bagus.purchasingapp_mtn.base.BaseViewModel;
import com.bagus.purchasingapp_mtn.model.User;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginViewModel extends BaseViewModel<LoginNavigator> {

    public LoginViewModel(Context context) {
        super(context);
    }

    public void login() {
        Log.w("TAG", "LoginClick");
        getNavigator().onLoginClick();
    }

    public void processLogin(JsonObject jsonObject) {
        setIsLoading(true);
        getServer().login(jsonObject)
                .enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            if (response.body().success && response.body().data.userLevel.equals("2")) {
                                Utils.putPreference(getContext(), Constants.USER_SID, response.body().data.userSID);
                                Utils.putPreference(getContext(), Constants.USER_NIK, response.body().data.userNIK);
                                Utils.putPreference(getContext(), Constants.USER_NAME, response.body().data.userName);
                                Utils.putPreference(getContext(), Constants.USER_PHONE, response.body().data.userPhone);
                                Utils.putPreference(getContext(), Constants.USER_POSITION, response.body().data.userPosition);
                                Utils.putPreference(getContext(), Constants.USER_LEVEL, response.body().data.userLevel);
                                Utils.putPreference(getContext(), Constants.USER_STATUS, response.body().data.userStatus);
                                Utils.putPreference(getContext(), Constants.USER_PASSWORD, response.body().data.userPassword);
                                Utils.putPreference(getContext(), Constants.USER_TOKEN, response.body().data.userToken);
                                Utils.putPreference(getContext(), Constants.USER_LASTMODIFED, response.body().data.userLastModifed);
                                Utils.putPreference(getContext(), Constants.IS_LOGIN, true);

                                getNavigator().openStartActivity();
                            } 
                            else {
                                getNavigator().onLoginFail("Invalid Username or Password");
                            }
                        }

                        if (response.code() == 401) {
                            getNavigator().onLoginFail("Invalid Username or Password");
                        }

                        setIsLoading(false);
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        getNavigator().onLoginFail("Please check your internet connection \n" + t.getLocalizedMessage());
                    }
                });
    }

    public static class ViewModelFactory implements ViewModelProvider.Factory {
        private Context context;

        ViewModelFactory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new LoginViewModel(context);
        }
    }
}
