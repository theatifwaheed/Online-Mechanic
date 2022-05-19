package com.onlinemachenic.activities.user;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.onlinemachenic.R;
import com.onlinemachenic.adapters.NearbyMechanicsAdapter;
import com.onlinemachenic.helpers.StaticConfig;

import java.util.ArrayList;

import static com.onlinemachenic.helpers.StaticConfig.allMechanicsList;

public class UserMapsActivity extends FragmentActivity implements OnMapReadyCallback,
                                                                  Listener,
                                                                  GoogleMap.OnMarkerClickListener
{

    public static GoogleMap mMap;
//    private RecyclerView mechanics_recycler_view;
    public static Location location;
//    private NearbyMechanicsAdapter adapter;
    private EasyWayLocation easyWayLocation;
    private MarkerOptions myMarkerOptions;
    private Marker myLocationMarker;
    private ArrayList<Marker> allMarkers;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_maps_updated);
        StaticConfig.context = this;

//        mechanics_recycler_view = findViewById(R.id.mechanics_recycler_view);
        easyWayLocation = new EasyWayLocation(this, false, false, this);
        myMarkerOptions = new MarkerOptions();
        allMarkers = new ArrayList<>();
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapView);
        mapFragment.getMapAsync(this);


//        adapter = new NearbyMechanicsAdapter(UserMapsActivity.this);
//        mechanics_recycler_view.setAdapter(adapter);
//        mechanics_recycler_view.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }


        for (int i = 0; i < StaticConfig.getApprovedMechanics().size(); i++)
        {
            createMarker(StaticConfig.getApprovedMechanics().get(i).getLatitude(), StaticConfig.getApprovedMechanics().get(i).getLongitude(), StaticConfig.getApprovedMechanics().get(i).getName(), StaticConfig.getApprovedMechanics().get(i).getShop_name());
        }


    }

    private void createMarker(double latitude, double longitude, String title, String snippet)
    {
        LatLng latLng = new LatLng(latitude, longitude);
        MarkerOptions markerOption = new MarkerOptions().position(latLng).anchor(0.5f, 0.5f).title(title).snippet(snippet).icon(bitmapDescriptorFromVector(UserMapsActivity.this, R.drawable.ic_mechanic));
        Marker marker = mMap.addMarker(markerOption);
        allMarkers.add(marker);
    }

    @Override
    public void locationOn()
    {

    }

    @Override
    public void currentLocation(Location location)
    {
        UserMapsActivity.location = location;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (i == 0)
        {
            myMarkerOptions.position(latLng);
            myLocationMarker = mMap.addMarker(myMarkerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 12/*((20 / allMechanicsList.size()) * 1.5)*/));
            myLocationMarker.setTitle("My Location");
            i++;
        }

        myLocationMarker.setPosition(latLng);
//        adapter.notifyDataSetChanged();
        Log.d("TAG", "currentLocation: " + location);
    }

    @Override
    public void locationCancelled()
    {

    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId)
    {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onResume()
    {
        easyWayLocation.startLocation();
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        easyWayLocation.endUpdates();
        super.onPause();
    }

    @Override
    public boolean onMarkerClick(@NonNull Marker marker)
    {
        int index = allMarkers.indexOf(marker);
        Intent intent = new Intent(UserMapsActivity.this, MechanicDetailsActivity.class);
        String selected_mechanic_id =StaticConfig.getApprovedMechanics().get(index).getMechanic_id();
        intent.putExtra("selected_mechanic_id", selected_mechanic_id);
        startActivity(intent);

        return false;
    }

    public void showAllMechanics(View view)
    {
        startActivity(new Intent(this, AllMechanicsActivity.class));
    }
}