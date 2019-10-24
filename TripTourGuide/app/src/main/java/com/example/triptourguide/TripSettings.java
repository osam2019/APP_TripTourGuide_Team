package com.example.triptourguide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.borax12.materialdaterangepicker.date.DatePickerDialog;
import com.example.triptourguide.ListnersAndAdapter.ActivityListGridViewAdapter;
import com.example.triptourguide.Models.CityTripEntity;
import com.hbb20.CountryCodePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TripSettings extends AppCompatActivity {


    CountryCodePicker ccp;
    Spinner statespinner;
    Spinner cityspinner;
    Map<String, Map<String, List<String>>> countryToState = new HashMap<>();
    Map<String, List<String>> cityactivity = new HashMap<>();
    String pickedcountry;
    String pickedstate;
    String pickedcity;
    List<String> selectedstate;
    List<String> selectedcity;
    List<String> selectedactivities;
    String[] citydata;
    String[] activitydata;
    Context _context;

    GridView activityListGridView;
    TextView dateRageTextView;
    TextView textView;
    Calendar startDate = Calendar.getInstance();
    Calendar endDate = Calendar.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_settings);

        _context = this;
        citydata = new String[0];
        activitydata = new String[0];
        ccp = findViewById(R.id.ccp);
        textView = findViewById(R.id.select_activity_text);
        textView.setText("");
        dateRageTextView = findViewById(R.id.date_range_text);
        dateRageTextView.setOnClickListener(new DateRangeViewListner());
        statespinner = findViewById(R.id.statespinner);
        final ArrayAdapter<String> stateadapter = new ArrayAdapter<>(this, R.layout.spinner_row, new ArrayList<String>());
        statespinner.setAdapter(stateadapter);
        statespinner.setPrompt("주를 선택하세요");
        cityspinner = findViewById(R.id.cityspinner);
        final ArrayAdapter<String> cityadapter = new ArrayAdapter<String>(this, R.layout.spinner_row, citydata);
        cityspinner.setAdapter(cityadapter);
        cityspinner.setPrompt("도시를 선택하세요");

        StateListener stateListener = new StateListener();
        statespinner.setOnItemSelectedListener(stateListener);


        activityListGridView = findViewById(R.id.activity_list_grid);
        ActivityListGridViewAdapter adapter = new ActivityListGridViewAdapter(this, new ArrayList<String>());
        activityListGridView.setAdapter(adapter);
        activityListGridView.setOnItemClickListener(new ActivityListGridListener(adapter));

        CityListener cityListener = new CityListener();
        cityspinner.setOnItemSelectedListener(cityListener);

        try {
            JSONArray countryCityNamesJson = new JSONArray(TripUtils.ReadFileFromAsset(_context, "CityNames.json"));
            for (int i = 0; i < countryCityNamesJson.length(); i++) {

                JSONObject country = countryCityNamesJson.getJSONObject(i);
                String countryName = country.getString("name");

                JSONObject state = country.getJSONObject("states");

                Iterator<String> keys = state.keys();

                countryToState.put(countryName, new HashMap<String, List<String>>());

                while(keys.hasNext()) {
                    Map<String, List<String>> stateMap = countryToState.get(countryName);
                    String stateName = keys.next();
                    JSONArray cities = state.getJSONArray(stateName);
                    List<String> cityList = new ArrayList<>();
                    for (int j = 0; j < cities.length(); j++) {
                        cityList.add(cities.getString(j));
                    }
                    stateMap.put(stateName, cityList);
                }

            }

        } catch (JSONException e) {
            Log.d("error found", "error found");
        }

        try {
            JSONArray CityActivityJson = new JSONArray(TripUtils.ReadFileFromAsset(this, "CityActivityList.json"));
            for (int i = 0; i < CityActivityJson.length(); i++) {

                JSONObject city = CityActivityJson.getJSONObject(i);
                String cityName = city.getString("name");
                JSONArray cityListArr = city.getJSONArray("activityList");
                List<String> cityList = new ArrayList<>();
                for (int j = 0; j < cityListArr.length(); j++) {
                    cityList.add(cityListArr.getString(j));
                }
                cityactivity.put(cityName, cityList);
            }

        } catch (JSONException e) {
            Log.d("error found", "error found");
        }


        ccp.setOnCountryChangeListener(new CountryCodePicker.OnCountryChangeListener() {
            @Override
            public void onCountrySelected() {
                pickedcountry = ccp.getSelectedCountryName();
                selectedstate = new ArrayList<>();
                Set<String> statesSet = new HashSet<>();
                if (countryToState.containsKey(pickedcountry))
                    statesSet = countryToState.get(pickedcountry).keySet();

                for (String state : statesSet) {
                    selectedstate.add(state);
                }
                String[] stateSections = selectedstate.toArray(new String[selectedstate.size()]);
                Arrays.sort(stateSections);
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(_context, R.layout.spinner_row, stateSections);
                statespinner.setAdapter(stateAdapter);
            }
        });


    }

    public void addCityBtn(View view) {
        if (pickedcity == null || pickedcity == null ) {
            Toast.makeText(_context, "모든정보를 기입하셔야 합니다 ㅠㅠ", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        ActivityListGridViewAdapter activityAdapter = (ActivityListGridViewAdapter) activityListGridView.getAdapter();
        intent.putExtra("newCity", new CityTripEntity(pickedcountry, pickedcity, startDate, endDate, activityAdapter.GetActiveActivities()));
        setResult(RESULT_OK, intent);
        finish();
    }

    class StateListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            pickedstate = (String) statespinner.getSelectedItem();
            selectedcity = new ArrayList<>();
            selectedcity = countryToState.get(pickedcountry).get(pickedstate);
            citydata = selectedcity.toArray(new String[selectedcity.size()]);
            ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(_context, R.layout.spinner_row, citydata);
            cityspinner.setAdapter(cityAdapter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    }

    class CityListener implements AdapterView.OnItemSelectedListener{
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            pickedcity = (String) cityspinner.getSelectedItem();
            selectedactivities = new ArrayList<>();
            if(cityactivity.get(pickedcity) == null)
                return;
            selectedactivities = cityactivity.get(pickedcity);

            ActivityListGridViewAdapter adapter = new ActivityListGridViewAdapter(_context, selectedactivities);
            ((ActivityListGridListener) activityListGridView.getOnItemClickListener()).ResetAdapter(adapter);
            activityListGridView.setAdapter(adapter);
            textView.setText("도시에서 어떤 여행을 하나요?");
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }

    class ActivityListGridListener implements AdapterView.OnItemClickListener {

        ActivityListGridViewAdapter _adapter;
        public ActivityListGridListener(ActivityListGridViewAdapter adapter) {
            _adapter = adapter;
        }
        public void ResetAdapter(ActivityListGridViewAdapter adapter){
            _adapter = adapter;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (_adapter.GridViewSelection[position]) {
                _adapter.GridViewSelection[position] = false;
                view.setBackgroundColor(Color.GRAY);
            } else {
                _adapter.GridViewSelection[position] = true;
                view.setBackgroundColor(Color.YELLOW);
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    activityListGridView.setAdapter(_adapter);
                }
            });
        }
    }

    class DateRangeViewListner implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Calendar now = Calendar.getInstance();
            DatePickerDialog dpd = DatePickerDialog.newInstance(
                    new DateRangeSelectListener(),
                    now.get(Calendar.YEAR),
                    now.get(Calendar.MONTH),
                    now.get(Calendar.DAY_OF_MONTH)
            );
            dpd.show(getFragmentManager(), "Datepickerdialog");
        }
    }

    class DateRangeSelectListener implements DatePickerDialog.OnDateSetListener {

        @Override
        public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth, int yearEnd, int monthOfYearEnd, int dayOfMonthEnd) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date strDate = sdf.parse(dayOfMonth + "/" + monthOfYear + "/" + year);
                if (sdf.parse(dayOfMonthEnd + "/" + monthOfYearEnd + "/" + yearEnd).before(strDate)) {
                    Toast.makeText(_context, "여행종료시점이 여행시작시점보다 후여야 합니다.", Toast.LENGTH_SHORT).show();
                    dateRageTextView.setText("set date");
                    return;
                }
            } catch (ParseException e) {
                Toast.makeText(_context, "에러가 발생했습니다!!!!", Toast.LENGTH_LONG).show();
                return;
            }

            startDate.set(year, monthOfYear, dayOfMonth);
            endDate.set(yearEnd, monthOfYearEnd, dayOfMonthEnd);
            String dateStr = year + "." + monthOfYear  + "." + dayOfMonth + " - " + yearEnd + "." + monthOfYearEnd + "." + dayOfMonthEnd;
            dateRageTextView.setText(dateStr);
        }
    }

}
