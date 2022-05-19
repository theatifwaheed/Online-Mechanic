package com.onlinemachenic.models;

/**
 * Created by Mubashar Hussain on 05/05/2021.
 **/
public class User
{
    private String user_id, name, pic_url, phone_number, email, password, notification_token;
    private double latitude, longitude;

    public User()
    {
    }


    public User(String user_id, String name, String pic_url, String phone_number, String email, String password, String notification_token, double latitude, double longitude)
    {
        this.user_id = user_id;
        this.name = name;
        this.pic_url = pic_url;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.notification_token = notification_token;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getUser_id()
    {
        return user_id;
    }

    public void setUser_id(String user_id)
    {
        this.user_id = user_id;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getPic_url()
    {
        return pic_url;
    }

    public void setPic_url(String pic_url)
    {
        this.pic_url = pic_url;
    }

    public String getNotification_token()
    {
        return notification_token;
    }

    public void setNotification_token(String notification_token)
    {
        this.notification_token = notification_token;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public String getPhone_number()
    {
        return phone_number;
    }

    public void setPhone_number(String phone_number)
    {
        this.phone_number = phone_number;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }
}
