package com.finder.application.ui.activity.ui.main.form;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.finder.application.App;
import com.finder.application.ClickToAddress;
import com.finder.application.R;
import com.finder.application.ViewModelFactory;
import com.finder.application.model.Item;
import com.finder.application.ui.activity.ui.adapters.AdapterAddress;
import com.finder.application.ui.activity.ui.main.home.HomeFragment;
import com.finder.application.ui.activity.ui.main.home.HomeFragmentDirections;
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

import java.util.Calendar;
import java.util.List;

public class FormFragment extends Fragment implements ClickToAddress {

    private FormViewModel formViewModel;
//    TextView text_view_location;
    private EditText edit_text_title, edit_text_description, edit_text_location;
//    private RadioButton current_location, select_location, find_address;
    private View view_select_location, view_find_address;


    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private double longitude;
    private double latitude;
    private String location;
    private float previousZoomLevel = -1.0f;
    private boolean isZooming = false;
    private AdapterAddress adapterAddress;
    private Marker marker;
    private boolean is_edit;
    private Menu menu;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        formViewModel =
                ViewModelProviders.of(this).get(FormViewModel.class);
        View root = inflater.inflate(R.layout.fragment_form, container, false);
//        text_view_location = (TextView) root.findViewById(R.id.text_view_location);
        edit_text_title = (EditText) root.findViewById(R.id.edit_text_title);
        edit_text_description = (EditText) root.findViewById(R.id.edit_text_description);
        edit_text_location = (EditText) root.findViewById(R.id.edit_text_location);
//        view_select_location = (View) root.findViewById(R.id.view_select_location);
//        view_find_address = (View) root.findViewById(R.id.view_find_address);

//        current_location = (RadioButton) root.findViewById(R.id.current_location);
//        current_location.setTag(0);
//        select_location = (RadioButton) root.findViewById(R.id.select_location);
//        select_location.setTag(1);
//        find_address = (RadioButton) root.findViewById(R.id.find_address);
//        find_address.setTag(2);


        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recycler_view_address);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapterAddress = new AdapterAddress(this);
        recyclerView.setAdapter(adapterAddress);


        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(mapReadyCallback());
        googleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(connectionCallbacks())
                .addOnConnectionFailedListener(connectionFailedListener())
                .addApi(LocationServices.API)
                .build();


        Bundle bundle = getArguments();
        long id = 0;
        if (bundle != null) {
            id = bundle.getLong("id", 0);
        }
        if (id != 0) {
            formViewModel.putItem(id);
        } else {
//            text_view_location.setText("non");
//            current_location.setChecked(true);
        }

        formViewModel.dataItem.observe(getViewLifecycleOwner(), new Observer<Item>() {
            @Override
            public void onChanged(Item item) {
                edit_text_title.setText(item.title);
                edit_text_description.setText(item.description);
                longitude = item.longitude;
                latitude = item.latitude;
                location = location;
                is_edit = true;
                move(item.location);
            }
        });
        edit_text_location.addTextChangedListener(textChangedListener());
        return root;
    }

    private TextWatcher textChangedListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.toString().length() > 5) {
                    findLocation(s.toString());
                }
            }
        };
    }

    private void findLocation(String address) {
        try {
            Geocoder gc = new Geocoder(getContext());
            if(gc.isPresent()) {
                App.addressList = gc.getFromLocationName(address, 5);
                adapterAddress.update(App.addressList);
            } else {
                App.Toast(getContext(), "!Geocoder.isPresent()");
            }
        } catch (Exception exception) {
            App.Toast(getContext(), exception.getMessage());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
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
                edit_text_location.setText(text);
            }
        });
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == App.LOCATION_PERMISSION_REQUEST_CODE) {
            if(is_edit) {
                move(location);
            } else {
                currentLocation();
            }
        }
    }

    @Override
    public void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void OnClickToAddress(Address address) {
        latitude = address.getLatitude();
        longitude = address.getLongitude();
        location = address.getAddressLine(0);
        move(location);//new LatLng(address.getLatitude(), address.getLongitude()),
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
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
                    if(is_edit) {
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

    private OnMapReadyCallback mapReadyCallback() {
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                mMap.setOnMarkerDragListener(markerDragListener());
                mMap.setOnMapLongClickListener(mapLongClickListener());
                mMap.setOnCameraChangeListener(cameraChangeListener());
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

    private void newMarker(LatLng latLng, String title) {
        mMap.clear();
        longitude = latLng.longitude;
        latitude = latLng.latitude;
        location = title;
        marker = mMap.addMarker(new MarkerOptions().position(latLng).draggable(true).title(title));
        marker.setFlat(true);
    }


    private void currentLocation() {
//        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
//        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
//        Criteria crta = new Criteria();
//        crta.setAccuracy(Criteria.ACCURACY_FINE);
//        crta.setAltitudeRequired(true);
//        crta.setBearingRequired(true);
//        crta.setCostAllowed(true);
//        crta.setPowerRequirement(Criteria.POWER_LOW);
//        String provider = locationManager.getBestProvider(crta, true);
//        Log.d("LOCATION_PROVIDER", "provider : " + provider);
//        provider = LocationManager.GPS_PROVIDER;
//        locationManager.requestLocationUpdates(provider, 1000, 0, locationListener());
//        Location location = locationManager.getLastKnownLocation(provider);

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            longitude = location.getLongitude();
            latitude = location.getLatitude();
            move(location.toString());
            this.location = "current"; //TODO
        }
    }

    private LocationListener locationListener() {
        return new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                App.Log("onLocationChanged");
                App.Log(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
                App.Log("onStatusChanged");
                App.Log(provider);
                App.Log(status);
                App.Log(extras);
            }

            @Override
            public void onProviderEnabled(String provider) {
                App.Log("onProviderEnabled");
                App.Log(provider);
            }

            @Override
            public void onProviderDisabled(String provider) {
                App.Log("onProviderDisabled");
                App.Log(provider);
            }
        };
    }

    private void move(String title) {
        mMap.clear();
        LatLng latLng = new LatLng(latitude, longitude);
        marker = mMap.addMarker(new MarkerOptions()
                .position(latLng)
                .draggable(true)
                .title(title));
        marker.setFlat(true);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final FormFragment fragment = this;
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.view_button_voice_title).setOnClickListener(App.ClickToButtonVoice(fragment, App.FORM_VOICE_TITLE));
        view.findViewById(R.id.view_button_voice_description).setOnClickListener(App.ClickToButtonVoice(fragment, App.FORM_VOICE_DESCRIPTION));
        view.findViewById(R.id.view_button_voice_location).setOnClickListener(App.ClickToButtonVoice(fragment, App.FORM_VOICE_LOCATION));
        view.findViewById(R.id.view_fab_current_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit_text_location.setText("");
                currentLocation();
            }
        });
        view.findViewById(R.id.view_fab_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String title = edit_text_title.getText().toString();
                if (title.length() > 2) {
                    String description = edit_text_description.getText().toString();
                    int type = 0;
                    formViewModel.saveItem(title, description, type, location, longitude, latitude);
                    formViewModel.dataItem.observe(getViewLifecycleOwner(), new Observer<Item>() {
                        @Override
                        public void onChanged(Item item) {
                            NavHostFragment.findNavController(FormFragment.this)
                                    .navigate(R.id.action_FromFragment_to_HomeFragment);
                        }
                    });
                } else {
                    App.Toast(getContext(), "title " + title);
                }
            }
        });
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
