package com.bagus.purchasingapp_mtn.ui.user_main;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.support.annotation.NonNull;

import com.bagus.purchasingapp_mtn.base.BaseViewModel;
import com.bagus.purchasingapp_mtn.model.ApiResponse;
import com.bagus.purchasingapp_mtn.model.Report;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserMainViewModel extends BaseViewModel<UserMainNavigator> {

    MutableLiveData<List<Report.Data>> data = new MutableLiveData<>();

    public UserMainViewModel(Context context) {
        super(context);
    }

    public MutableLiveData<List<Report.Data>> getReport() {
        getNavigator().showLoading();
        getServer().reportRead(Utils.getStringPreference(getContext(), Constants.USER_TOKEN),
                Utils.getStringPreference(getContext(), Constants.USER_SID))
                .enqueue(new Callback<Report>() {
                    @Override
                    public void onResponse(Call<Report> call, Response<Report> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            data.setValue(response.body().data);
                        } else {
                            getNavigator().onError(response.message());
                        }

                        getNavigator().hideLoading();
                    }

                    @Override
                    public void onFailure(Call<Report> call, Throwable t) {
                        getNavigator().onError(t.getMessage());
                        getNavigator().hideLoading();
                    }
                });

        return data;
    }
    

    public void itemClick(Report.Data data) {
        getNavigator().onItemClick(data);
    }

    public static class MainViewModelFactory implements ViewModelProvider.Factory {
        private Context context;

        MainViewModelFactory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new UserMainViewModel(context);
        }
    }
}
