package com.example.triptourguide.Fragments;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.triptourguide.DBopenHelper;
import com.example.triptourguide.MainActivity;
import com.example.triptourguide.Models.CityTripEntity;
import com.example.triptourguide.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 */
public class MarkedMapFragment extends Fragment implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Context _context;

    public MarkedMapFragment(Context context) {
        _context = context;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v1 = inflater.inflate(R.layout.fragment_marked_map, container, false);
        return v1;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        DBopenHelper dbHelper = new DBopenHelper(_context);
        List<CityTripEntity> entityList = dbHelper.RetrieveTripDetail(MainActivity.db, MainActivity.GetCurrentTripName());

        Geocoder geocoder = new Geocoder(_context);
        for (CityTripEntity cityTripEntity : entityList) {
            try {
                List<Address> address = geocoder.getFromLocationName(cityTripEntity.CityName, 3);
                Address matchedAddress = address.get(0);
                for (Address ad : address) {
                    if (ad.getCountryName().equalsIgnoreCase(cityTripEntity.CountryName)) {
                        matchedAddress = ad;
                        break;
                    }
                }
                LatLng targetLoc = new LatLng(matchedAddress.getLatitude(), matchedAddress.getLongitude());
                mMap.addMarker(new MarkerOptions().position(targetLoc).title("Marker in " + cityTripEntity.CityName));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(targetLoc));

            } catch (IOException e) {}
        }

    }
}
