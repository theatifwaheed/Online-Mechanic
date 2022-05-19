package com.onlinemachenic.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.onlinemachenic.R;
import com.onlinemachenic.activities.mechanic.MechanicDetailedMapActivity;
import com.onlinemachenic.activities.user.DetailedMapActivity;
import com.onlinemachenic.helpers.StaticConfig;
import com.onlinemachenic.models.MechanicRequest;
import com.onlinemachenic.models.User;

import java.util.ArrayList;

import static com.onlinemachenic.helpers.StaticConfig.allUsersList;

/**
 * Created by Mubashar Hussain on 06/05/2021.
 **/
public class RecentHiredAdapter extends RecyclerView.Adapter<RecentHiredAdapter.RecyclerViewHolder>
{
    private Context context;
    private ArrayList<MechanicRequest> requestsArrayList;

    public RecentHiredAdapter(Context context)
    {
        this.context = context;
        requestsArrayList = new ArrayList<>(StaticConfig.allMechanicRequestsSentByMe);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_with_status, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position)
    {

        MechanicRequest request = requestsArrayList.get(position);
        User sender = getSenderDetailsByID(request.getRequest_sender());

        holder.tv_date.setText(request.getDate());
        holder.tv_request_text.setText(request.getReason());
        holder.tv_request_sender.setText(sender.getName());

        holder.tv_request_status.setText(request.getStatus());

        if (request.getStatus().equals("processing"))
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    Intent intent = new Intent(context, DetailedMapActivity.class);
                    intent.putExtra("mechanic_id", request.getMechanic_id());
                    intent.putExtra("request_id", request.getRequest_id());

                    Log.d("TAG", "onClick: req_id: " + request.getRequest_id());

                    context.startActivity(intent);
                }
            });
        }



    }

    @Override
    public int getItemCount()
    {
        return requestsArrayList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_request_sender, tv_request_text, tv_date, tv_request_status;
        Button btn_request_status;


        public RecyclerViewHolder(@NonNull View itemView)
        {
            super(itemView);

            tv_request_sender = itemView.findViewById(R.id.tv_request_sender);
            tv_request_text = itemView.findViewById(R.id.tv_request_text);
            tv_date = itemView.findViewById(R.id.tv_date);
            tv_request_status = itemView.findViewById(R.id.request_status);
        }
    }

    private User getSenderDetailsByID(String id)
    {
        for (User user : allUsersList)
        {
            if (user.getUser_id().equals(id))
            {
                return user;
            }
        }

        return null;
    }
}
