package com.bagus.purchasingapp_mtn.ui.user_profile;

import com.google.gson.JsonObject;

public interface UserProfileNavigator {
    void onUpdatePhone();

    void onUpdatePassword();

    void onError(String message);

    void onSuccess(JsonObject jsonObject, String message);
}
