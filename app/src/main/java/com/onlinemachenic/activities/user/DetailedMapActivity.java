package com.onlinemachenic.activities.user;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
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
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.onlinemachenic.R;
import com.onlinemachenic.helpers.FCMMessages;
import com.onlinemachenic.helpers.StaticConfig;
import com.onlinemachenic.models.Feedback;
import com.onlinemachenic.models.MechanicRequest;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.onlinemachenic.helpers.StaticConfig.*;

public class DetailedMapActivity extends FragmentActivity implements OnMapReadyCallback,
                                                                     Listener,
                                                                     RoutingListener
{

    private GoogleMap mMap;
    private EasyWayLocation easyWayLocation;
    private int i = 0;
    private MarkerOptions myMarkerOptions;
    public static MarkerOptions mechanicMarkerOptions;
    private Marker myLocationMarker;
    public static Marker mechanicMarker;
    private ExtendedFloatingActionButton distance_meter;
    private MechanicRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        easyWayLocation = new EasyWayLocation(this, false, false, this);
        String mechanic_id = getIntent().getStringExtra("mechanic_id");
        String request_id = getIntent().getStringExtra("request_id");
        trackMechanicData(mechanic_id);

        request = StaticConfig.getMyRequestByID(request_id);

        Log.d("TAG", "onCreate: req_id_received: " + request.getRequest_id());

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
                createMarker(mMechanic.getLatitude(), mMechanic.getLongitude(), mMechanic.getName(), mMechanic.getShop_name());
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
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, (float) 12 /*((20 / allMechanicsList.size()) * 1.5)*/));
            myLocationMarker.setTitle("My Location");
        }

        i++;
        mUser.setLatitude(location.getLatitude());
        mUser.setLongitude(location.getLongitude());
        setUserData(mUser);
        myLocationMarker.setPosition(latLng);

        float distance = (float) meterDistanceBetweenPoints((float)mUser.getLatitude(),(float) mUser.getLongitude(),(float) mMechanic.getLatitude(), (float) mMechanic.getLongitude());

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

        LatLng origin = latLng;
        LatLng destination = new LatLng(mMechanic.getLatitude(), mMechanic.getLongitude());


        if (i == 5)
        {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(origin, destination)
                    .build();
            routing.execute();
        }

    }

    @Override
    public void locationCancelled()
    {

    }

    private void createMarker(double latitude, double longitude, String title, String snippet)
    {
        LatLng latLng = new LatLng(latitude, longitude);
        mMap.addMarker(new MarkerOptions().position(latLng).anchor(0.5f, 0.5f).title(title).snippet(snippet).icon(bitmapDescriptorFromVector(DetailedMapActivity.this, R.drawable.ic_mechanic)));
    }

    @Override
    public void onRoutingFailure(RouteException e)
    {

    }

    @Override
    public void onRoutingStart()
    {
//        Toast.makeText(this, "Finding best routes", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int i)
    {
        for (int x = 0; x <route.size(); x++) {

            //In case of more than 5 alternative routes
//            int colorIndex = x % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
//            polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            polyOptions.color(getResources().getColor(R.color.colorRouteLine));
            polyOptions.width(10 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
//            polylines.add(polyline);

//            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRoutingCancelled()
    {

    }

    public void markComplete(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Mark as completed");
        builder.setMessage("This will be marked as completed. Make sure you share your feedback before completing it");
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.reason_input_layout, (ViewGroup) findViewById(android.R.id.content), false);
// Set up the input
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        input.setHint("Your feedback here");
        builder.setView(viewInflated);

        builder.setPositiveButton("Accept", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String feedback_text = input.getText().toString();
                if (feedback_text.isEmpty())
                {
                    Toast.makeText(DetailedMapActivity.this, "You must share your feedback", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    ProgressDialog progressDialog = new ProgressDialog(DetailedMapActivity.this);
                    progressDialog.setMessage("Submitting your feedback");
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    String date = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(new Date());
                    Feedback feedback = new Feedback(System.currentTimeMillis() + "", myUID, date, feedback_text);
                    new FCMMessages().sendMessageSingle(DetailedMapActivity.this, mMechanic.getNotification_token(), "Work Accepted", mUser.getName() + " marked your work as completed and left a feedback for you.", null);
                    request.setStatus("completed");
                    mechanicsRef.child(mMechanic.getMechanic_id()).child("requests").child(request.getRequest_id()).setValue(request);
                    mechanicsRef.child(mMechanic.getMechanic_id()).child("feedbacks").child(feedback.getFeedback_id()).setValue(feedback)
                            .addOnCompleteListener(new OnCompleteListener<Void>()
                            {
                                @Override
                                public void onComplete(@NonNull Task<Void> task)
                                {
                                    progressDialog.cancel();
                                    if (task.isSuccessful())
                                    {
                                        Toast.makeText(DetailedMapActivity.this, "Thanks for sharing your feedback", Toast.LENGTH_SHORT).show();
                                        finishAffinity();
                                        startActivity(new Intent(DetailedMapActivity.this, UserDashboardActivity.class));
                                    }
                                    else
                                    {
                                        Toast.makeText(DetailedMapActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        })
                .setNegativeButton("Not Now", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        dialog.cancel();
                    }
                })
                .show();
    }

    private double meterDistanceBetweenPoints(float lat_a, float lng_a, float lat_b, float lng_b) {
        float pk = (float) (180.f/Math.PI);

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