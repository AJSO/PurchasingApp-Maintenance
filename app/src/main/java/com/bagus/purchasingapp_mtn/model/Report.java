package com.bagus.purchasingapp_mtn.model;

import com.bagus.purchasingapp_mtn.utils.Constants;

import java.io.Serializable;
import java.util.List;

public class Report {

    public Boolean success;
    public String message;

    public List<Data> data;

    public class Data implements Serializable {
        public String reportSID;
        public String reportNo;
        public String reportTitle;
        public String reportDescription;
        public String reportLocation;
        public String reportStatus;
        public String reportCreateAt;
        public String reportLastModifed;
        public User.Data reportByUser;
        public User.Data reportByMaintenance;
        public User.Data reportByAdmin;
        public String reportPhoto;

        public boolean isPending(String reportStatus) {
            return reportStatus.equals(Constants.PENDING) || reportStatus.equals(Constants.READ);
        }

        public boolean isProcessed(String reportStatus) {
            return reportStatus.equals(Constants.PROCESSED);
        }
    }
}
