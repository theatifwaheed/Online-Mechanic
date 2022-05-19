package com.onlinemachenic.activities.mechanic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.onlinemachenic.R;
import com.onlinemachenic.adapters.RequestsAdapter;

public class PendingRequestsActivity extends AppCompatActivity
{
    private RecyclerView pending_requests_recyclerView;
    private RequestsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_requests);
        pending_requests_recyclerView = findViewById(R.id.pending_requests_recyclerView);

        adapter = new RequestsAdapter(this, "mechanic");
        pending_requests_recyclerView.setAdapter(adapter);
        pending_requests_recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume()
    {
        adapter.notifyDataSetChanged();
        super.onResume();
    }
}