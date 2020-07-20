package com.finder.application.ui.activity.ui.adapters;

import android.location.Address;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.finder.application.ClickToAddress;
import com.finder.application.R;
import com.finder.application.ui.activity.ui.adapters.holders.HolderAddress;
import com.finder.application.ui.activity.ui.main.form.FormFragment;

import java.util.List;

public class AdapterAddress extends RecyclerView.Adapter<HolderAddress> {

    public List<Address> addressList;
    private ClickToAddress clickToAddress;

    public AdapterAddress(ClickToAddress clickToAddress) {
        this.clickToAddress = clickToAddress;
    }

    @NonNull
    @Override
    public HolderAddress onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        HolderAddress addressHolder = new HolderAddress(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_view_address, parent, false), clickToAddress);
        return addressHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull HolderAddress holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        if (addressList == null) return 0;
        return addressList.size();
    }

    public void update(List<Address> addressList) {
        this.addressList = addressList;
        notifyDataSetChanged();
    }
}