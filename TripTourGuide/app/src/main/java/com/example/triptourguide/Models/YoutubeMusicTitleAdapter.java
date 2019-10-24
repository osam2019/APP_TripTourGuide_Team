package com.example.triptourguide.Models;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.triptourguide.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YoutubeMusicTitleAdapter implements ListAdapter {


    public int Current_Position = 0;
    private List<String> _musicTitleList;
    private Context _context;
    private int _backGroundColor = Color.parseColor("#804A98CC");
    private Map<Integer, View> _viewRecyclerMap = new HashMap<>();

    public YoutubeMusicTitleAdapter(List<String> musicTitleList, Context context) {
        _musicTitleList = musicTitleList;
        _context = context;
    }


    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public int getCount() {
        return _musicTitleList.size();
    }

    @Override
    public Object getItem(int i) {
        return _musicTitleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    public View getViewAt(int position) {
        return _viewRecyclerMap.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(!_viewRecyclerMap.containsKey(position)) {
            LayoutInflater vi = LayoutInflater.from(_context);
            convertView = vi.inflate(R.layout.music_title_row, null);
            ((TextView) convertView.findViewById(R.id.music_title)).setText(_musicTitleList.get(position));
            _viewRecyclerMap.put(position, convertView);
        }
        convertView = _viewRecyclerMap.get(position);

        return convertView;
    }

    @Override
    public int getItemViewType(int i) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return _musicTitleList.isEmpty();
    }


    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int i) {
        return false;
    }


}
