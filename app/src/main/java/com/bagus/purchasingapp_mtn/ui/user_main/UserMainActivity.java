package com.bagus.purchasingapp_mtn.ui.user_main;

import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bagus.purchasingapp_mtn.BR;
import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.base.BaseActivity;
import com.bagus.purchasingapp_mtn.databinding.UserActivityMainBinding;
import com.bagus.purchasingapp_mtn.model.Report;
import com.bagus.purchasingapp_mtn.service.AppService;
import com.bagus.purchasingapp_mtn.ui.SplashScreen;
import com.bagus.purchasingapp_mtn.ui.user_laporan.UserReportAddActivity;
import com.bagus.purchasingapp_mtn.ui.user_main.detail.UserReportDetailActivity;
import com.bagus.purchasingapp_mtn.ui.user_profile.UserProfileActivity;
import com.bagus.purchasingapp_mtn.utils.Constants;
import com.bagus.purchasingapp_mtn.utils.Utils;

public class UserMainActivity extends BaseActivity<UserActivityMainBinding, UserMainViewModel> implements UserMainNavigator,
        NavigationView.OnNavigationItemSelectedListener {

    private UserActivityMainBinding binding;
    private UserMainViewModel viewModel;

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.user_activity_main;
    }

    @Override
    public UserMainViewModel getViewModel() {
        return viewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = getViewDataBinding();
        viewModel = ViewModelProviders.of(this, new UserMainViewModel.MainViewModelFactory(getApplicationContext())).get(UserMainViewModel.class);
        viewModel.setNavigator(this);
        setupNavigationView();


        binding.appbar.add.setOnClickListener(view -> {
            Intent intent = new Intent(UserMainActivity.this, UserReportAddActivity.class);
            startActivity(intent);
        });

        binding.appbar.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        binding.appbar.swipe.setOnRefreshListener(this::getData);
        
        setupStartService();
    }

    private void getData() {

        viewModel.getReport().observe(this, data -> {
            binding.appbar.recyclerView.setAdapter(new UserMainAdapter(this, data, viewModel));
        });

    }


    @SuppressLint("SetTextI18n")
    void setupNavigationView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getResources().getString(R.string.app_name));

        View headerView = LayoutInflater.from(this).inflate(R.layout.nav_header_main, binding.navView, false);
        binding.navView.addHeaderView(headerView);
        /*User Menu*/
        binding.navView.inflateMenu(R.menu.menu_nav_user);

        TextView navTitle = headerView.findViewById(R.id.nav_title);
        TextView navSubTitle = headerView.findViewById(R.id.nav_subtitle);

        navTitle.setText(Utils.getStringPreference(this, Constants.USER_NAME));
        navSubTitle.setText(Utils.getStringPreference(this, Constants.USER_POSITION));

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, binding.drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        binding.navView.setNavigationItemSelectedListener(UserMainActivity.this);
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        if (id == R.id.logout) {
            closeDrawer();
            tanyaLogout();
            return true;
        } else if (id == R.id.my_profile) {
            closeDrawer();
            startActivity(new Intent(this, UserProfileActivity.class));
            return true;
        }
        closeDrawer();
        return false;
    }

    private void closeDrawer() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    private void tanyaLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Logout?")
                .setMessage("Are you sure?")
                .setNegativeButton("CANCEL", (dialog, which) -> {
                    dialog.cancel();
                })
                .setPositiveButton("YES", ((dialog, which) -> {
                    dialog.cancel();
                    logout();
                }))
                .show();
    }

    private void logout() {
        Utils.putPreference(this, Constants.IS_LOGIN, false);
        startActivity(new Intent(this, SplashScreen.class));
        finish();
    }

    private void setupStartService() {
        Intent intent = new Intent(this, AppService.class);
        startService(intent);
    }

    @Override
    public void onItemClick(Report.Data data) {
        Intent intent = new Intent(this, UserReportDetailActivity.class);
        intent.putExtra("data", data);
        startActivity(intent);
    }

    @Override
    public void showLoading() {
        binding.appbar.swipe.setRefreshing(true);
    }

    @Override
    public void hideLoading() {
        binding.appbar.swipe.setRefreshing(false);
    }

    @Override
    public void onError(String message) {
        showAlertDialog("Failure", message);
        if (message.equals("Unauthorized")) {
            logout();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getData();
    }
}
