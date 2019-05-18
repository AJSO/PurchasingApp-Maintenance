package com.bagus.purchasingapp_mtn.ui.user_profile;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableField;
import android.support.annotation.NonNull;

import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.base.BaseViewModel;
import com.bagus.purchasingapp_mtn.model.ApiResponse;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;

public class UserProfileViewModel extends BaseViewModel<UserProfileNavigator> {

    public final ObservableField<String> name, phone, nik, position, level;

    public UserProfileViewModel(Context context) {
        super(context);
        name = new ObservableField<>(Utils.getStringPreference(context, Constants.USER_NAME));
        nik = new ObservableField<>(Utils.getStringPreference(context, Constants.USER_NIK));
        phone = new ObservableField<>(Utils.getStringPreference(context, Constants.USER_PHONE));
        position = new ObservableField<>(Utils.getStringPreference(context, Constants.USER_POSITION));
        level = new ObservableField<>(Utils.getStringPreference(context, Constants.USER_LEVEL));
    }

    public void updatePhone() {
        getNavigator().onUpdatePhone();
    }

    public void updatePassword() {
        getNavigator().onUpdatePassword();
    }

    public void update(JsonObject jsonObject) {
        setIsLoading(true);
        getServer().updateProfile(getToken(), jsonObject)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, retrofit2.Response<ApiResponse> response) {
                        setIsLoading(false);
                        if (response.isSuccessful() && response.body().success) {
                            getNavigator().onSuccess(jsonObject, response.body().message);
                        } else {
                            getNavigator().onError(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        setIsLoading(false);
                        getNavigator().onError(getContext().getResources().getString(R.string.check_internet));
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
            return (T) new UserProfileViewModel(context);
        }
    }
}
