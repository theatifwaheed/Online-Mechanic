package com.onlinemachenic.adapters;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.onlinemachenic.R;
import com.onlinemachenic.activities.user.MechanicDetailsActivity;
import com.onlinemachenic.helpers.FCMMessages;
import com.onlinemachenic.models.Mechanic;
import com.onlinemachenic.models.MechanicRequest;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.onlinemachenic.helpers.StaticConfig.*;
import static com.onlinemachenic.activities.user.UserMapsActivity.*;

/**
 * Created by Mubashar Hussain on 07/05/2021.
 **/
public class NearbyMechanicsAdapter extends RecyclerView.Adapter<NearbyMechanicsAdapter.RecyclerViewHolder> implements Filterable
{
    Context context;
    private ViewGroup viewGroup;
    private ArrayList<Mechanic> mechanicsListFull;
    private ArrayList<Mechanic> mechanicArrayList;


    public NearbyMechanicsAdapter(Context context, ArrayList<Mechanic> mechanicArrayList)
    {
        this.context = context;
        this.mechanicArrayList = mechanicArrayList;
        mechanicsListFull = new ArrayList<>(mechanicArrayList);
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        viewGroup = parent;
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mechanic, parent, false);
        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position)
    {
        Mechanic mechanic = mechanicArrayList.get(position);
        Picasso.get().load(mechanic.getPic_url()).into(holder.mechanic_image);
        holder.mechanic_name.setText(mechanic.getName());
        holder.mechanic_shop.setText(mechanic.getShop_name());

        double mechanic_latitude = mechanic.getLatitude();
        double mechanic_longitude = mechanic.getLongitude();
//        Location mechanic_location = new Location("");
//        mechanic_location.setLatitude(mechanic_latitude);
//        mechanic_location.setLongitude(mechanic_longitude);
//        location.distanceTo(mechanic_location);
        double roundOff = 0;
        if (location != null)
        {
            double my_latitude = location.getLatitude();
            double my_longitude = location.getLongitude();
            double distance = meterDistanceBetweenPoints((float) my_latitude, (float) my_longitude, (float) mechanic_latitude, (float) mechanic_longitude);

            double kilo_distance = distance / 1000;

            roundOff = Math.round(kilo_distance * 100.0) / 100.0;
            holder.shop_distance.setText((roundOff) + " km");
        }

        double finalRoundOff = roundOff;
        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
//                AlertDialog.Builder builder = new AlertDialog.Builder(context);
//                builder.setTitle("Hire this mechanic");
//                builder.setMessage("This mechanic is " + finalRoundOff + " km away from your location. Do you really want to hire him ?");
//
//                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mechanic.getLatitude(), mechanic.getLongitude())));
//
//
//                View viewInflated = LayoutInflater.from(context).inflate(R.layout.reason_input_layout, (ViewGroup) viewGroup, false);
//// Set up the input
//                final EditText input = (EditText) viewInflated.findViewById(R.id.input);
//
//                builder.setView(viewInflated);
//                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
//                {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which)
//                    {
//                        ProgressDialog progressDialog = new ProgressDialog(context);
//                        progressDialog.setMessage("Sending Request");
//                        progressDialog.show();
//                        progressDialog.setCancelable(false);
//
//                        String date = new SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(new Date());
//                        String text_body = mUser.getName() + " is " + finalRoundOff + " km away from your location. Do you want to go for him ?";
//                        String reason = input.getText().toString();
//                        new FCMMessages().sendMessageSingle(context, mechanic.getNotification_token(), mUser.getName() + " wants to hire you", text_body, null);
//
//                        String req_id = System.currentTimeMillis() + "";
//
//                        MechanicRequest request = new MechanicRequest(req_id, mechanic.getMechanic_id(), mUser.getUser_id(), date, text_body, "pending", reason);
//                        mechanicsRef.child(mechanic.getMechanic_id()).child("requests").child( req_id).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>()
//                        {
//                            @Override
//                            public void onComplete(@NonNull Task<Void> task)
//                            {
//                                progressDialog.cancel();
//                                if (task.isSuccessful())
//                                {
//                                    dialog.cancel();
//                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
//                                    builder1.setTitle("Request Sent");
//                                    builder1.setMessage("Please wait at least 5 minutes until the mechanic responds");
//                                    builder1.setNeutralButton("Ok", null);
//                                    builder1.show();
//                                }
//                                else
//                                {
//                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }
//                        });
//
//                    }
//                })
//                        .setNegativeButton("No", new DialogInterface.OnClickListener()
//                        {
//                            @Override
//                            public void onClick(DialogInterface dialog, int which)
//                            {
//                                dialog.cancel();
//                            }
//                        })
//                        .show();

                Intent intent = new Intent(context, MechanicDetailsActivity.class);
                intent.putExtra("selected_mechanic_id", mechanic.getMechanic_id());
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return mechanicArrayList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
            TextView    mechanic_name,
                        mechanic_shop,
                        shop_distance;
        CircleImageView mechanic_image;

        public RecyclerViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mechanic_name = itemView.findViewById(R.id.mechanic_name);
            mechanic_shop = itemView.findViewById(R.id.mechanic_shop);
            shop_distance = itemView.findViewById(R.id.shop_distance);
            mechanic_image = itemView.findViewById(R.id.mechanic_image);


        }
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




    @Override
    public Filter getFilter() {
        return recipesFilter;
    }

    private Filter recipesFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            ArrayList<Mechanic> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0)
            {
                filteredList.addAll(mechanicsListFull);
            }
            else
            {
                String filterPattern = constraint.toString().toLowerCase().trim();


                for (Mechanic mechanic: mechanicsListFull)
                {

                    if (mechanic.getName().toLowerCase().trim().startsWith(filterPattern.trim()))
                    {
                        filteredList.add(mechanic);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            mechanicArrayList.clear();
            mechanicArrayList.addAll((ArrayList) results.values);
            notifyDataSetChanged();
        }
    };
}
