package com.bagus.purchasingapp_mtn.ui.user_main.detail;
public interface Navigator {
    void showLoading();
    void hideLoading();
    void onSuccess();
    void onError(String message);
}