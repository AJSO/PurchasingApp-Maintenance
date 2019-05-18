package com.bagus.purchasingapp_mtn.ui.user_main.request;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.databinding.ObservableArrayList;
import android.databinding.ObservableList;
import android.support.annotation.NonNull;

import com.bagus.purchasingapp_mtn.base.BaseViewModel;
import com.bagus.purchasingapp_mtn.model.ApiResponse;
import com.bagus.purchasingapp_mtn.model.PurchaseItem;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;
import com.google.gson.JsonObject;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserReportRequestViewModel extends BaseViewModel<Navigator> {
    
    public ObservableList<PurchaseItem> items = new ObservableArrayList<>();
    MutableLiveData<List<PurchaseItem>> itemsList = new MutableLiveData<>();
    
    public UserReportRequestViewModel(Context context) {
        super(context);
    }

    public void addItem(PurchaseItem item) {
        items.add(item);
    }
    
    public void updateItem(PurchaseItem item, int position) {
        if (itemsList.getValue() != null) {
            itemsList.getValue().get(position).name.set(item.name.get());
            itemsList.getValue().get(position).quantity.set(item.quantity.get());
            itemsList.getValue().get(position).satuan.set(item.satuan.get());
            itemsList.getValue().get(position).note.set(item.note.get());
        }
    }

    public MutableLiveData<List<PurchaseItem>> getItemsList() {
        itemsList.setValue(items);
        return itemsList;
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
    
    public void editItem(PurchaseItem purchaseItem, int position) {
        getNavigator().updateItem(purchaseItem, position);
    }
    
    public static class ViewModelFactory implements ViewModelProvider.Factory {
        private Context context;

        ViewModelFactory(Context context) {
            this.context = context;
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
            return (T) new UserReportRequestViewModel(context);
        }
    }
}
