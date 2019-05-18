package com.bagus.purchasingapp_mtn.api;

public class AppServer {
    public static AppApi getApi() {
        return AppServerConfig.getClient().create(AppApi.class);
    }
}
