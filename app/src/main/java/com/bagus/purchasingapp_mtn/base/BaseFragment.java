package com.bagus.purchasingapp_mtn.base;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.api.AppApi;
import com.bagus.purchasingapp_mtn.api.AppServer;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;

public abstract class BaseFragment<T extends ViewDataBinding, V extends BaseViewModel> extends Fragment {

    private BaseActivity activity;
    private View rootView;
    private T binding;
    private V viewModel;

    public abstract int getBindingVariable();

    public abstract @LayoutRes
    int getLayoutId();

    public abstract V getViewModel();

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BaseActivity) {
            BaseActivity activity = (BaseActivity) context;
            this.activity = activity;
            activity.onFragmentAttached();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = getViewModel();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, getLayoutId(), container, false);
        rootView = binding.getRoot();
        return rootView;
    }

    @Override
    public void onDetach() {
        activity = null;
        super.onDetach();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.setVariable(getBindingVariable(), viewModel);
        binding.executePendingBindings();
    }

    public BaseActivity getBaseActivity() {
        return activity;
    }

    public T getViewDataBinding() {
        return binding;
    }

    public void hideKeyboard() {
        if (activity != null) {
            activity.hideKeyboard();
        }
    }

    public boolean isNetworkConnected() {
        return activity != null && activity.isNetworkConnected();
    }

    public void openActivityOnTokenExpire() {
        if (activity != null) {
            activity.openActivityOnTokenExpire();
        }
    }

    protected AppApi getServer() {
        return AppServer.getApi();
    }

    protected String getToken() {
        return Utils.getStringPreference(getActivity(), Constants.USER_TOKEN);
    }

    public interface Callback {

        void onFragmentAttached();

        void onFragmentDetached(String tag);
    }

    public void showAlertDialog(String title, String message) {
        new AlertDialog.Builder(new ContextThemeWrapper(getActivity(), R.style.AlertDialogTheme)).setTitle(title).setMessage(message).show();
    }
}
