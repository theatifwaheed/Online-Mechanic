package com.onlinemachenic.activities.admin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.onlinemachenic.R;
import com.onlinemachenic.activities.LoginActivity;

public class AdminDashboardActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);
    }

    public void logoutAdmin(View view)
    {
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    public void showRegisteredMechanics(View view)
    {
        startActivity(new Intent(this, AllRegisteredMechanicsActivity.class));
    }
}