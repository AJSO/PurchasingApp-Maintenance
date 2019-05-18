package com.bagus.purchasingapp_mtn.ui.user_main;

import com.bagus.purchasingapp_mtn.model.Report;

public interface UserMainNavigator {
    void onItemClick(Report.Data data);

    void showLoading();

    void hideLoading();

    void onError(String message);
}
