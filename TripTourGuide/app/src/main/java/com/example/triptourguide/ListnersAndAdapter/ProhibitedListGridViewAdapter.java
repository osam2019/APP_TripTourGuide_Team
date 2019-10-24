package com.example.triptourguide.ListnersAndAdapter;


import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.example.triptourguide.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProhibitedListGridViewAdapter extends BaseAdapter {

    private Context _context;
    private List<String> _itemlist = new ArrayList<>();
    public Boolean[] GridViewSelection;

    Map<String, Integer> itemToImageIdMap;

    public ProhibitedListGridViewAdapter(Context context, Set<String> prohibitedList) {
        _context = context;
        _itemlist.addAll(prohibitedList);
        setItemToImageIdMap();
        GridViewSelection = new Boolean[_itemlist.size()];
        for (int i = 0; i < GridViewSelection.length; i++)
            GridViewSelection[i] = false;
    }

    @Override
    public int getCount() {
        return _itemlist.size();
    }

    @Override
    public Object getItem(int position) {
        return _itemlist.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(_context);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        } else {
            imageView = (ImageView) convertView;
        }

        if (!itemToImageIdMap.containsKey(_itemlist.get(position))) {
            imageView.setImageResource(R.drawable.ic_launcher_background);
            return imageView;
        }

        imageView.setImageResource(itemToImageIdMap.get(_itemlist.get(position)));
        imageView.setBackgroundColor(GridViewSelection[position] ? Color.BLACK : Color.parseColor("#00ffffff"));
        return imageView;
    }

    private void setItemToImageIdMap() {
        itemToImageIdMap = new HashMap<>();
        itemToImageIdMap.put("alcohol", R.drawable.alcohol_50);
        itemToImageIdMap.put("army", R.drawable.army_50);
        itemToImageIdMap.put("artifact", R.drawable.artifact_50);
        itemToImageIdMap.put("cigarettes", R.drawable.cigarettes_50);
        itemToImageIdMap.put("drug", R.drawable.drug_50);
        itemToImageIdMap.put("firework", R.drawable.firework_50);
        itemToImageIdMap.put("gun", R.drawable.gun_50);
        itemToImageIdMap.put("lighter", R.drawable.lighter_50);
        itemToImageIdMap.put("paint", R.drawable.paint_50);
        itemToImageIdMap.put("pets", R.drawable.pets_50);
        itemToImageIdMap.put("pill", R.drawable.pill_50);
        itemToImageIdMap.put("porno", R.drawable.porno_50);
        itemToImageIdMap.put("printer", R.drawable.printer_50);
        itemToImageIdMap.put("seed", R.drawable.seed_50);
        itemToImageIdMap.put("soil", R.drawable.soil_50);
        itemToImageIdMap.put("wildlife", R.drawable.wildlife_50);
        itemToImageIdMap.put("gum", R.drawable.gum_50);
    }


}
