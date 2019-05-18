package com.bagus.purchasingapp_mtn.base;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.databinding.ObservableBoolean;

import com.bagus.purchasingapp_mtn.api.AppApi;
import com.bagus.purchasingapp_mtn.api.AppServer;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;

import java.lang.ref.WeakReference;

public abstract class BaseViewModel<N> extends ViewModel {

    private final ObservableBoolean isLoading = new ObservableBoolean(false);

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private WeakReference<N> navigator;

    public BaseViewModel(Context context) {
        this.context = context;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
    }

    protected AppApi getServer() {
        return AppServer.getApi();
    }

    protected String getToken() {
        return Utils.getStringPreference(context, Constants.USER_TOKEN);
    }

    public ObservableBoolean getIsLoading() {
        return isLoading;
    }

    public void setIsLoading(boolean isLoading) {
        this.isLoading.set(isLoading);
    }

    public N getNavigator() {
        return navigator.get();
    }

    public void setNavigator(N navigator) {
        this.navigator = new WeakReference<>(navigator);
    }

    public Context getContext() {
        return context;
    }
}
