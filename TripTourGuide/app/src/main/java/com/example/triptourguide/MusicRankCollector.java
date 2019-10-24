package com.example.triptourguide;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicRankCollector {


    private static String _apiToken = "8439ef1fc99f75fca22c2618a809cf87";

    private static String baseUrl = "http://ws.audioscrobbler.com/2.0/?method=geo.gettoptracks";

    private static String queryBuilder(String country) {
        return baseUrl + "&country=" + country.replace(" ", "%20") + "&api_key=" + _apiToken + "&format=json";

    }

    private static JSONObject GetMusicRankJson(String country) {
        String requestQuery = queryBuilder(country);
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(requestQuery));
            StatusLine statusLine = response.getStatusLine();
            if(statusLine.getStatusCode() == HttpStatus.SC_OK){
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                response.getEntity().writeTo(out);
                String responseString = out.toString();
                out.close();
                return new JSONObject(responseString);
            } else{
                response.getEntity().getContent().close();
            }

        } catch (IOException e) {
            e.printStackTrace();

        } catch (JSONException e) {

        }
        return new JSONObject();
    }

    public static List<String> GetCountryMusicRank(String country) {
        List<String> musicList = new ArrayList<>();
        try {
            JSONArray tracks = GetMusicRankJson(country).getJSONObject("tracks").getJSONArray("track");
            int maxTrack = Math.min(tracks.length(), 20);
            for (int i = 0; i < maxTrack; i++) {
                JSONObject track = tracks.getJSONObject(i);
                musicList.add(track.getString("name"));
            }
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }
        return musicList;
    }



}
