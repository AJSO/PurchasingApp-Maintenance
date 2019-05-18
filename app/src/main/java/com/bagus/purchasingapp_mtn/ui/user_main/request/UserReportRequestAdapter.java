package com.bagus.purchasingapp_mtn.ui.user_main.request;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.bagus.purchasingapp_mtn.R;
import com.bagus.purchasingapp_mtn.databinding.UserItemRequestBinding;
import com.bagus.purchasingapp_mtn.model.PurchaseItem;

import java.util.List;

public class UserReportRequestAdapter extends RecyclerView.Adapter<UserReportRequestAdapter.MyViewHolder> {

    private Context context;
    private List<PurchaseItem> items;
    private UserReportRequestViewModel viewModel;

    public UserReportRequestAdapter(Context context, List<PurchaseItem> items, UserReportRequestViewModel viewModel) {
        this.context = context;
        this.items = items;
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        LayoutInflater inflater = LayoutInflater.from(context);
        UserItemRequestBinding binding = DataBindingUtil.inflate(inflater, R.layout.user_item_request, viewGroup, false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        PurchaseItem item = items.get(i);
        myViewHolder.bind(item, viewModel);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public UserItemRequestBinding binding;

        public MyViewHolder(UserItemRequestBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(PurchaseItem item, UserReportRequestViewModel viewModel) {
            binding.setItem(item);
            binding.setViewModel(viewModel);
            binding.setPosition(getAdapterPosition());
            int numberItem = getAdapterPosition() + 1;
            binding.number.setText(String.valueOf(numberItem) + ".");
            binding.executePendingBindings();
        }
    }
}

