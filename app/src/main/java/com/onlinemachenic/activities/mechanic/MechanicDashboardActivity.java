package com.onlinemachenic.activities.mechanic;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.easywaylocation.EasyWayLocation;
import com.example.easywaylocation.Listener;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.microprogramers.library.PermissionAsker;
import com.onlinemachenic.R;
import com.onlinemachenic.activities.LoginActivity;
import com.onlinemachenic.models.MechanicRequest;

import java.util.ArrayList;

import static com.onlinemachenic.helpers.StaticConfig.allMechanicRequests;
import static com.onlinemachenic.helpers.StaticConfig.feedbackKeysList;
import static com.onlinemachenic.helpers.StaticConfig.fetchMechanicRequests;
import static com.onlinemachenic.helpers.StaticConfig.fetchMyFeedbacks;
import static com.onlinemachenic.helpers.StaticConfig.mMechanic;
import static com.onlinemachenic.helpers.StaticConfig.myFeedbacksList;
import static com.onlinemachenic.helpers.StaticConfig.setMechanicData;

public class MechanicDashboardActivity extends AppCompatActivity implements Listener
{

    private EasyWayLocation easyWayLocation;
    public static TextView all_requests_tv;

    private String[] permissions = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mechanic_dashboard);
        all_requests_tv = findViewById(R.id.all_requests_tv);
        feedbackKeysList = new ArrayList<>();
        myFeedbacksList = new ArrayList<>();
        easyWayLocation = new EasyWayLocation(this, false, false, this);
        all_requests_tv.setText("Pending Requests (" + calculatePendingRequests() + ")");
        fetchMechanicRequests();
        fetchMyFeedbacks();

        if (!mMechanic.isApproved())
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("You are not approved yet. Wait for approval to get started");
            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener()
            {
                @Override
                public void onClick(DialogInterface dialogInterface, int i)
                {
                    dialogInterface.cancel();
                }
            });
            builder.show();

            findViewById(R.id.cardView_requests).setEnabled(false);
            findViewById(R.id.cardView_feedbacks).setEnabled(false);
            findViewById(R.id.cardView_edit_profile).setEnabled(false);

        }


        if (PermissionAsker.hasPermissions(this, permissions))
        {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGpsEnabled)
            {
                displayLocationSettingsRequest();
            }
        }
        else
        {
            PermissionAsker.getUserPermissions(this, permissions);
        }
    }

    @Override
    public void locationOn()
    {

    }

    @Override
    public void currentLocation(Location location)
    {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        mMechanic.setLongitude(latitude);
        mMechanic.setLongitude(longitude);
        setMechanicData(mMechanic);


    }

    @Override
    public void locationCancelled()
    {

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

    public void pendingRequests(View view)
    {
        startActivity(new Intent(this, PendingRequestsActivity.class));
    }

    public void viewFeedbacks(View view)
    {
        startActivity(new Intent(this, FeedbacksActivity.class));
    }

    public void logoutMe(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you really want to logout");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(MechanicDashboardActivity.this, LoginActivity.class));
                finishAffinity();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        }).show();
    }

    int calculatePendingRequests()
    {
        int pending = 0;
        for (MechanicRequest request : allMechanicRequests)
        {
            if (request.getStatus().equals("pending"))
            {
                pending++;
            }
        }
        return pending;
    }


    private void displayLocationSettingsRequest()
    {
        LocationRequest mLocationRequest = LocationRequest.create().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(10 * 1000).setFastestInterval(1 * 1000);
        LocationSettingsRequest.Builder settingsBuilder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        settingsBuilder.setAlwaysShow(true);
        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(this).checkLocationSettings(settingsBuilder.build());
        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>()
        {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task)
            {
                try
                {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                } catch (ApiException ex)
                {
                    switch (ex.getStatusCode())
                    {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try
                            {
                                ResolvableApiException resolvableApiException = (ResolvableApiException) ex;
                                resolvableApiException.startResolutionForResult(MechanicDashboardActivity.this, REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e)
                            {

                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                            break;
                    }
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CHECK_SETTINGS)
        {
            switch (resultCode)
            {
                case Activity.RESULT_OK:
                    //Success Perform Task Here
                    break;
                case Activity.RESULT_CANCELED:
                    Log.e("GPS", "User denied to access location");
                    openGpsEnableSetting();
                    break;
            }
        }
        else if (requestCode == REQUEST_ENABLE_GPS)
        {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (!isGpsEnabled)
            {
                openGpsEnableSetting();
            }
        }
    }

    private void openGpsEnableSetting()
    {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivityForResult(intent, REQUEST_ENABLE_GPS);
    }

    public void editProfile(View view)
    {
        startActivity(new Intent(this, com.onlinemachenic.activities.mechanic.EditProfileActivity.class));
    }
}