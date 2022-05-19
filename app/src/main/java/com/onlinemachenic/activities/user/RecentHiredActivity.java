package com.onlinemachenic.activities.user;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.onlinemachenic.R;
import com.onlinemachenic.adapters.RecentHiredAdapter;

public class RecentHiredActivity extends AppCompatActivity
{
    private RecyclerView recent_hired_recyclerView;
    private RecentHiredAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recent_hired);
        recent_hired_recyclerView = findViewById(R.id.recent_hired_recyclerView);

        adapter = new RecentHiredAdapter(this);
        recent_hired_recyclerView.setAdapter(adapter);
        recent_hired_recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }
}