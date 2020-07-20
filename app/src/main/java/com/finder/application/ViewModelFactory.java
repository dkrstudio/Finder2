package com.finder.application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import com.finder.application.ui.activity.ui.main.form.FormViewModel;

public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final long id;

    public ViewModelFactory(long id) {
        super();
        this.id = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return null;
    }
}
