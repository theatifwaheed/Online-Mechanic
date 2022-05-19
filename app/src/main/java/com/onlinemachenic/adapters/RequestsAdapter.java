package com.onlinemachenic.adapters;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.onlinemachenic.helpers.StaticConfig;
import com.onlinemachenic.models.MechanicRequest;
import com.onlinemachenic.models.User;

import java.util.ArrayList;

import static com.onlinemachenic.helpers.StaticConfig.allUsersList;

/**
 * Created by Mubashar Hussain on 06/05/2021.
 **/
public class RequestsAdapter extends RecyclerView.Adapter<RequestsAdapter.RecyclerViewHolder>
{
    private Context context;
    private String type;
    private ArrayList<MechanicRequest> requestsArrayList;

    public RequestsAdapter(Context context, String type)
    {
        this.context = context;
        this.type = type;

        if (type.equals("user"))
        {
            requestsArrayList = new ArrayList<>(StaticConfig.allMechanicRequestsSentByMe);
        }
        else
        {
            requestsArrayList = new ArrayList<>(StaticConfig.allMechanicRequests);
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        //0 for pending and 1 for done
        View view;
        if (viewType == 0)
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pending_request, parent, false);
        }
        else
        {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_with_status, parent, false);
        }

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

        if (!request.getStatus().equals("pending"))
        {
            holder.tv_request_status.setText(request.getStatus());
        }

        if (getItemViewType(position) == 0 && holder.btn_request_status != null)
        {
            if (request.getVehicle_type() != null && !request.getVehicle_type().isEmpty())
            {
                holder.vehicle_type.setText("Vehicle Type: " + request.getVehicle_type());
                holder.vehicle_type.setVisibility(View.VISIBLE);
            }
            else
            {
                holder.vehicle_type.setVisibility(View.GONE);
            }

            holder.btn_request_status.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (request.getStatus().equals("pending"))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setMessage("Respond to this request")
                                .setPositiveButton("Accept", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which)
                                    {
                                        ProgressDialog progressDialog = new ProgressDialog(context);
                                        progressDialog.setMessage("Please Wait");
                                        progressDialog.setCancelable(false);
                                        progressDialog.show();
                                        MechanicRequest request = requestsArrayList.get(position);
                                        request.setStatus("processing");
                                        StaticConfig.mechanicsRef.child(StaticConfig.myUID).child("requests").child(request.getRequest_id()).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>()
                                        {
                                            @Override
                                            public void onSuccess(Void aVoid)
                                            {
                                                progressDialog.cancel();
                                                Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(context, MechanicDetailedMapActivity.class);
                                                intent.putExtra("sender_id", request.getRequest_sender());
                                                context.startActivity(intent);
                                            }
                                        });
                                    }
                                }).setNegativeButton("Reject", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialog, int which)
                            {
                                MechanicRequest request = requestsArrayList.get(position);
                                request.setStatus("rejected by mechanic");
                                StaticConfig.mechanicsRef.child(StaticConfig.myUID).child("requests").child(request.getRequest_id()).setValue(request).addOnSuccessListener(new OnSuccessListener<Void>()
                                {
                                    @Override
                                    public void onSuccess(Void aVoid)
                                    {
                                        Toast.makeText(context, "Rejected", Toast.LENGTH_SHORT).show();
                                        ((Activity) context).finish();
                                    }
                                });
                            }
                        }).show();
                    }
                }
            });
        }

        if (request.getStatus().equals("processing"))
        {
            holder.itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    if (request.getStatus().equals("processing"))
                    {
                        Intent intent = new Intent(context, MechanicDetailedMapActivity.class);
                        intent.putExtra("sender_id", request.getRequest_sender());
                        context.startActivity(intent);
                    }
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position)
    {
        MechanicRequest request = requestsArrayList.get(position);
        if (request.getStatus().equals("pending"))
        {
            return 0;
        }
        else
        {
            return 1;
        }
    }

    @Override
    public int getItemCount()
    {
        return requestsArrayList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView tv_request_sender, tv_request_text, tv_date, tv_request_status, vehicle_type;
        Button btn_request_status;


        public RecyclerViewHolder(@NonNull View itemView)
        {
            super(itemView);

            tv_request_sender = itemView.findViewById(R.id.tv_request_sender);
            tv_request_text = itemView.findViewById(R.id.tv_request_text);
            tv_date = itemView.findViewById(R.id.tv_date);

//            if (getItemViewType() == 0)
//            {
            btn_request_status = itemView.findViewById(R.id.btn_request_status);
//            }
//            else
//            {
            tv_request_status = itemView.findViewById(R.id.request_status);
            vehicle_type = itemView.findViewById(R.id.vehicle_type);
//            }
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
