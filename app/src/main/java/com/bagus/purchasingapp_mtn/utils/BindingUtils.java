package com.bagus.purchasingapp_mtn.utils;

import android.databinding.BindingAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bagus.purchasingapp_mtn.R;

public final class BindingUtils {

    @BindingAdapter(value = {"app:codestatus"})
    public static void setStatus(LinearLayout view, String codestatus) {
        if (codestatus.equals("0")) {
            view.setBackgroundResource(R.drawable.bg_label_status_yellow);
        } else if (codestatus.equals("1")) {
            view.setBackgroundResource(R.drawable.bg_label_status_blue);
        } else if (codestatus.equals("2")) {
            view.setBackgroundResource(R.drawable.bg_label_status_green);
        } else if (codestatus.equals("3")) {
            view.setBackgroundResource(R.drawable.bg_label_status_blue_dark);
        } else if (codestatus.equals("4")) {
            view.setBackgroundResource(R.drawable.bg_label_status_green);
        } else if (codestatus.equals("5")) {
            view.setBackgroundResource(R.drawable.bg_label_status_red);
        } else if (codestatus.equals("6")) {
            view.setBackgroundResource(R.drawable.bg_label_status_blue);
        } else if (codestatus.equals("7")) {
            view.setBackgroundResource(R.drawable.bg_label_status_red);
        } else if (codestatus.equals("8")) {
            view.setBackgroundResource(R.drawable.bg_label_status_red);
        } else {
            view.setBackgroundResource(R.drawable.bg_label_status_red);
        }
    }

    @BindingAdapter(value = {"app:statusName"})
    public static void statusName(TextView view, String codestatus) {
        if (codestatus.equals("0")) {
            view.setText("PENDING");
        } else if (codestatus.equals("1")) {
            view.setText("READ");
        } else if (codestatus.equals("2")) {
            view.setText("PROCESSED");
        } else if (codestatus.equals("3")) {
            view.setText("REPAIRING");
        } else if (codestatus.equals("4")) {
            view.setText("APPROVED");
        } else if (codestatus.equals("5")) {
            view.setText("REJECTED");
        } else if (codestatus.equals("6")) {
            view.setText("PURCHASED");
        } else if (codestatus.equals("7")) {
            view.setText("COMPLETED");
        } else if (codestatus.equals("8")) {
            view.setText("CANCELED");
        } else {
            view.setText("STATUS NOT DEFINE");
        }
    }
}
