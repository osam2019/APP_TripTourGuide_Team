package com.example.triptourguide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.triptourguide.ListnersAndAdapter.TripNameClickedListener;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {


    private static String _currentTripName = "";
    WheelView wheelView;
    Button newTrip;
    public static SQLiteDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DBopenHelper dbHelper = new DBopenHelper(this);
        db = dbHelper.getWritableDatabase();

        wheelView = findViewById(R.id.tripPicker);
        wheelView.setWheelClickable(true);
        wheelView.setOnWheelItemClickListener(new TripNameClickedListener(this));
        wheelView.setWheelAdapter(new ArrayWheelAdapter(this));
        wheelView.setSkin(WheelView.Skin.Common);
        wheelView.setWheelData(GetTripList(db));
        wheelView.setWheelSize(5);
        wheelView.setBackgroundColor(Color.parseColor("#00ffffff"));
        wheelView.setAlpha(0.7f);

        WheelView.WheelViewStyle wheelStyle= new WheelView.WheelViewStyle();
        wheelStyle.selectedTextColor = Color.BLACK;
        wheelStyle.textColor = Color.GRAY;
        wheelStyle.selectedTextSize = 20;
        wheelView.setStyle(wheelStyle);

        newTrip = findViewById(R.id.new_trip);

    }

    public void newTripBtn(View view) {
        Intent intent = new Intent(this, TripCreator.class);
        startActivityForResult(intent, 2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ((BaseAdapter) wheelView.getAdapter()).notifyDataSetChanged();
    }

    public List<String> GetTripList(SQLiteDatabase db) {

        Set<String> tripSet = new HashSet<>();
        Cursor c = db.rawQuery("select * from Trip", null);
        while (c.moveToNext()) {
            int nameInd = c.getColumnIndex("name");
            String tripName = c.getString(nameInd);
            tripSet.add(tripName);
        }
        List<String> tripList = new ArrayList<>();
        tripList.addAll(tripSet);
        return tripList;
    }

    public static String GetCurrentTripName() {
        return _currentTripName;
    }

    public static void SetCurrentTripName(String tripName) {
        _currentTripName = tripName;
    }

}
