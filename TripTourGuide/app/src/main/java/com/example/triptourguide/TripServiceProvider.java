package com.example.triptourguide;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.triptourguide.Fragments.MarkedMapFragment;
import com.example.triptourguide.Fragments.PrepItemFragment;
import com.example.triptourguide.Fragments.YoutubeFragmentX;
import com.example.triptourguide.Models.CityTripEntity;
import com.example.triptourguide.Models.ConsulateModel;
import com.google.android.gms.maps.SupportMapFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TripServiceProvider extends AppCompatActivity {

    public static Map<String, Set<String>> conditionToItemsMap = new HashMap<>();
    Map<String, ConsulateModel> countryToConsulateMap = new HashMap<>();
    public static Set<String> chosenActivities;
    public static List<CityTripEntity> CityTripEntityList;
    Map<String, List<String>> countryToItemsMap = new HashMap<>();
    List<String> list;
    YoutubeFragmentX youtubeFragmentX;
    MarkedMapFragment mapFragment;


    private String getCountryItemJson(String countryName) {
        return TripUtils.ReadFileFromAsset(this, countryName + "Prepare.json");
    }

    private String getCountryConsulateJson(String countryName) {
        return TripUtils.ReadFileFromAsset(this, countryName + "Consulate.json");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travel_item_provider);
        populateCountryToCosulateMap();
        String tripName = getIntent().getStringExtra("tripName");
        DBopenHelper dbHelper = new DBopenHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        CityTripEntityList = dbHelper.RetrieveTripDetail(db, tripName);

        populateConditionToItemMap(CityTripEntityList);

        chosenActivities = new HashSet<>();
        chosenActivities.add("common");
        for (CityTripEntity cityTripEntity : CityTripEntityList)
            chosenActivities.addAll(cityTripEntity.ActivityList);

        Set<String> items = new HashSet<>();
        for (String condition : chosenActivities) {
            if (conditionToItemsMap.containsKey(condition))
                items.addAll(conditionToItemsMap.get(condition));
        }

        list = countryToItemsMap.get("US");

        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment fragment = new PrepItemFragment(conditionToItemsMap, chosenActivities);
        ft.replace(R.id.item_provider_container, fragment);
        ft.commit();
        youtubeFragmentX = new YoutubeFragmentX();
        mapFragment = new MarkedMapFragment(this);

    }

    private void populateCountryToCosulateMap() {
        String[] builtInConsulateCountry = new String[]{"Singapore", "United States", "Canada"};
        for (String countryName : builtInConsulateCountry) {
            try {
                JSONArray countryConsulateJson = new JSONArray(getCountryConsulateJson(countryName));
                for (int i = 0; i < countryConsulateJson.length(); i++) {
                    JSONObject contryConsulateJson = countryConsulateJson.getJSONObject(i);
                    String city = contryConsulateJson.getString("name");
                    String address = contryConsulateJson.getString("adress");
                    String telephone = contryConsulateJson.getString("telephone");
                    String fax = contryConsulateJson.getString("fax");
                    String homepage = contryConsulateJson.getString("homepage");
                    JSONArray consulateListArr = contryConsulateJson.getJSONArray("jurisdiction");
                    List<String> jurisdictions = new ArrayList<>();
                    for (int j = 0; j < consulateListArr.length(); j++) {
                        jurisdictions.add(consulateListArr.getString(j));
                    }
                    countryToConsulateMap.put(countryName, new ConsulateModel(city, address, telephone, fax, homepage, jurisdictions));
                }
            } catch(JSONException e) {
                Log.d("error found", "error found");
            }
        }
    }

    private String getConsulateString() {
        Set<String> countries = new HashSet<>();
        for (CityTripEntity cityTripEntity : TripServiceProvider.CityTripEntityList) {
            countries.add(cityTripEntity.CountryName);
        }
        List<ConsulateModel> consulateModels = new ArrayList<>();
        for (String countryName : countries) {
            if (countryToConsulateMap.containsKey(countryName))
                consulateModels.add(countryToConsulateMap.get(countryName));
        }
        StringBuilder sb = new StringBuilder();
        for (ConsulateModel consulateModel : consulateModels) {
            sb.append("도시 : " + consulateModel.Name + "\n");
            sb.append("주소 : " + consulateModel.Address);
            sb.append("\n전화번호 : " + consulateModel.Telephone);
            sb.append("\n홈페이지 : " + consulateModel.Homepage + "\n\n");

        }
        return sb.toString();
    }


    private void populateConditionToItemMap(List<CityTripEntity> cityTripEntityList) {
        for (CityTripEntity cityTripEntity : cityTripEntityList) {
            try {
                JSONArray cityPrepareJson = new JSONArray(getCountryItemJson(cityTripEntity.CityName));
                for (int i = 0; i < cityPrepareJson.length(); i++) {
                    JSONObject object = cityPrepareJson.getJSONObject(i);
                    String condition = object.getString("name");
                    JSONArray supplyListArr = object.getJSONArray("prepareList");
                    if (!conditionToItemsMap.containsKey(condition))
                        conditionToItemsMap.put(condition, new HashSet<String>());
                    for (int j = 0; j < supplyListArr.length(); j++) {
                        conditionToItemsMap.get(condition).add(supplyListArr.getString(j));
                    }
                }
            } catch(JSONException e) {
                Log.d("error found", "error found");
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.content_trip_option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.pre_trip:
                FragmentTransaction itemPrepFt = getSupportFragmentManager().beginTransaction();
                Fragment itemPrepFragment = new PrepItemFragment(conditionToItemsMap, chosenActivities);
                itemPrepFt.replace(R.id.item_provider_container, itemPrepFragment);
                itemPrepFt.commit();
                break;

            case R.id.in_trip:
                FragmentTransaction youtubeFt = getSupportFragmentManager().beginTransaction();
                youtubeFt.replace(R.id.item_provider_container, youtubeFragmentX);
                youtubeFt.commit();

                break;

            case R.id.post_trip:
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.item_provider_container, mapFragment);
                ft.commit();
                getSupportFragmentManager().executePendingTransactions();
                SupportMapFragment fragment = (SupportMapFragment)mapFragment.getChildFragmentManager().findFragmentById(R.id.marked_map_fragment);
                fragment.getMapAsync(mapFragment);
                break;

            case R.id.country_consulate:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("영사관 정보");
                builder.setIcon(R.drawable.consulate_50);
                builder.setMessage(getConsulateString());
                builder.setPositiveButton("확인", null);
                builder.show();

        }
        return super.onOptionsItemSelected(item);
    }

}
