package com.bagus.purchasingapp_mtn.ui.login;

public interface LoginNavigator {
    void openStartActivity();

    void onLoginClick();

    void onLoginFail(String message);
}
