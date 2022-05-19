package com.onlinemachenic.utils.drawroutemap;

import android.os.AsyncTask;
import android.util.Log;

import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.onlinemachenic.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RouteDrawerTask extends AsyncTask<String, Integer, List<List<HashMap<String, String>>>>
{
    private PolylineOptions lineOptions;
    private GoogleMap mMap;
    private int routeColor;

    public RouteDrawerTask(GoogleMap mMap2)
    {
        this.mMap = mMap2;
    }

    /* access modifiers changed from: protected */
    public List<List<HashMap<String, String>>> doInBackground(String... jsonData)
    {
        List<List<HashMap<String, String>>> routes = null;
        try
        {
            JSONObject jObject = new JSONObject(jsonData[0]);
            Log.d("RouteDrawerTask", jsonData[0]);
            DataRouteParser parser = new DataRouteParser();
            Log.d("RouteDrawerTask", parser.toString());
            routes = parser.parse(jObject);
            Log.d("RouteDrawerTask", "Executing routes");
            Log.d("RouteDrawerTask", routes.toString());
            return routes;
        } catch (Exception e)
        {
            Log.d("RouteDrawerTask", e.toString());
            e.printStackTrace();
            return routes;
        }
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(List<List<HashMap<String, String>>> result)
    {
        if (result != null)
        {
            drawPolyLine(result);
        }
    }

    private void drawPolyLine(List<List<HashMap<String, String>>> result)
    {
        this.lineOptions = null;
        for (int i = 0; i < result.size(); i++)
        {
            ArrayList<LatLng> points = new ArrayList<>();
            this.lineOptions = new PolylineOptions();
            List<HashMap<String, String>> path = result.get(i);
            for (int j = 0; j < path.size(); j++)
            {
                HashMap<String, String> point = path.get(j);
                points.add(new LatLng(Double.parseDouble(point.get("lat")), Double.parseDouble(point.get("lng"))));
            }
            this.lineOptions.addAll(points);
            this.lineOptions.width(6.0f);
            this.routeColor = ContextCompat.getColor(DrawRouteMaps.getContext(), R.color.colorRouteLine);
            if (this.routeColor == 0)
            {
                this.lineOptions.color(-16085240);
            }
            else
            {
                this.lineOptions.color(this.routeColor);
            }
        }
        if (this.lineOptions == null || this.mMap == null)
        {
            Log.d("onPostExecute", "without Polylines draw");
        }
        else
        {
            this.mMap.addPolyline(this.lineOptions);
        }
    }
}
