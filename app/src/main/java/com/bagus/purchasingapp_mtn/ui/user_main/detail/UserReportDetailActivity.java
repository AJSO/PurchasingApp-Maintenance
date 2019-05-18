package com.bagus.purchasingapp_mtn.ui.user_main.detail;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AlertDialog;
import android.view.MenuItem;
import android.view.View;

import com.bagus.purchasingapp_mtn.BR;
import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.api.AppServerConfig;
import com.bagus.purchasingapp_mtn.base.BaseActivity;
import com.bagus.purchasingapp_mtn.databinding.UserActivityReportDetailBinding;
import com.bagus.purchasingapp_mtn.model.Report;
import com.bagus.purchasingapp_mtn.ui.photo_viewer.PhotoViewerActivity;
import com.bagus.purchasingapp_mtn.ui.user_main.request.UserReportRequestActivity;
import com.bagus.purchasingapp_mtn.utils.BindingUtils;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bumptech.glide.Glide;

public class UserReportDetailActivity extends BaseActivity<UserActivityReportDetailBinding, UserReportDetailViewModel> implements Navigator {

    UserActivityReportDetailBinding binding;
    UserReportDetailViewModel viewModel;

    Report.Data data;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.user_activity_report_detail;
    }

    @Override
    public UserReportDetailViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new UserReportDetailViewModel.ViewModelFactory(this)).get(UserReportDetailViewModel.class);
        binding.setViewModel(viewModel);
        viewModel.setNavigator(this);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //getData from list item
        Intent intent = getIntent();
        data = (Report.Data) intent.getSerializableExtra("data");

        //setup data to detail
        getSupportActionBar().setTitle(data.reportNo);
        binding.setItem(data);
        Glide.with(this).asBitmap().load(AppServerConfig.IMAGE_URL + data.reportPhoto).into(binding.photo);
        BindingUtils.setStatus(binding.layoutStatus, data.reportStatus);
        BindingUtils.statusName(binding.tvStatus, data.reportStatus);

        //button view photo
        binding.photo.setOnClickListener(v -> {
            Intent i = new Intent(this, PhotoViewerActivity.class);
            i.putExtra("photo", data.reportPhoto);
            Pair<View, String> pair = Pair.create(binding.photo, ViewCompat.getTransitionName(binding.photo));
            ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pair);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                startActivity(i, optionsCompat.toBundle());
            } else {
                startActivity(i);
            }
        });

        
        binding.proccess.setOnClickListener(v -> {
            viewModel.updateStatus(data.reportSID, Constants.PROCESSED);          
        });
        
        
        binding.repair.setOnClickListener(v -> {
            new AlertDialog.Builder(this, R.style.AlertDialogTheme).setTitle("Konfirmasi").setMessage("Apakah anda yakin?")
                    .setPositiveButton("CANCEL", null)
                    .setNegativeButton("YES", (dialog, which) -> {
                        viewModel.updateStatus(data.reportSID, Constants.REPAIRING);
                    }).show();
        });
        
        binding.purchase.setOnClickListener(v -> {
            Intent i = new Intent(this, UserReportRequestActivity.class);
            i.putExtra("data", data);
            startActivity(i);
        });
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
        toast("Success");
        finish();
    }

    @Override
    public void onError(String message) {

    }
}
