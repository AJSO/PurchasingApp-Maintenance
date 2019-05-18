package com.bagus.purchasingapp_mtn.ui.user_laporan;

public interface UserReportNavigator {
    void showProgress();

    void hideProgress();

    void onResult(boolean status, String responseMessage);
}
