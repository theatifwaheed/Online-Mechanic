package com.onlinemachenic.utils.drawroutemap;

import android.content.Context;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

public class DrawRouteMaps
{
    private static DrawRouteMaps instance;
    private Context context;

    public static DrawRouteMaps getInstance(Context context2)
    {
        instance = new DrawRouteMaps();
        instance.context = context2;
        return instance;
    }

    public DrawRouteMaps draw(LatLng origin, LatLng destination, GoogleMap googleMap)
    {
        String url_route = FetchUrl.getUrl(origin, destination);
        new DrawRoute(googleMap).execute(url_route);
        return instance;
    }

    public static Context getContext()
    {
        return instance.context;
    }
}
