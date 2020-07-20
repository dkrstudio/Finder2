package com.finder.application.ui.activity.add_place;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import com.finder.application.App;
import com.finder.application.AppDatabase;
import com.finder.application.R;
import com.finder.application.SaveListener;
import com.finder.application.dao.ItemDao;
import com.finder.application.model.Item;
import com.finder.application.ui.activity.main.MainActivity;
import com.finder.application.ui.activity.my_place.MyPlaceActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class AddPlaceActivity extends AppCompatActivity implements SaveListener {

    private GoogleMap googleMap;
    private float previousZoomLevel = -1.0f;
    private boolean isZooming = false;
    private double longitude;
    private double latitude;

    private EditText edit_text_title;
    private EditText edit_text_description;
    private EditText edit_text_location;
    private CheckBox view_is_public;
    private ImageView view_button_show_list;
    private Marker marker;
    private String location = "";
    private long unic = 0;
    private boolean BackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addplace_activity);
        final Intent intent = getIntent();
        unic = intent.getLongExtra("unic", 0);

        final App app = (App) getApplication();

        findViewById(R.id.view_button_voice_title).setOnClickListener(voice(App.FORM_VOICE_TITLE));
        findViewById(R.id.view_button_voice_description).setOnClickListener(voice(App.FORM_VOICE_DESCRIPTION));
        findViewById(R.id.view_button_voice_location).setOnClickListener(voice(App.FORM_VOICE_LOCATION));
        findViewById(R.id.view_button_find_location).setOnClickListener(find_location());
        edit_text_title = (EditText) findViewById(R.id.edit_text_title);
        edit_text_description = (EditText) findViewById(R.id.edit_text_description);
        edit_text_location = (EditText) findViewById(R.id.edit_text_location);
        view_is_public = (CheckBox) findViewById(R.id.view_is_public);
        edit_text_location.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                view_is_public.setChecked(false);
                if (view_button_show_list.getVisibility() == View.VISIBLE)
                    view_button_show_list.setVisibility(View.GONE);
            }
        });
        view_button_show_list = (ImageView) findViewById(R.id.view_button_show_list);
        view_button_show_list.setVisibility(View.GONE);
        view_button_show_list.setOnClickListener(show_list());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback());
        app.googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(connectionCallbacks())
                .addOnConnectionFailedListener(connectionFailedListener())
                .addApi(LocationServices.API)
                .build();

        if (unic != 0) {
            AppDatabase database = App.database;
            final ItemDao itemDao = database.itemDao();
            new AsyncTask<Long, Void, Item>() {

                @Override
                protected Item doInBackground(Long... longs) {
                    return itemDao.get(longs[0]);
                }

                @Override
                protected void onPostExecute(Item item) {
                    super.onPostExecute(item);
                    if (item != null) {
                        edit_text_title.setText(item.title);
                        edit_text_description.setText(item.description);
                        edit_text_location.setText(item.location);
                        latitude = item.latitude;
                        longitude = item.longitude;
                        location = item.location;
                        view_is_public.setChecked(item.is_public == 1);

                    }
                }
            }.execute(unic);
        }
        App.UserInfo user_info = app.hasUser();
        if (!user_info.is_auth) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (unic != 0) {
            getMenuInflater().inflate(R.menu.editplace_menu, menu);
        } else {
            getMenuInflater().inflate(R.menu.addplace_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                BackPressed = false;
                save();
                break;
            case R.id.action_remove:
                remove();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void save() {
        final String title = edit_text_title.getText().toString();
        final String description = edit_text_description.getText().toString();
        final String location = edit_text_location.getText().toString();
        final int is_public = ((CheckBox) findViewById(R.id.view_is_public)).isChecked() ? 1 : 0;
        App app = (App) getApplication();
        final App.UserInfo user_info = app.hasUser();
        if (user_info.is_auth) {
            if (unic != 0) {
                App.Update(user_info, unic, title, description, location, latitude, longitude, is_public, this);
            } else {
                App.Insert(user_info, title, description, location, latitude, longitude, is_public, this);
            }
        }
        else OnError(-1);
    }

    private void remove() {
        if (unic == 0) return;
    }

    @Override
    public void onBackPressed() {
        BackPressed = true;
        save();
    }

    public void getFullAddress() {
        System.out.println("__________________________________");
        Geocoder geocoder = new Geocoder(AddPlaceActivity.this);

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                edit_text_location.setText(address.getAddressLine(0));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("__________________________________");
    }

    private View.OnClickListener find_location() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view_button_show_list.setVisibility(View.GONE);
                String location = edit_text_location.getText().toString();
                try {
                    if (location.length() < 5) {
                        throw new Exception(getString(R.string.set_address));
                    }
                    Geocoder gc = new Geocoder(AddPlaceActivity.this);
                    if (!gc.isPresent()) {
                        throw new Exception(getString(R.string.not_results));
                    }
                    App.addressList = gc.getFromLocationName(location, 5);

                    if (App.addressList.size() == 0) {
                        throw new Exception(getString(R.string.not_results));
                    }
                    Address address = App.addressList.get(0);
                    latitude = address.getLatitude();
                    longitude = address.getLongitude();
                    location = address.getAddressLine(0);
                    move(location);

                    if (App.addressList.size() > 1) {
                        view_button_show_list.setVisibility(View.VISIBLE);
                    }
                } catch (Exception exception) {
                    App.Toast(getApplicationContext(), exception.getMessage());
                    exception.printStackTrace();
                }
            }
        };
    }

    private View.OnClickListener show_list() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (App.addressList != null && App.addressList.size() > 0) {
                    show_address_list();
                }
            }
        };
    }

    private void show_address_list() {
        AlertDialog.Builder builderSingle = new AlertDialog.Builder(AddPlaceActivity.this);
        builderSingle.setIcon(R.drawable.baseline_map_black_18dp);
        builderSingle.setTitle(getString(R.string.select_location));

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(AddPlaceActivity.this, android.R.layout.select_dialog_singlechoice);
        for (Address address : App.addressList) {
            arrayAdapter.add(address.getAddressLine(0));
        }


        builderSingle.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (which < App.addressList.size()) {
                    Address address = App.addressList.get(which);
                }
            }
        });
        builderSingle.show();
    }

    private View.OnClickListener voice(final int code) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                        RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                startActivityForResult(intent, code);
            }
        };
    }

    private void currentLocation() {
        App app = (App) getApplication();
        Location location = LocationServices.FusedLocationApi.getLastLocation(app.googleApiClient);
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            getFullAddress();
            move(location.toString());
        }
    }

    private void move(String title) {
        googleMap.clear();
        LatLng latLng = new LatLng(latitude, longitude);
        marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(title));
        marker.setFlat(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    private void newMarker(LatLng latLng, String title) {
        googleMap.clear();
        longitude = latLng.longitude;
        latitude = latLng.latitude;
        getFullAddress();
        location = title;
        marker = googleMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(title));
        marker.setFlat(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        App.VoiceResult(this, requestCode, resultCode, data, App.FORM_VOICE_TITLE, new App.VoiceListener() {
            @Override
            public void OnSuccess(String text) {
                edit_text_title.setText(text);
            }
        });
        App.VoiceResult(this, requestCode, resultCode, data, App.FORM_VOICE_DESCRIPTION, new App.VoiceListener() {
            @Override
            public void OnSuccess(String text) {
                edit_text_description.setText(text);
            }
        });
        App.VoiceResult(this, requestCode, resultCode, data, App.FORM_VOICE_LOCATION, new App.VoiceListener() {
            @Override
            public void OnSuccess(String text) {
                view_button_show_list.setVisibility(View.GONE);
                edit_text_location.setText(text);
            }
        });
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == App.LOCATION_PERMISSION_REQUEST_CODE) {
            currentLocation();
        }
    }

    @Override
    public void onStart() {
        App app = (App) getApplication();
        app.googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        App app = (App) getApplication();
        app.googleApiClient.disconnect();
        super.onStop();
    }


    private OnMapReadyCallback mapReadyCallback() {
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap gMap) {
                googleMap = gMap;
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                googleMap.setOnMarkerDragListener(markerDragListener());
                googleMap.setOnMapLongClickListener(mapLongClickListener());
                googleMap.setOnCameraChangeListener(cameraChangeListener());
            }
        };
    }

    private GoogleApiClient.OnConnectionFailedListener connectionFailedListener() {
        return new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

            }
        };
    }

    private GoogleApiClient.ConnectionCallbacks connectionCallbacks() {
        return new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    if (unic != 0 && !location.equals("")) {//TODO
                        move(location);
                    } else {
                        currentLocation();
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, App.LOCATION_PERMISSION_REQUEST_CODE);
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        };
    }

    private GoogleMap.OnMapLongClickListener mapLongClickListener() {
        return new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                newMarker(latLng, "1");
            }
        };
    }

    private GoogleMap.OnMarkerDragListener markerDragListener() {
        return new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                latitude = marker.getPosition().latitude;
                longitude = marker.getPosition().longitude;
                move("1");//TODO
            }
        };
    }

    private GoogleMap.OnCameraChangeListener cameraChangeListener() {
        return new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (previousZoomLevel != cameraPosition.zoom) {
                    isZooming = true;
                }

                previousZoomLevel = cameraPosition.zoom;
            }
        };
    }


    @Override
    public void OnSave(Long unic) {
        App.Toast(getApplicationContext(), "Save " + unic);
        startActivity(new Intent(getApplicationContext(), MyPlaceActivity.class));
    }

    @Override
    public void OnError(int i) {
        App.Toast(getApplicationContext(), "OnError " + BackPressed);
        if(!BackPressed) {
            startActivity(new Intent(getApplicationContext(), MyPlaceActivity.class));
        }
    }
}
