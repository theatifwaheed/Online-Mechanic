package com.onlinemachenic.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.microprogramers.library.PermissionAsker;
import com.onlinemachenic.R;
import com.onlinemachenic.activities.admin.AdminDashboardActivity;
import com.onlinemachenic.activities.mechanic.MechanicDashboardActivity;
import com.onlinemachenic.activities.mechanic.MechanicSignUpActivity;
import com.onlinemachenic.activities.user.UserDashboardActivity;
import com.onlinemachenic.activities.user.UserSignUpActivity;
import com.onlinemachenic.models.Mechanic;
import com.onlinemachenic.models.User;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import static com.onlinemachenic.helpers.StaticConfig.allMechanicRequests;
import static com.onlinemachenic.helpers.StaticConfig.allMechanicRequestsSentByMe;
import static com.onlinemachenic.helpers.StaticConfig.allMechanicsList;
import static com.onlinemachenic.helpers.StaticConfig.allUsersList;
import static com.onlinemachenic.helpers.StaticConfig.fetchMechanicsData;
import static com.onlinemachenic.helpers.StaticConfig.fetchUsersData;
import static com.onlinemachenic.helpers.StaticConfig.mechanicRequestsKeys;
import static com.onlinemachenic.helpers.StaticConfig.mechanicsKeysList;
import static com.onlinemachenic.helpers.StaticConfig.myUID;
import static com.onlinemachenic.helpers.StaticConfig.trackMechanicData;
import static com.onlinemachenic.helpers.StaticConfig.trackUserData;
import static com.onlinemachenic.helpers.StaticConfig.usersKeysList;

public class LoginActivity extends AppCompatActivity
{

    private EditText edt_email, edt_password;
    private int mechanics = 0;
    private ProgressDialog progressDialog;
    private String[] permissions = new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

    private SettingsClient mSettingsClient;
    private LocationSettingsRequest mLocationSettingsRequest;
    private static final int REQUEST_CHECK_SETTINGS = 214;
    private static final int REQUEST_ENABLE_GPS = 516;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        edt_email = findViewById(R.id.edt_email);
        edt_password = findViewById(R.id.edt_password);
        mechanics = 0;
        allMechanicsList = new ArrayList<>();
        mechanicsKeysList = new ArrayList<>();
        mechanicRequestsKeys = new ArrayList<>();
        allMechanicRequestsSentByMe = new ArrayList<>();
        allMechanicRequests = new ArrayList<>();
        allUsersList = new ArrayList<>();
        usersKeysList = new ArrayList<>();
        myUID = "";

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging In");
    }

    public void login(View view)
    {
        String email = edt_email.getText().toString();
        String password = edt_password.getText().toString();
        if (email.isEmpty() || password.isEmpty())
        {
            Toast.makeText(this, "Both fields required", Toast.LENGTH_SHORT).show();
        }
        else if (email.equals("admin") && password.equals("admin"))
        {
            progressDialog.show();
            progressDialog.setCancelable(false);
            loginAccount();
        }
        else
        {
            progressDialog.show();
            progressDialog.setCancelable(false);

            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>()
            {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {

                    if (task.isSuccessful())
                    {
                        myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        loginAccount();
                    }
                    else
                    {
                        progressDialog.cancel();
                        Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    }

    private void loginAccount()
    {
        fetchUsersData();
        fetchMechanicsData();
        progressDialog = new ProgressDialog(LoginActivity.this);
        progressDialog.setMessage("Logging in");
        progressDialog.show();
        progressDialog.setCancelable(false);


        Timer timer = new Timer();
        timer.schedule(new Check(), 3000, 3000);

    }

    private boolean isMechanic()
    {
        for (Mechanic mechanic : allMechanicsList)
        {
            if (mechanic.getMechanic_id().equals(myUID))
            {
                return true;
            }
        }
        return false;
    }
    private boolean isUser()
    {
        for (User user : allUsersList)
        {
            if (user.getUser_id().equals(myUID))
            {
                return true;
            }
        }
        return false;
    }

    public void signUpAsUser(View view)
    {

        if (PermissionAsker.hasPermissions(this, permissions))
        {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGpsEnabled)
            {
                displayLocationSettingsRequest();
            }
            else
            {
                trackUserData(myUID);
                startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class));
            }

        }
        else
        {
            PermissionAsker.getUserPermissions(this, permissions);
        }
    }

    public void signUpAsMechanic(View view)
    {
        if (PermissionAsker.hasPermissions(this, permissions))
        {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (!isGpsEnabled)
            {
                displayLocationSettingsRequest();
            }
            else
            {
                startActivity(new Intent(this, MechanicSignUpActivity.class));
//                trackMechanicData(myUID);
//                startActivity(new Intent(LoginActivity.this, MechanicDashboardActivity.class));
            }
        }
        else
        {
            PermissionAsker.getUserPermissions(this, permissions);
        }
    }


    @Override
    protected void onStart()
    {
        if (FirebaseAuth.getInstance().getCurrentUser() != null)
        {
            myUID = FirebaseAuth.getInstance().getCurrentUser().getUid();
            Log.d("TAG", "onStart: " + myUID);
            loginAccount();
        }
        super.onStart();
    }

    class Check extends TimerTask
    {
        @Override
        public void run()
        {
            if (allMechanicsList.size() > 0 && allUsersList.size() > 0)
            {
                if (isMechanic())
                {
                    trackMechanicData(myUID);
                    startActivity(new Intent(LoginActivity.this, MechanicDashboardActivity.class));
                }
                else if (isUser())
                {
                    trackUserData(myUID);
                    startActivity(new Intent(LoginActivity.this, UserDashboardActivity.class));
                }
                else
                {
                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                }
                this.cancel();
                finish();
            }
        }
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
                                resolvableApiException.startResolutionForResult(LoginActivity.this, REQUEST_CHECK_SETTINGS);
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
}