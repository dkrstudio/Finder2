package com.finder.application.ui.activity.ui.adapters.holders;

import android.content.Context;
import android.location.Address;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.finder.application.App;
import com.finder.application.ClickToAddress;
import com.finder.application.R;
import com.finder.application.ui.activity.ui.main.form.FormFragment;

public class HolderAddress extends RecyclerView.ViewHolder {
    private View address_view;
    private TextView view_title;
    public HolderAddress(@NonNull View itemView, final ClickToAddress clickToAddress) {
        super(itemView);
        address_view = itemView.findViewById(R.id.address_view);
        address_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag() != null) {
                    Address address = App.addressList.get((Integer) v.getTag());
                    if(address != null && clickToAddress != null) {
                        clickToAddress.OnClickToAddress(address);
                    }
                }
            }
        });
        view_title = itemView.findViewById(R.id.view_title);
    }

    public void bind(int position) {
        Address address = App.addressList.get(position);
        address_view.setTag(position);
        if(view_title != null) {
            view_title.setText(address.getAddressLine(0));
        }
    }
}