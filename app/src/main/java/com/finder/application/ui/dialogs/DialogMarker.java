package com.finder.application.ui.dialogs;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.finder.application.App;
import com.finder.application.AppDatabase;
import com.finder.application.CropSquareTransformation;
import com.finder.application.R;
import com.finder.application.dao.ItemDao;
import com.finder.application.model.Item;
import com.finder.application.ui.activity.main.MainActivity;
import com.squareup.picasso.Picasso;

public class DialogMarker extends DialogFragment {


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle("Dialog marker");
        View v = inflater.inflate(R.layout.dialog_marker, null);
        final TextView text_view_user_name = v.findViewById(R.id.text_view_user_name);
        final TextView text_view_description = v.findViewById(R.id.text_view_description);
        final ImageView image_view_user_avatar = v.findViewById(R.id.image_view_user_avatar);
        final Button view_button_go = v.findViewById(R.id.view_button_go);
        Bundle bundle = getArguments();
        if(bundle != null) {
            long unic = bundle.getLong("unic");
            new AsyncTask<Long, Void, Item>() {

                @Override
                protected Item doInBackground(Long... longs) {
                    AppDatabase database = App.database;
                    ItemDao itemDao = database.itemDao();
                    return itemDao.get(longs[0]);
                }

                @Override
                protected void onPostExecute(Item item) {
                    super.onPostExecute(item);
                    if(item != null) {
                        getDialog().setTitle(item.title);
                        text_view_description.setText(item.description);
                        text_view_user_name.setText(item.author_name);
                        if(image_view_user_avatar != null) {
                            Picasso.get()
                                    .load(item.author_avatar)
                                    .placeholder(R.drawable.user_profile_person)
                                    .transform(new CropSquareTransformation())
                                    .resize(64, 64)
                                    .centerCrop().into(image_view_user_avatar);
                        }
                        view_button_go.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                            }
                        });
                    }
                }
            }.execute(unic);
        };
//        v.findViewById(R.id.button_close).setOnClickListener(this);
        return v;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
    }
}
