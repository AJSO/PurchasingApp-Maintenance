package com.bagus.purchasingapp_mtn.model;

import java.io.Serializable;

public class User {
    public Boolean success;
    public String message;
    public Data data;

    public class Data implements Serializable {
        public String userSID;
        public String userNIK;
        public String userName;
        public String userPhone;
        public String userPosition;
        public String userLevel;
        public String userStatus;
        public String userPassword;
        public String userToken;
        public String userLastModifed;
    }
}
