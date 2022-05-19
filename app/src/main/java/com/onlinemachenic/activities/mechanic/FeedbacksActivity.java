package com.onlinemachenic.activities.mechanic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.onlinemachenic.R;
import com.onlinemachenic.adapters.FeedbacksAdapter;

public class FeedbacksActivity extends AppCompatActivity
{
    private RecyclerView feedbacks_recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedbacks);
        feedbacks_recyclerView = findViewById(R.id.feedbacks_recyclerView);
        FeedbacksAdapter adapter = new FeedbacksAdapter(this);
        feedbacks_recyclerView.setAdapter(adapter);
        feedbacks_recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}