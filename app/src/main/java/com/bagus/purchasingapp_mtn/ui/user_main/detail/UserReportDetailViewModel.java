package com.bagus.purchasingapp_mtn.ui.user_main.detail;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.bagus.purchasingapp_mtn.base.BaseNavigator;
import com.bagus.purchasingapp_mtn.base.BaseViewModel;
import com.bagus.purchasingapp_mtn.model.ApiResponse;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;
import com.google.gson.JsonObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReportDetailViewModel extends BaseViewModel<Navigator> {

    public UserReportDetailViewModel(Context context) {
        super(context);
    }

    public void updateStatus(String reportSID, String status) {
        getNavigator().showLoading();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("reportSID", reportSID);
        jsonObject.addProperty("reportStatus",status);
        jsonObject.addProperty("userSID", Utils.getStringPreference(getContext(), Constants.USER_SID));
        getServer().reportUpdateStatus(Utils.getStringPreference(getContext(), Constants.USER_TOKEN), jsonObject)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        getNavigator().hideLoading();
                        if (response.body() != null && response.body().success) {
                            getNavigator().onSuccess();
                        } else {
                            getNavigator().onError(response.message());
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        getNavigator().hideLoading();
                        getNavigator().onError(t.getMessage());
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
            return (T) new UserReportDetailViewModel(context);
        }
    }
}
