package com.finder.application;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.room.Room;
import com.finder.application.dao.ItemDao;
import com.finder.application.model.Item;
import com.finder.application.model.LoginResponce;
import com.finder.application.responce.HttpPostClient;
import com.finder.application.responce.ResponceAdd;
import com.finder.application.responce.ResponceListener;
import com.finder.application.responce.ResponceLogin;
import com.finder.application.ui.activity.add_place.AddPlaceActivity;
import com.finder.application.ui.activity.main.MainActivity;
import com.finder.application.ui.activity.my_place.MyPlaceActivity;
import com.finder.application.ui.activity.signin.SignInActivity;
import com.finder.application.ui.activity.ui.main.form.FormFragment;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;
import okhttp3.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class App extends Application {
    public static final int RC_SIGN_IN = 999;
    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1000;
    public static final int FORM_VOICE_TITLE = 1001;
    public static final int FORM_VOICE_DESCRIPTION = 1002;
    public static final int FORM_VOICE_LOCATION = 1003;

    public static AppDatabase database;
    public static List<Address> addressList;
    public static boolean is_continue;
    private static boolean debug = true;

    public GoogleSignInClient googleSignInClient;

    public static List<Item> items = new ArrayList<>();
    public GoogleApiClient googleApiClient;

    public static void Toast(Context context, String string) {
        if (debug) {
            Toast.makeText(context, string, Toast.LENGTH_SHORT).show();
        }
    }

    public static void Update(final App.UserInfo user_info,
                              final long unic,
                              final String title,
                              final String description,
                              final String location,
                              final double latitude,
                              final double longitude,
                              final int is_public,
                              final FragmentActivity activity) {
        AppDatabase appDatabase = App.database;
        final ItemDao itemDao = appDatabase.itemDao();
        new AsyncTask<Long, Void, Long>() {

            int error_code = 0;

            @Override
            protected Long doInBackground(Long... unics) {
                Item item = itemDao.get(unics[0]);
                if (item == null) {
                    error_code = 1;
                    return null;
                }
                if (title.equals("")) {
                    error_code = 2;
                    return null;
                }
                item.title = title;
//                    if(description.equals("")) {
//                        error_code = 3;
//                        return null;
//                    }
                item.description = description;
                if (location.equals("")) {
                    error_code = 4;
                    return null;
                }
                item.location = location;
                if (latitude == 0) {
                    error_code = 5;
                    return null;
                }
                item.latitude = latitude;
                if (longitude == 0) {
                    error_code = 6;
                    return null;
                }
                item.longitude = longitude;
                item.is_public = is_public;
                itemDao.Update(item);
                if(App.IsOnline(activity.getApplicationContext())) {
                    item = itemDao.get(unic);
                    ResponceAdd.Send(user_info.user_id,
                            user_info.user_name,
                            user_info.user_avatar,
                            item.unic+"",
                            item.title,
                            item.description,
                            item.location,
                            item.is_public+"",
                            item.latitude+"",
                            item.longitude+"");
                }
                return item.unic;
            }

            @Override
            protected void onPostExecute(Long aLong) {
                super.onPostExecute(aLong);
                if(activity instanceof SaveListener) {
                    SaveListener listener = (SaveListener) activity;
                    if (error_code == 0) listener.OnSave(aLong);
                    else listener.OnError(error_code);
                }
            }
        }.execute(unic);
    }

    public static void Insert(final App.UserInfo user_info,
                              final String title,
                              final String description,
                              final String location,
                              final double latitude,
                              final double longitude,
                              final int is_public,
                              final FragmentActivity activity) {
        AppDatabase appDatabase = App.database;
        final ItemDao itemDao = appDatabase.itemDao();
        new AsyncTask<Void, Void, Item>() {

            int error_code = 0;

            @Override
            protected Item doInBackground(Void... voids) {
                Item item = new Item();
                item.unic = Calendar.getInstance().getTimeInMillis();
                if (title.equals("")) {
                    error_code = 2;
                    return null;
                }
                item.title = title;
                item.description = description;
                if (location.equals("")) {
                    error_code = 4;
                    return null;
                }
                item.location = location;
                if (latitude == 0) {
                    error_code = 5;
                    return null;
                }
                item.latitude = latitude;
                if (longitude == 0) {
                    error_code = 6;
                    return null;
                }
                item.longitude = longitude;
                item.is_public = is_public;
                item.author_id = user_info.user_id;
                item.author_name = user_info.user_name;
                item.author_avatar = user_info.user_avatar;
                long unic = itemDao.InsertId(item);

                if(App.IsOnline(activity.getApplicationContext())) {
                    item = itemDao.get(unic);
                    ResponceAdd.Send(user_info.user_id,
                            user_info.user_name,
                            user_info.user_avatar,
                            item.unic+"",
                            item.title,
                            item.description,
                            item.location,
                            item.is_public+"",
                            item.latitude+"",
                            item.longitude+"");
                }
                return item;
            }

            @Override
            protected void onPostExecute(Item item) {
                super.onPostExecute(item);
                if(activity instanceof SaveListener) {
                    Toast.makeText(activity.getApplicationContext(), "2is " + item.is_public, Toast.LENGTH_SHORT).show();
                    SaveListener listener = (SaveListener) activity;
                    if (error_code == 0) listener.OnSave(item.unic);
                    else listener.OnError(error_code);
                }
            }
        }.execute();
    }

    public static boolean IsOnline(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }

    public static void RunUpdate(FragmentActivity activity, final Item item) {

        App app = (App) activity.getApplication();
        final App.UserInfo user_info = app.hasUser();
        if(user_info.is_auth) {
            AppDatabase database = App.database;
            final ItemDao itemDao = database.itemDao();
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {
                    ResponceAdd.Send(user_info.user_id,
                            user_info.user_name,
                            user_info.user_avatar,
                            item.unic+"",
                            item.title,
                            item.description,
                            item.location,
                            item.is_public+"",
                            item.latitude+"",
                            item.longitude+"");
                    itemDao.Update(item);
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                }
            }.execute();
        }
    }

    public static void UnPublic(final FragmentActivity activity, final Item item) {
        App app = (App) activity.getApplication();
        final App.UserInfo user_info = app.hasUser();
        if(user_info.is_auth) {
            AppDatabase database = App.database;
            final ItemDao itemDao = database.itemDao();
            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... voids) {

                    HashMap<String, String> data = new HashMap<>();
                    data.put("user_id", user_info.user_id);
                    data.put("unic", item.unic+"");
                    data.put("is_public", item.is_public+"");

                    HttpPostClient httpPostClient = new HttpPostClient("http://kurum.ru/public/");
                    for (String key : data.keySet()) {
                        httpPostClient.add(key, data.get(key));
                    }
                    try {
                        String result = httpPostClient.execute();
                        System.out.println("result UnPublic " + result);
                        System.out.println(result);
                        System.out.println("result add update " + result);
                        itemDao.Update(item);
                        return null;
                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    App.Toast(activity.getApplicationContext(), "UnPublic " + item.is_public);
                }
            }.execute();
        }
    }

    public static void Remove(final Item item) {
        AppDatabase appDatabase = App.database;
        final ItemDao itemDao = appDatabase.itemDao();
        new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                itemDao.Delete(item);
                return null;
            }
        }.execute();
    }

    public static interface OnHasListener {
        void OnHasSucces();
        void OnHasError();
    }

    public static void Has(FragmentActivity activity, OnHasListener listener) {
        if (ContextCompat.checkSelfPermission(activity.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            listener.OnHasSucces();
        } else {
            listener.OnHasError();
        }
    }

    public void Toast(String string) {
        if (debug) {
            Toast.makeText(getApplicationContext(), string, Toast.LENGTH_SHORT).show();
        }
    }

    public static void Log(Object value) {
        if (debug) System.out.println(value);
    }

    public static void Voice(Fragment fragment, int code) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        fragment.startActivityForResult(intent, code);
    }

    public static View.OnClickListener ClickToButtonVoice(final FormFragment fragment, final int code) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Voice(fragment, code);
            }
        };
    }

    public static void VoiceResult(FormFragment fragment, int requestCode, int resultCode, Intent data, int code, VoiceListener voiceListener) {
        if (requestCode == code && resultCode == FragmentActivity.RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (results != null) {
                if (results.size() > 0) {
                    String spokenText = results.get(0);
                    if (spokenText != null) voiceListener.OnSuccess(spokenText);
                }
            }
        }
    }

    public static void VoiceResult(FragmentActivity activity, int requestCode, int resultCode, Intent data, int code, VoiceListener voiceListener) {
        if (requestCode == code && resultCode == FragmentActivity.RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(
                    RecognizerIntent.EXTRA_RESULTS);
            if (results != null) {
                if (results.size() > 0) {
                    String spokenText = results.get(0);
                    if (spokenText != null) voiceListener.OnSuccess(spokenText);
                }
            }
        }
    }

    public UserInfo hasUser() {
        UserInfo userInfo = new UserInfo();
        SharedPreferences sharedPreferences = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        if (sharedPreferences.contains("USER_ID")) {
            userInfo.is_auth = true;
            userInfo.user_id = sharedPreferences.getString("USER_ID", "");
            userInfo.user_token = sharedPreferences.getString("USER_TOKEN", "");
            userInfo.user_name = sharedPreferences.getString("USER_NAME", "");
            userInfo.user_email = sharedPreferences.getString("USER_EMAIL", "");
            userInfo.user_avatar = sharedPreferences.getString("USER_AVATAR", "");
        }
        return userInfo;
    }

    public void logout() {
        SharedPreferences sharedPreferences = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public static void MoveToAddPlace(FragmentActivity activity) {
        Intent intent = new Intent(activity.getApplicationContext(), AddPlaceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public static void MoveToMyPlace(FragmentActivity activity) {
        Intent intent = new Intent(activity.getApplicationContext(), MyPlaceActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
    }

    public class UserInfo {
        public boolean is_auth = false;
        public String user_id = "";
        public String user_token = "";
        public String user_name = "";
        public String user_email = "";
        public String user_avatar = "";
    }

    public void HandleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                SharedPreferences sharedPreferences = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("USER_ID", account.getId());
                editor.putString("USER_TOKEN", account.getIdToken());
                editor.putString("USER_NAME", account.getDisplayName());
                editor.putString("USER_EMAIL", account.getEmail());
                editor.putString("USER_AVATAR", account.getPhotoUrl().toString());
                editor.apply();
//                send_user_data_to_server(account);
            }
            signOut();
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (ApiException e) {
            App.Toast(getApplicationContext(), "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void send_user_data_to_server(GoogleSignInAccount account) {
        SharedPreferences sharedPreferences = getSharedPreferences("LOCATION_PREFERENCES", Context.MODE_PRIVATE);
        String latitude = sharedPreferences.getString("latitude", "");
        String longitude = sharedPreferences.getString("longitude", "");
        new ResponceLogin(
                latitude, longitude, account.getEmail(), account.getIdToken(), account.getDisplayName(), account.getPhotoUrl().toString(),
                new ResponceListener() {

                    @Override
                    public void OnSuccess(String responce) {
                        if(!responce.equals("")) {
                            Gson gson = new Gson();
                            LoginResponce loginResponce = gson.fromJson(responce, LoginResponce.class);
                            if(!loginResponce.code.equals("") && loginResponce.user != null) {
                                Toast.makeText(getApplicationContext(), "error " + loginResponce.error + " " + loginResponce.code, Toast.LENGTH_SHORT).show();
                            } else {
                                SharedPreferences sharedPreferences = getSharedPreferences("USER_PREFERENCES", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                editor.putString("USER_ID", loginResponce.user.id);
                                editor.apply();
                            }
                        }
                    }
                }).execute();
    }


    private void signOut() {
        if (googleSignInClient == null) {
            Toast("googleSignInClient == null");
        }
        googleSignInClient.signOut();
    }

    public void ClickToSignIn(FragmentActivity activity) {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        activity.startActivityForResult(signInIntent, App.RC_SIGN_IN);
    }

    public void MoveToContinue() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("continue", true);
        startActivity(intent);
    }

    public void moveToSignIn() {
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void silentSignIn(FragmentActivity activity) {
        googleSignInClient.silentSignIn()
                .addOnCompleteListener(
                        activity,
                        new OnCompleteListener<GoogleSignInAccount>() {
                            @Override
                            public void onComplete(@NonNull Task<GoogleSignInAccount> task) {
//                                HandleSignInResult(task);
                                signOut();
                            }
                        });
    }

    public interface VoiceListener {
        void OnSuccess(String text);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        database = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "database").build();
    }
}
