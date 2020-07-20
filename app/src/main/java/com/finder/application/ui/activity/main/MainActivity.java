package com.finder.application.ui.activity.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import com.finder.application.*;
import com.finder.application.dao.ItemDao;
import com.finder.application.model.Item;
import com.finder.application.model.ResponceLocation;
import com.finder.application.model.ResponceLocations;
import com.finder.application.responce.ResponceListener;
import com.finder.application.responce.ResponceGetLocations;
import com.finder.application.ui.activity.access_fine_location.ActivityAccess;
import com.finder.application.ui.activity.signin.SignInActivity;
import com.finder.application.ui.dialogs.DialogMarker;
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
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private GoogleMap googleMap;
    private float previousZoomLevel = -1.0f;
    private boolean isZooming = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        final App app = (App) getApplication();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        Intent intent = getIntent();
        App.is_continue = intent.getBooleanExtra("continue", false);
        boolean is_go_signin = false;
        App.UserInfo user_info = app.hasUser();

        if (!App.is_continue) {
            is_go_signin = true;
        }
        if (user_info.is_auth) {
            is_go_signin = false;
            System.out.println("set_view_user");
            set_view_user(navigationView.getHeaderView(0), user_info);
            System.out.println("set_view_user");
        } else {
            set_view_guest(navigationView.getHeaderView(0));
        }
        if (is_go_signin) {
            startActivity(new Intent(getApplicationContext(), SignInActivity.class));
        } else {
        }
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback());
        app.googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(connectionCallbacks())
                .addOnConnectionFailedListener(connectionFailedListener())
                .addApi(LocationServices.API)
                .build();


        if(App.IsOnline(getApplicationContext())) {
        } else {
            App.Toast(getApplicationContext(), "Error net");
        }

    }

    private void set_view_guest(View view) {
        final App app = (App) getApplication();
        ((TextView) view.findViewById(R.id.text_view_button)).setVisibility(View.VISIBLE);
        TextView text_view_user_name = (TextView) view.findViewById(R.id.text_view_user_name);
        TextView text_view_user_email = (TextView) view.findViewById(R.id.text_view_user_email);
        if (text_view_user_name != null) text_view_user_name.setVisibility(View.GONE);
        if (text_view_user_email != null) text_view_user_email.setVisibility(View.GONE);
        View view_header = view.findViewById(R.id.view_header);
        view_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.moveToSignIn();
            }
        });
    }

    private void set_view_user(View view, App.UserInfo user_info) {
        final App app = (App) getApplication();
        View view_header = view.findViewById(R.id.view_header);
        view_header.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                app.logout();
            }
        });
        ((TextView) view.findViewById(R.id.text_view_button)).setVisibility(View.GONE);
        ImageView image_view_user_avatar = (ImageView) view.findViewById(R.id.image_view_user_avatar);
        if(image_view_user_avatar != null) {
            Picasso.get()
                    .load(user_info.user_avatar)
                    .placeholder(R.drawable.user_profile_person)
                    .transform(new CropSquareTransformation())
                    .resize(64, 64)
                    .centerCrop().into(image_view_user_avatar);
        }
        TextView text_view_user_name = (TextView) view.findViewById(R.id.text_view_user_name);
        TextView text_view_user_email = (TextView) view.findViewById(R.id.text_view_user_email);
        if (text_view_user_name != null) {
            text_view_user_name.setVisibility(View.VISIBLE);
            text_view_user_name.setText(user_info.user_name);
        }
        if (text_view_user_email != null) {
            text_view_user_email.setVisibility(View.VISIBLE);
            text_view_user_email.setText(user_info.user_email);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.add_place:
                App.MoveToAddPlace(this);
                break;
            case R.id.my_place:
                App.MoveToMyPlace(this);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
                googleMap.setOnCameraChangeListener(cameraChangeListener());
                googleMap.setOnMarkerClickListener(markerClickListener());
                googleMap.setOnMapLongClickListener(markerLongClickListener());
            }
        };
    }

    private GoogleMap.OnMapLongClickListener markerLongClickListener() {
        return new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {

            }
        };
    }

    private GoogleMap.OnMarkerClickListener markerClickListener() {
        return new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker != null) {
                    long unic = (long) marker.getTag();
                    DialogMarker dialogMarker = new DialogMarker();
                    Bundle bundle = new Bundle();
                    bundle.putLong("unic", unic);
                    dialogMarker.setArguments(bundle);
                    dialogMarker.show(getSupportFragmentManager(), "dialogMarker");
                }
                return false;
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
        final MainActivity activity = this;
        return new GoogleApiClient.ConnectionCallbacks() {
            @Override
            public void onConnected(@Nullable Bundle bundle) {
                App.Has(activity, new App.OnHasListener() {
                    @Override
                    public void OnHasSucces() {
                        currentLocation();
                    }

                    @Override
                    public void OnHasError() {
                        App app = (App) activity.getApplication();
                        final App.UserInfo user_info = app.hasUser();
                        if(user_info.is_auth) {
                            startActivity(new Intent(getApplicationContext(), ActivityAccess.class));
                        }
                    }
                });
            }

            @Override
            public void onConnectionSuspended(int i) {

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

    public String getFullAddress(double latitude, double longitude) {
        System.out.println("__________________________________");
        Geocoder geocoder = new Geocoder(MainActivity.this);

        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);

            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                return address.getAddressLine(0);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("__________________________________");
        return "";
    }

    private void currentLocation() {
        App app = (App) getApplication();
        Location location = LocationServices.FusedLocationApi.getLastLocation(app.googleApiClient);
        if (location != null) {
            SharedPreferences sharedPreferences = getSharedPreferences("LOCATION_PREFERENCES", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString("latitude", location.getLatitude()+"");
            editor.putString("longitude", location.getLongitude()+"");
            editor.apply();

            if(App.IsOnline(getApplicationContext())) {
                App.UserInfo user_info = app.hasUser();
                String user_id = "";
                String user_email = "";
                String user_token = "";
                if(user_info.is_auth) {
                    user_id = user_info.user_id;
                    user_email = user_info.user_email;
                    user_token = user_info.user_token;
                }
                final String finalUser_id = user_id;
                new ResponceGetLocations(
                        location.getLatitude()+"",
                        location.getLongitude()+"",
//                        String.valueOf(location.getLatitude()),
//                        String.valueOf(location.getLongitude()),
                        user_email,
                        user_token,
                        new ResponceListener() {
                    @Override
                    public void OnSuccess(String responce) {
                        if(!responce.equals("")) {
                            try {
                                Gson gson = new Gson();
                                ResponceLocations responceLocations = gson.fromJson(responce, ResponceLocations.class);
                                if(responceLocations != null) {
                                    new SaveLocations(responceLocations.locations, finalUser_id, new UpdateLocationListener() {

                                        @Override
                                        public void OnUpdateLocations(List<Item> items) {
                                            updateMarkers(items);
                                        }
                                    }).execute();
                                }
                                System.out.println(responceLocations);
                                System.out.println(responceLocations);
                                System.out.println(responceLocations.distance);
                                System.out.println(responceLocations.latitude);
                                System.out.println(responceLocations.longitude);
                                System.out.println(responceLocations.locations.size());
                                System.out.println(responceLocations.locations);
                            } catch (Exception e) {
                                App.Toast(getApplicationContext(), e.getMessage());
                                App.Toast(getApplicationContext(), responce);
                            }
                        }
                        System.out.println("responce " + responce);
                        System.out.println("responce get " + responce);
                        System.out.println("responce " + responce);
                    }
                }).execute();
            }
            String address = getFullAddress(location.getLatitude(), location.getLongitude());
            if(address.equals("")) {
                address = "I";
            }
            move(address, location.getLatitude(), location.getLongitude());
        }

        AppDatabase database = App.database;
        final ItemDao itemDao = database.itemDao();
        new AsyncTask<Void, Void, List<Item>>() {

            @Override
            protected List<Item> doInBackground(Void... voids) {
                return itemDao.getAll();
            }

            @Override
            protected void onPostExecute(List<Item> items) {
                super.onPostExecute(items);
                updateMarkers(items);
            }
        }.execute();
    }

    private void updateMarkers(List<Item> items) {
        if (items != null) {
            if(items.size() > 0) {
                googleMap.clear();
                for (Item item : items) {
                    Marker marker = googleMap.addMarker(new MarkerOptions()
                            .position(new LatLng(item.latitude, item.longitude))
                            .title(item.title));
                    marker.setTag(item.unic);
                }
            }
        }
    }

    private void move(String title, double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        Marker marker = googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(title));
        marker.setFlat(true);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }
}

