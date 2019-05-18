package com.bagus.purchasingapp_mtn.base;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.api.AppApi;
import com.bagus.purchasingapp_mtn.api.AppServer;
import com.bagus.purchasingapp_mtn.ui.login.LoginActivity;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;


public abstract class BaseActivity<T extends ViewDataBinding, V extends BaseViewModel> extends AppCompatActivity
        implements BaseFragment.Callback {

    private ProgressDialog progressDialog;
    private T binding;
    private V viewModel;

    public abstract int getBindingVariable();

    public abstract @LayoutRes
    int getLayoutId();

    public abstract V getViewModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        performDataBinding();
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    public T getViewDataBinding() {
        return binding;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public boolean hasPermission(String permission) {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M ||
                checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED;
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    private void performDataBinding() {
        binding = DataBindingUtil.setContentView(this, getLayoutId());
        this.viewModel = viewModel == null ? getViewModel() : viewModel;
        binding.setVariable(getBindingVariable(), viewModel);
        binding.executePendingBindings();
    }

    public boolean isNetworkConnected() {
        return Utils.isNetworkConnected(getApplicationContext());
    }

    public void openActivityOnTokenExpire() {
        Toast.makeText(this, "Token Expired!", Toast.LENGTH_SHORT).show();
        Utils.putPreference(this, Constants.IS_LOGIN, false);
        startActivity(LoginActivity.newIntent(this));
        finish();
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermissionsSafely(String[] permissions, int requestCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, requestCode);
        }
    }

    @Override
    public void onFragmentAttached() {

    }

    @Override
    public void onFragmentDetached(String tag) {

    }

    protected AppApi getServer() {
        return AppServer.getApi();
    }

    protected String getToken() {
        return Utils.getStringPreference(this, Constants.USER_TOKEN);
    }

    public void checkLogin() {
        if (!Utils.getBooleanPreference(this, Constants.IS_LOGIN)) {
            startActivity(LoginActivity.newIntent(this));
            finish();
        }
    }

    public void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogTheme)).setTitle(title).setMessage(message).setPositiveButton("CLOSE", (dialog, which) -> dialog.dismiss()).show();
    }

    public void showProgressDialog(String message) {
        progressDialog.setMessage(message);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
        }
    }

    public void hideProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.cancel();
        }
    }
    
    public void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

}
