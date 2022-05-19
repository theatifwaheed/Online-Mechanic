package com.onlinemachenic.models;

/**
 * Created by Mubashar Hussain on 07/05/2021.
 **/
public class Feedback
{
    private String feedback_id, sender_id, date, text;

    public Feedback()
    {
    }

    public Feedback(String feedback_id, String sender_id, String date, String text)
    {
        this.feedback_id = feedback_id;
        this.sender_id = sender_id;
        this.date = date;
        this.text = text;
    }

    public String getFeedback_id()
    {
        return feedback_id;
    }

    public void setFeedback_id(String feedback_id)
    {
        this.feedback_id = feedback_id;
    }

    public String getSender_id()
    {
        return sender_id;
    }

    public void setSender_id(String sender_id)
    {
        this.sender_id = sender_id;
    }

    public String getDate()
    {
        return date;
    }

    public void setDate(String date)
    {
        this.date = date;
    }

    public String getText()
    {
        return text;
    }

    public void setText(String text)
    {
        this.text = text;
    }
}
