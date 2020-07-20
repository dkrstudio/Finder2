package com.finder.application.action;

import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.navigation.fragment.NavHostFragment;
import com.finder.application.App;
import com.finder.application.R;
import com.finder.application.ui.activity.ui.main.ActivityMain;

public class ClickToLogoutUser implements View.OnClickListener {
    private Fragment fragment;

    public ClickToLogoutUser(Fragment fragment) {
        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {
        App.Toast(fragment.getContext(), "logout");
    }
}
