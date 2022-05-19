package com.onlinemachenic.utils.drawroutemap;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class DrawRoute extends AsyncTask<String, Void, String>
{
    private GoogleMap mMap;

    public DrawRoute(GoogleMap mMap2)
    {
        this.mMap = mMap2;
    }

    /* access modifiers changed from: protected */
    public String doInBackground(String... url)
    {
        String data = BuildConfig.FLAVOR;
        try
        {
            data = getJsonRoutePoint(url[0]);
            Log.d("Background Task data", data);
            return data;
        } catch (Exception e)
        {
            Log.d("Background Task", e.toString());
            return data;
        }
    }

    /* access modifiers changed from: protected */
    public void onPostExecute(String result)
    {
        super.onPostExecute((String) result);
        new RouteDrawerTask(this.mMap).execute(result);
    }

    private String getJsonRoutePoint(String strUrl) throws IOException
    {
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try
        {
            urlConnection = (HttpURLConnection) new URL(strUrl).openConnection();
            urlConnection.connect();
            iStream = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));
            StringBuffer sb = new StringBuffer();
            while (true)
            {
                String line = br.readLine();
                if (line != null)
                {
                    sb.append(line);
                }
                else
                {
                    String data = sb.toString();
                    Log.d("getJsonRoutePoint", data.toString());
                    br.close();
                    return data;
                }
            }
        } catch (Exception e)
        {
            Log.d("Exception", e.toString());
            return BuildConfig.FLAVOR;
        } finally
        {
            iStream.close();
            urlConnection.disconnect();
        }
    }
}
