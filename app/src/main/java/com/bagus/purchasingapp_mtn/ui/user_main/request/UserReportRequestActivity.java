package com.bagus.purchasingapp_mtn.ui.user_main.request;

import android.app.AlertDialog;
import android.app.Dialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bagus.purchasingapp_mtn.BR;
import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.base.BaseActivity;
import com.bagus.purchasingapp_mtn.databinding.UserActivityRequestBinding;
import com.bagus.purchasingapp_mtn.model.PurchaseItem;
import com.bagus.purchasingapp_mtn.model.Report;

public class UserReportRequestActivity extends BaseActivity<UserActivityRequestBinding, UserReportRequestViewModel> implements Navigator {
    
    UserActivityRequestBinding binding;
    UserReportRequestViewModel viewModel;
    
    Report.Data data;
    
    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.user_activity_request;
    }

    @Override
    public UserReportRequestViewModel getViewModel() {
        return viewModel;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Purchasing Request");
        }

        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new UserReportRequestViewModel.ViewModelFactory(this)).get(UserReportRequestViewModel.class);
        viewModel.setNavigator(this);
        binding.setViewModel(viewModel);
        
        binding.recyclerView.setNestedScrollingEnabled(true);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //getData from list item
        Intent intent = getIntent();
        data = (Report.Data) intent.getSerializableExtra("data");
        binding.setItem(data);
        
        viewModel.getItemsList().observe(this, purchaseItems -> {
           binding.recyclerView.setAdapter(new UserReportRequestAdapter(this, purchaseItems, viewModel));
        });
        
        binding.add.setOnClickListener(v -> dialogPurchase(null, 0));
    }
    
    void dialogPurchase(PurchaseItem item, int position) {
        
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AlertDialogTheme);
        AlertDialog dialog = builder.create();
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_purchase_item, null);
        dialog.setView(view);

        EditText etName = view.findViewById(R.id.nama_barang);
        EditText etQty = view.findViewById(R.id.quantity);
        EditText etNote = view.findViewById(R.id.note);
        EditText etUom = view.findViewById(R.id.satuan);
        
        
        if (item == null) {

            dialog.setTitle("Tambah Item");
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "ADD", (dialog1, which) -> {
            });
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", (dialog1, which) -> dialog1.dismiss());
            dialog.show();

            Button add = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            add.setOnClickListener(v -> {
                if (etName.getText().toString().equals("")) {
                    etName.setError("Please input Product Name");
                    etName.requestFocus();
                } else if (etQty.getText().toString().equals("")) {
                    etQty.setError("Please input Quantity");
                    etQty.requestFocus();
                } else if (etUom.getText().toString().equals("")) {
                    etUom.setError("Please input UOM Quantity");
                    etUom.requestFocus();
                } else {
                    PurchaseItem addItem = new PurchaseItem();
                    addItem.name.set(etName.getText().toString().trim());
                    addItem.quantity.set(etQty.getText().toString().trim());
                    addItem.note.set(etNote.getText().toString().trim());
                    addItem.satuan.set(etUom.getText().toString().trim());
                    viewModel.addItem(addItem);
                    dialog.dismiss();
                }
            });

        } else {
            
            dialog.setTitle("Edit Item");
            dialog.setCancelable(false);
            dialog.setButton(DialogInterface.BUTTON_POSITIVE, "UPDATE", (dialog1, which) -> {});
            dialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", (dialog1, which) -> dialog1.dismiss());
            dialog.show();
            
            etName.setText(item.name.get());
            etQty.setText(item.quantity.get());
            etUom.setText(item.satuan.get());
            etNote.setText(item.note.get());

            Button add = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            add.setOnClickListener(v -> {
                if (etName.getText().toString().equals("")) {
                    etName.setError("Please input Product Name");
                    etName.requestFocus();
                } else if (etQty.getText().toString().equals("")) {
                    etQty.setError("Please input Quantity");
                    etQty.requestFocus();
                } else if (etUom.getText().toString().equals("")) {
                    etUom.setError("Please input UOM Quantity");
                    etUom.requestFocus();
                } else {
                    PurchaseItem itemUpdate = new PurchaseItem();
                    itemUpdate.name.set(etName.getText().toString().trim());
                    itemUpdate.quantity.set(etQty.getText().toString().trim());
                    itemUpdate.note.set(etNote.getText().toString().trim());
                    itemUpdate.satuan.set(etUom.getText().toString().trim());
                    viewModel.updateItem(itemUpdate, position);
                    dialog.dismiss();
                }
            });
        }
    }
    
    @Override
    public void showLoading() {
        showProgressDialog("Please wait...");
    }

    @Override
    public void hideLoading() {
        hideProgressDialog();
    }

    @Override
    public void onSuccess() {
        showAlertDialog("Success", "Report Updated");
    }

    @Override
    public void onError(String message) {
        showAlertDialog("Failure", message);
    }

    @Override
    public void updateItem(PurchaseItem item, int position) {
        dialogPurchase(item, position);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return true;
    }
}
