package com.onlinemachenic.models;

/**
 * Created by Mubashar Hussain on 06/05/2021.
 **/
public class MechanicRequest
{
    private String request_id, mechanic_id,  request_sender, date, text, status;
    private String reason;
    private String vehicle_type;

    public MechanicRequest()
    {

    }

    public MechanicRequest(String request_id, String mechanic_id, String request_sender, String date, String text, String status, String reason)
    {
        this.request_id = request_id;
        this.mechanic_id = mechanic_id;
        this.request_sender = request_sender;
        this.date = date;
        this.text = text;
        this.status = status;
        this.reason = reason;
    }

    public MechanicRequest(String request_id, String mechanic_id, String request_sender, String date, String text, String status)
    {
        this.request_id = request_id;
        this.mechanic_id = mechanic_id;
        this.request_sender = request_sender;
        this.date = date;
        this.text = text;
        this.status = status;
    }

    public MechanicRequest(String request_id, String mechanic_id, String request_sender, String date, String text, String status, String reason, String vehicle_type)
    {
        this.request_id = request_id;
        this.mechanic_id = mechanic_id;
        this.request_sender = request_sender;
        this.date = date;
        this.text = text;
        this.status = status;
        this.reason = reason;
        this.vehicle_type = vehicle_type;
    }

    public String getRequest_id()
    {
        return request_id;
    }

    public void setRequest_id(String request_id)
    {
        this.request_id = request_id;
    }

    public String getRequest_sender()
    {
        return request_sender;
    }

    public void setRequest_sender(String request_sender)
    {
        this.request_sender = request_sender;
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

    public String getStatus()
    {
        return status;
    }

    public void setStatus(String status)
    {
        this.status = status;
    }

    public String getMechanic_id()
    {
        return mechanic_id;
    }

    public void setMechanic_id(String mechanic_id)
    {
        this.mechanic_id = mechanic_id;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        this.reason = reason;
    }

    public String getVehicle_type()
    {
        return vehicle_type;
    }

    public void setVehicle_type(String vehicle_type)
    {
        this.vehicle_type = vehicle_type;
    }
}
