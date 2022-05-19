package com.onlinemachenic.utils.drawroutemap;

import com.google.android.gms.maps.model.LatLng;

public class FetchUrl
{
    public static String getUrl(LatLng origin, LatLng dest)
    {
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        return "https://maps.googleapis.com/maps/api/directions/" + "json" + "?" + (str_origin + "&" + ("destination=" + dest.latitude + "," + dest.longitude) + "&" + "sensor=false");
    }
}
