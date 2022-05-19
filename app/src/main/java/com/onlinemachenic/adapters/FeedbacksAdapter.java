package com.onlinemachenic.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onlinemachenic.R;
import com.onlinemachenic.models.Feedback;
import com.onlinemachenic.models.User;

import static com.onlinemachenic.helpers.StaticConfig.allUsersList;
import static com.onlinemachenic.helpers.StaticConfig.myFeedbacksList;

/**
 * Created by Mubashar Hussain on 06/05/2021.
 **/
public class FeedbacksAdapter extends RecyclerView.Adapter<FeedbacksAdapter.RecyclerViewHolder>
{
    private Context context;

    public FeedbacksAdapter(Context context)
    {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feedback, parent, false);


        RecyclerViewHolder recyclerViewHolder = new RecyclerViewHolder(view);
        return recyclerViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position)
    {
        Feedback feedback = myFeedbacksList.get(position);
        holder.feedback_sender.setText(getSenderDetailsByID(feedback.getSender_id()).getName());
        holder.feedback_text.setText(feedback.getText());
        holder.tv_date.setText(feedback.getDate());
    }


    @Override
    public int getItemCount()
    {
        return myFeedbacksList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        TextView    feedback_sender,
                    tv_date,
                    feedback_text;


        public RecyclerViewHolder(@NonNull View itemView)
        {
            super(itemView);

            feedback_sender = itemView.findViewById(R.id.feedback_sender);
            tv_date = itemView.findViewById(R.id.tv_date);
            feedback_text = itemView.findViewById(R.id.feedback_text);


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
