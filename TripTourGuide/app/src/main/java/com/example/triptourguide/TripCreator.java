package com.example.triptourguide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.triptourguide.ListnersAndAdapter.TripCreatorListAdapter;
import com.example.triptourguide.Models.CityTripEntity;

import java.util.ArrayList;
import java.util.List;

public class TripCreator extends AppCompatActivity {

    ListView cityTripListView;
    Button newTripButton;

    List<CityTripEntity> cityTripList = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip_creator);

        cityTripListView = findViewById(R.id.trip_list);
        newTripButton = findViewById(R.id.create_city_button);

    }

    public void addTripButton(View view) {
        Intent intent = new Intent(this, TripSettings.class);
        startActivityForResult(intent, 1);
    }

    public void composeTripButton(View view) {
        final View dialogView = LayoutInflater.from(this).inflate(R.layout.trip_name_dialog, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("새로운 여행 이름");
        alertDialog.setCancelable(false);
        alertDialog.setMessage("새로운 여행의 이름을 정하세요!");
        final Context activity = this;

        final EditText tripNameText = dialogView.findViewById(R.id.trip_name_text);

        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (tripNameText.getText().toString().isEmpty()) {
                    Toast.makeText(activity, "올바른 이름을 입력하세요!!", Toast.LENGTH_SHORT);
                    return;
                }

                DBopenHelper dbHelper = new DBopenHelper(activity);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                dbHelper.CreateNewTrip(db, cityTripList, tripNameText.getText().toString());
                finish();
            }
        });


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alertDialog.dismiss();
            }
        });


        alertDialog.setView(dialogView);
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        CityTripEntity cityTripEntity = null;
        if (requestCode == 1) {
            if(resultCode == RESULT_OK) {
               cityTripEntity = (CityTripEntity) data.getSerializableExtra("newCity");
            }

            cityTripList.add(cityTripEntity);

            TripCreatorListAdapter adapter = new TripCreatorListAdapter(this);
            adapter.UpdateCityTripEntity(cityTripList);
            cityTripListView.setAdapter(adapter);

        }

    }
}
