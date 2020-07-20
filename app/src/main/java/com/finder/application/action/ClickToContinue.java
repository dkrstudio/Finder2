package com.finder.application.action;

import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.finder.application.R;
import com.finder.application.ui.activity.ui.main.form.FormFragment;
import com.finder.application.ui.fragment.sign_in.SignInFragment;

public class ClickToContinue implements View.OnClickListener {
    private Fragment fragment;

    public ClickToContinue(Fragment fragment) {

        this.fragment = fragment;
    }

    @Override
    public void onClick(View v) {

        NavHostFragment.findNavController(fragment)
                .navigate(R.id.action_SignInFragment_to_HomeFragment);
    }
}
