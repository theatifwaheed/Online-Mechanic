package com.onlinemachenic.utils.drawroutemap;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataRouteParser
{
    public List<List<HashMap<String, String>>> parse(JSONObject jObject)
    {
        List<List<HashMap<String, String>>> routes = new ArrayList<>();
        try
        {
            JSONArray jRoutes = jObject.getJSONArray("routes");
            for (int i = 0; i < jRoutes.length(); i++)
            {
                JSONArray jLegs = ((JSONObject) jRoutes.get(i)).getJSONArray("legs");
                List path = new ArrayList();
                for (int j = 0; j < jLegs.length(); j++)
                {
                    JSONArray jSteps = ((JSONObject) jLegs.get(j)).getJSONArray("steps");
                    for (int k = 0; k < jSteps.length(); k++)
                    {
                        List<LatLng> list = decodePoly((String) ((JSONObject) ((JSONObject) jSteps.get(k)).get("polyline")).get("points"));
                        for (int l = 0; l < list.size(); l++)
                        {
                            HashMap<String, String> hm = new HashMap<>();
                            hm.put("lat", Double.toString(list.get(l).latitude));
                            hm.put("lng", Double.toString(list.get(l).longitude));
                            path.add(hm);
                        }
                    }
                    routes.add(path);
                }
            }
        } catch (JSONException e)
        {
            e.printStackTrace();
        } catch (Exception e2)
        {
            e2.printStackTrace();
        }
        return routes;
    }

    private List<LatLng> decodePoly(String encoded)
    {
        int index;
        List<LatLng> poly = new ArrayList<>();
        int index2 = 0;
        int len = encoded.length();
        int lat = 0;
        int lng = 0;
        while (index2 < len)
        {
            int shift = 0;
            int result = 0;
            while (true)
            {
                index = index2 + 1;
                int b = encoded.charAt(index2) - '?';
                result |= (b & 31) << shift;
                shift += 5;
                if (b < 32)
                {
                    break;
                }
                index2 = index;
            }
            lat += (result & 1) != 0 ? (result >> 1) ^ -1 : result >> 1;
            int shift2 = 0;
            int result2 = 0;
            while (true)
            {
                index++;
                int b2 = encoded.charAt(index) - '?';
                result2 |= (b2 & 31) << shift2;
                shift2 += 5;
                if (b2 < 32)
                {
                    break;
                }
            }
            lng += (result2 & 1) != 0 ? (result2 >> 1) ^ -1 : result2 >> 1;
            poly.add(new LatLng(((double) lat) / 100000.0d, ((double) lng) / 100000.0d));
            index2 = index;
        }
        return poly;
    }
}
