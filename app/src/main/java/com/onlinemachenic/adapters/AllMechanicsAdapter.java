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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.onlinemachenic.R;
import com.onlinemachenic.activities.user.MechanicDetailsActivity;
import com.onlinemachenic.helpers.StaticConfig;
import com.onlinemachenic.models.Mechanic;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.onlinemachenic.activities.user.UserMapsActivity.location;

/**
 * Created by Mubashar Hussain on 07/05/2021.
 **/
public class AllMechanicsAdapter extends RecyclerView.Adapter<AllMechanicsAdapter.RecyclerViewHolder> implements Filterable
{
    Context context;
    private ViewGroup viewGroup;
    private ArrayList<Mechanic> mechanicsListFull;
    private ArrayList<Mechanic> mechanicArrayList;


    public AllMechanicsAdapter(Context context, ArrayList<Mechanic> mechanicArrayList)
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mechanic_with_status, parent, false);
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
        holder.btn_status.setText(mechanic.isApproved() ? "Approved": "Unapproved");
        holder.btn_status.setEnabled(!mechanic.isApproved());

        holder.btn_status.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("Approve Mechanic");
                builder.setMessage("This mechanic will be able to provide his services. Are you sure to approve this mechanic");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i)
                    {
                        ProgressDialog progressDialog = new ProgressDialog(context);
                        progressDialog.setMessage("Please Wait");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        StaticConfig.mechanicsRef.child(mechanic.getMechanic_id()).child("info").child("approved").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>()
                        {
                            @Override
                            public void onComplete(@NonNull @org.jetbrains.annotations.NotNull Task<Void> task)
                            {
                                progressDialog.cancel();

                                if (task.isSuccessful())
                                {
                                    Toast.makeText(context, "Approved", Toast.LENGTH_SHORT).show();
                                    ((Activity) context).finish();
                                }
                                else
                                {
                                    Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener()
                        {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i)
                            {
                                dialogInterface.cancel();
                            }
                        })
                        .show();
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
                        mechanic_shop;
        CircleImageView mechanic_image;
        Button btn_status;

        public RecyclerViewHolder(@NonNull View itemView)
        {
            super(itemView);

            mechanic_name = itemView.findViewById(R.id.mechanic_name);
            mechanic_shop = itemView.findViewById(R.id.mechanic_shop);
            mechanic_image = itemView.findViewById(R.id.mechanic_image);
            btn_status = itemView.findViewById(R.id.btn_status);

        }
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
