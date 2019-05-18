package com.bagus.purchasingapp_mtn.ui.user_main;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.api.AppServerConfig;
import com.bagus.purchasingapp_mtn.databinding.UserItemMainBinding;
import com.bagus.purchasingapp_mtn.model.Report;
import com.bumptech.glide.Glide;

import java.util.List;

public class UserMainAdapter extends RecyclerView.Adapter<UserMainAdapter.MyViewHolder> {

    private Context context;
    private List<Report.Data> items;
    private UserMainViewModel viewModel;

    public UserMainAdapter(Context context, List<Report.Data> items, UserMainViewModel viewModel) {
        this.context = context;
        this.items = items;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        UserItemMainBinding binding = DataBindingUtil.inflate(inflater, R.layout.user_item_main, viewGroup, false);
        return new MyViewHolder(binding);   
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Report.Data item = items.get(i);
        myViewHolder.bind(item, viewModel);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public UserItemMainBinding binding;

        public MyViewHolder(UserItemMainBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(Report.Data item, UserMainViewModel viewModel) {
            binding.setItem(item);
            Glide.with(context).asBitmap().load(AppServerConfig.IMAGE_URL + item.reportPhoto).into(binding.image);
            binding.setViewModel(viewModel);
            binding.executePendingBindings();
        }
    }

}
