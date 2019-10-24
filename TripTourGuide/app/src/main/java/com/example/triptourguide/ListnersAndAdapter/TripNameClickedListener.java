package com.example.triptourguide.ListnersAndAdapter;

import android.content.Context;
import android.content.Intent;

import com.example.triptourguide.MainActivity;
import com.example.triptourguide.TripServiceProvider;
import com.wx.wheelview.widget.WheelView;

public class TripNameClickedListener implements WheelView.OnWheelItemClickListener {

    private Context _context;

    public TripNameClickedListener(Context mainContext) {
        _context = mainContext;
    }

    @Override
    public void onItemClick(int position, Object o) {
        MainActivity.SetCurrentTripName((String) o);
        Intent intent = new Intent(_context, TripServiceProvider.class);
        intent.putExtra("tripName", (String) o);
        _context.startActivity(intent);
    }
}
