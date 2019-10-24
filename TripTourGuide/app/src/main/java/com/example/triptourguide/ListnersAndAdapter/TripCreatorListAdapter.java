package com.example.triptourguide.ListnersAndAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import android.widget.TextView;

import com.example.triptourguide.Models.CityTripEntity;
import com.example.triptourguide.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;


public class TripCreatorListAdapter extends BaseAdapter {


    public List<CityTripEntity> CityTripEntity = new ArrayList<>();
    private Context _context;

    public TripCreatorListAdapter(Context context) {
        _context = context;

    }

    public void UpdateCityTripEntity(List<CityTripEntity> cityTripEntity) {
        CityTripEntity = cityTripEntity;
    }


    @Override
    public int getCount() {
        return CityTripEntity.size();
    }

    @Override
    public Object getItem(int i) {
        return CityTripEntity.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        View targetView = view;
        if (view == null) {
            CityTripEntity cte = CityTripEntity.get(i);
            LayoutInflater inflater= LayoutInflater.from(_context);
            targetView = inflater.inflate(R.layout.city_trip_view_layout, null);
            TextView cityNameView = targetView.findViewById(R.id.city_name);
            cityNameView.setText(cte.CityName);

            ((TextView) targetView.findViewById(R.id.country_name)).setText(cte.CountryName);
            ((TextView) targetView.findViewById(R.id.trip_date_range_textview)).setText(
                    new SimpleDateFormat("yyyy-MM-dd").format(cte.StartDate.getTime()) + " - " +
                            new SimpleDateFormat("yyyy-MM-dd").format(cte.EndDate.getTime())
            );

        }

        return targetView;
    }
}
