package com.onlinemachenic.models;

/**
 * Created by Mubashar Hussain on 05/05/2021.
 **/
public class Mechanic
{
    private String mechanic_id, name, shop_name, phone_number, email, password, pic_url, notification_token;
    private double latitude, longitude;
    private boolean available, approved;

    public Mechanic()
    {

    }

    public Mechanic(String mechanic_id, String name, String shop_name, String phone_number, String email, String password, String pic_url, double latitude, double longitude, boolean available)
    {
        this.mechanic_id = mechanic_id;
        this.name = name;
        this.shop_name = shop_name;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.pic_url = pic_url;
        this.latitude = latitude;
        this.longitude = longitude;
        this.available = available;
    }

    public Mechanic(String mechanic_id, String name, String shop_name, String phone_number, String email, String password, String pic_url, String notification_token, boolean available)
    {
        this.mechanic_id = mechanic_id;
        this.name = name;
        this.shop_name = shop_name;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.pic_url = pic_url;
        this.notification_token = notification_token;
        this.available = available;
    }

    public Mechanic(String mechanic_id, String name, String shop_name, String phone_number, String email, String password, String pic_url, boolean available)
    {
        this.mechanic_id = mechanic_id;
        this.name = name;
        this.shop_name = shop_name;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.pic_url = pic_url;
        this.available = available;
    }

    public Mechanic(String mechanic_id, String name, String shop_name, String phone_number, String email, String password, String pic_url, String notification_token, double latitude, double longitude, boolean available)
    {
        this.mechanic_id = mechanic_id;
        this.name = name;
        this.shop_name = shop_name;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.pic_url = pic_url;
        this.notification_token = notification_token;
        this.latitude = latitude;
        this.longitude = longitude;
        this.available = available;
    }

    public Mechanic(String mechanic_id, String name, String shop_name, String phone_number, String email, String password, String pic_url, String notification_token, double latitude, double longitude, boolean available, boolean approved)
    {
        this.mechanic_id = mechanic_id;
        this.name = name;
        this.shop_name = shop_name;
        this.phone_number = phone_number;
        this.email = email;
        this.password = password;
        this.pic_url = pic_url;
        this.notification_token = notification_token;
        this.latitude = latitude;
        this.longitude = longitude;
        this.available = available;
        this.approved = approved;
    }

    public String getMechanic_id()
    {
        return mechanic_id;
    }

    public void setMechanic_id(String mechanic_id)
    {
        this.mechanic_id = mechanic_id;
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

    public boolean isAvailable()
    {
        return available;
    }

    public void setAvailable(boolean available)
    {
        this.available = available;
    }

    public String getShop_name()
    {
        return shop_name;
    }

    public void setShop_name(String shop_name)
    {
        this.shop_name = shop_name;
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

    public boolean isApproved()
    {
        return approved;
    }

    public void setApproved(boolean approved)
    {
        this.approved = approved;
    }
}
