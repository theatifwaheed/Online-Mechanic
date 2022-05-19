package com.onlinemachenic.activities.mechanic;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.onlinemachenic.R;

import static com.onlinemachenic.helpers.StaticConfig.allMechanicsList;
import static com.onlinemachenic.helpers.StaticConfig.mMechanic;
import static com.onlinemachenic.helpers.StaticConfig.mUser;
import static com.onlinemachenic.helpers.StaticConfig.setUserData;
import static com.onlinemachenic.helpers.StaticConfig.trackUserData;

public class MechanicDetailedMapActivity extends FragmentActivity implements OnMapReadyCallback,
                                                                             Listener
{

    private GoogleMap mMap;
    private EasyWayLocation easyWayLocation;
    private int i = 0;
    private MarkerOptions myMarkerOptions;
    public static MarkerOptions mechanicMarkerOptions;
    private Marker myLocationMarker;
    public static Marker mechanicMarker;
    private ExtendedFloatingActionButton distance_meter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_detailed_map);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        easyWayLocation = new EasyWayLocation(this, false, false, this);
        String sender_id = getIntent().getStringExtra("sender_id");
        trackUserData(sender_id);

        distance_meter = findViewById(R.id.distance_meter);
        mechanicMarkerOptions = new MarkerOptions();
        myMarkerOptions = new MarkerOptions();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            mMap.setMyLocationEnabled(true);
        }

        new Handler().postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                createMarker(mUser.getLatitude(), mUser.getLongitude(), mUser.getName());
            }
        }, 3000);

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
    public void locationOn()
    {

    }

    @Override
    public void currentLocation(Location location)
    {

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (i == 0)
        {
            myMarkerOptions.position(latLng);
            myLocationMarker = mMap.addMarker(myMarkerOptions);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12/*(float) ((20 / allMechanicsList.size()) * 1.5)*/));
            myLocationMarker.setTitle("My Location");
        }

        i++;
        mUser.setLatitude(location.getLatitude());
        mUser.setLongitude(location.getLongitude());
        setUserData(mUser);
        myLocationMarker.setPosition(latLng);

        double distance = meterDistanceBetweenPoints((float) mMechanic.getLatitude(), (float) mMechanic.getLongitude(), (float) mUser.getLatitude(), (float) mUser.getLongitude());

        distance /= 1000;
        double roundOff = Math.round(distance * 100.0) / 100.0;

        distance_meter.setText(roundOff + " km");

        if (roundOff <= 0.03)
        {
            distance_meter.setText("Almost Found");
            distance_meter.setIconResource(R.drawable.ic_location_found);
        }
        else
        {
            distance_meter.setIconResource(R.drawable.ic_locator);
        }


    }

    @Override
    public void locationCancelled()
    {

    }

    private void createMarker(double latitude, double longitude, String title)
    {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f, 0.5f).title(title).icon(bitmapDescriptorFromVector(MechanicDetailedMapActivity.this, R.drawable.ic_user_marker)));
    }


    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b)
    {
        float pk = (float) (180.f / Math.PI);

        float a1 = lat_a / pk;
        float a2 = lng_a / pk;
        float b1 = lat_b / pk;
        float b2 = lng_b / pk;

        double t1 = Math.cos(a1) * Math.cos(a2) * Math.cos(b1) * Math.cos(b2);
        double t2 = Math.cos(a1) * Math.sin(a2) * Math.cos(b1) * Math.sin(b2);
        double t3 = Math.sin(a1) * Math.sin(b1);
        double tt = Math.acos(t1 + t2 + t3);

        return 6366000 * tt;
    }
}