package com.bagus.purchasingapp_mtn.ui.user_main.request;

import com.bagus.purchasingapp_mtn.model.PurchaseItem;

public interface Navigator {
    void showLoading();
    void hideLoading();
    void onSuccess();
    void onError(String message);
    void updateItem(PurchaseItem item, int position);
}