package com.bagus.purchasingapp_mtn.model;

import android.databinding.ObservableField;

public class PurchaseItem {
   
    static int pos = 1;
    
    public ObservableField<String> name = new ObservableField<>();
    public ObservableField<String> quantity = new ObservableField<>();
    public ObservableField<String> satuan = new ObservableField<>();
    public ObservableField<String> note = new ObservableField<>();
    
    public static int position() {
        return pos++;
    }
}
