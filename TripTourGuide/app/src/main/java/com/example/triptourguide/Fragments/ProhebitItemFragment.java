package com.example.triptourguide.Fragments;


import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.example.triptourguide.ListnersAndAdapter.ProhibitedListGridViewAdapter;
import com.example.triptourguide.Models.CityTripEntity;
import com.example.triptourguide.R;
import com.example.triptourguide.TripServiceProvider;
import com.example.triptourguide.TripUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProhebitItemFragment extends Fragment {

    static GridView ProhibitedItemGridView;
    private TextView _prohibitTextView;
    Map<String, List<String>> countryToProhItemMap = new HashMap<>();
    Map<String, String> itemToDescriptionMap = new HashMap<>();
    Set<String> loadprohitem;

    public ProhebitItemFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        loadprohitem = new HashSet<>();
        View rootView = inflater.inflate(R.layout.fragment_prohebit_item, container, false);
        ProhibitedItemGridView = rootView.findViewById(R.id.prohibited_item_gridview);
        _prohibitTextView = rootView.findViewById(R.id.prohibit_item_return_text);
        _prohibitTextView.setClickable(true);
        _prohibitTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PrepItemFragment prepItemFragment = new PrepItemFragment(TripServiceProvider.conditionToItemsMap, TripServiceProvider.chosenActivities);
                FragmentTransaction itemPrepFt = (getActivity()).getSupportFragmentManager().beginTransaction();
                itemPrepFt.replace(R.id.item_provider_container, prepItemFragment);
                itemPrepFt.commit();
            }
        });
        try {
            JSONArray CountryProhibitedJson = new JSONArray(TripUtils.ReadFileFromAsset(getActivity(), "CountryProhibited.json"));
            for (int i = 0; i < CountryProhibitedJson.length(); i++) {

                JSONObject country = CountryProhibitedJson.getJSONObject(i);
                String countryname = country.getString("name");
                JSONArray prohitemlistArr = country.getJSONArray("prohibited");
                List<String> prohitemlist = new ArrayList<>();
                for (int j = 0; j < prohitemlistArr.length(); j++) {
                    prohitemlist.add(prohitemlistArr.getString(j));
                }
                countryToProhItemMap.put(countryname, prohitemlist);

            }

        } catch (JSONException e) {
            Log.d("error found", "error found");
        }

        try {
            JSONArray DescriptionJson = new JSONArray(TripUtils.ReadFileFromAsset(getActivity(), "ProhibitDescription.json"));
            for (int i = 0; i < DescriptionJson.length(); i++) {

                JSONObject item = DescriptionJson.getJSONObject(i);
                String itemname = item.getString("name");
                String description = item.getString("description");
                itemToDescriptionMap.put(itemname, description);

            }

        } catch (JSONException e) {
            Log.d("error found", "error found");
        }

        for (CityTripEntity cityTripEntity : TripServiceProvider.CityTripEntityList) {
            if (!countryToProhItemMap.containsKey(cityTripEntity.CountryName))
                continue;
            loadprohitem.addAll(countryToProhItemMap.get(cityTripEntity.CountryName));
        }
        ProhibitedListGridViewAdapter adapter = new ProhibitedListGridViewAdapter(getActivity(), loadprohitem);
        ProhibitedItemGridView.setAdapter(adapter);
        ProhibitedItemGridView.setOnItemClickListener(new ProhibitedListGridListener(adapter,itemToDescriptionMap));
        return rootView;
    }

    public static class ProhibitedListGridListener implements AdapterView.OnItemClickListener {

        ProhibitedListGridViewAdapter _adapter;
        private Map<String, String> _prohebitedItemToReason;

        public ProhibitedListGridListener(ProhibitedListGridViewAdapter adapter, Map<String, String> prohebitedItemToReason) {
            _adapter = adapter;
            _prohebitedItemToReason = prohebitedItemToReason;
        }

        public void ResetAdapter(ProhibitedListGridViewAdapter adapter) {
            _adapter = adapter;
        }


        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Context context = view.getContext();
            String clickedText = ProhibitedItemGridView.getItemAtPosition(position).toString();
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(clickedText);
            String iconid = clickedText+"_50";
            Class<R.drawable> drawable = R.drawable.class;
            Field field = null;
            try {
                field = drawable.getField( iconid );
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            int r = 0;
            try {
                r = field.getInt(null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            builder.setMessage(_prohebitedItemToReason.get(clickedText));
            builder.setIcon(r);
            builder.setPositiveButton("확인", null);
            builder.show();
        }
    }

}
