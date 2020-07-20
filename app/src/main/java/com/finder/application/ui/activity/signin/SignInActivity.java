package com.finder.application.ui.activity.signin;

import android.content.Intent;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentActivity;
import com.finder.application.App;
import com.finder.application.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SignInActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signin_activity);

        final App app = (App) getApplication();
        final FragmentActivity activity = this;

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        app.googleSignInClient = GoogleSignIn.getClient(this, gso);

        if(app.googleSignInClient != null) {
            app.silentSignIn(activity);
        }

        findViewById(R.id.view_button_continue).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.MoveToContinue();
            }
        });
        SignInButton signInButton = findViewById(R.id.view_button_sign_in);
        signInButton.setSize(SignInButton.SIZE_ICON_ONLY);
        signInButton.setColorScheme(SignInButton.COLOR_DARK);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.ClickToSignIn(activity);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == App.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            final App app = (App) getApplication();
            app.HandleSignInResult(task);
        }
    }
}
