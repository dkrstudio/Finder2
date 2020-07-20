package com.finder.application.ui.activity.ui.main.home;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import com.finder.application.App;
import com.finder.application.R;
import com.finder.application.model.Item;
import com.finder.application.ui.activity.ui.adapters.AdapterItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.*;

import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        homeViewModel.putList();
        homeViewModel.dataItems.observe(getViewLifecycleOwner(), new Observer<List<Item>>() {
            @Override
            public void onChanged(List<Item> items) {
                App.items = items;

                SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
                mapFragment.getMapAsync(mapReadyCallback());
                googleApiClient = new GoogleApiClient.Builder(getContext())
                        .addConnectionCallbacks(connectionCallbacks())
                        .addOnConnectionFailedListener(connectionFailedListener())
                        .addApi(LocationServices.API)
                        .build();

                googleApiClient.connect();
            }
        });
        return root;
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
                    add_markers(App.items);
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, App.LOCATION_PERMISSION_REQUEST_CODE);
                }
            }

            @Override
            public void onConnectionSuspended(int i) {

            }
        };
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == App.LOCATION_PERMISSION_REQUEST_CODE) {
            add_markers(App.items);
        }
    }

    private void add_markers(List<Item> items) {
        mMap.clear();
        System.out.println("___________________________________________________________________");
        System.out.println("items " + items.size());
        System.out.println("___________________________________________________________________");
        for (Item item : items) {
//            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(getMarkerBitmapFromView(null));
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(item.latitude, item.longitude))
//                .icon(icon)
//                .draggable(true)
                    .title(item.title));
            System.out.println("___________________________________________________________________");
            System.out.println(item.unic);
            System.out.println(item.title);
            System.out.println(item.latitude);
            System.out.println(item.longitude);
            System.out.println("___________________________________________________________________");
        }
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if (location != null) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
//                .icon(icon)
                    .draggable(true)
                    .title("I"));
            marker.setFlat(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    private OnMapReadyCallback mapReadyCallback() {
        return new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            }
        };
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

//        HomeFragmentDirections.ActionHomeFragmentToHomeSecondFragment action =
//                HomeFragmentDirections.actionHomeFragmentToHomeSecondFragment
//                        ("From HomeFragment");
//        NavHostFragment.findNavController(HomeFragment.this)
//                .navigate(action);
        view.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putLong("id", 0);//Calendar.getInstance().getTimeInMillis()
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_HomeFragment_to_FormFragment, bundle);
            }
        });
    }

    private Bitmap getMarkerBitmapFromView(Intent resId) {

        View customMarkerView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_marker, null);
        TextView user_name_view = (TextView) customMarkerView.findViewById(R.id.user_name);
        user_name_view.setText("dsd23342");
        ImageView markerImageView = (ImageView) customMarkerView.findViewById(R.id.profile_image);
//        markerImageView.setImageResource(resId);
        customMarkerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        customMarkerView.layout(0, 0, customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight());
        customMarkerView.buildDrawingCache();
        Bitmap returnedBitmap = Bitmap.createBitmap(customMarkerView.getMeasuredWidth(), customMarkerView.getMeasuredHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        canvas.drawColor(Color.WHITE, PorterDuff.Mode.SRC_IN);
        Drawable drawable = customMarkerView.getBackground();
        if (drawable != null)
            drawable.draw(canvas);
        customMarkerView.draw(canvas);
        return returnedBitmap;
    }
}
