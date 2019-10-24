package com.example.triptourguide.Models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.List;

public class CityTripEntity implements Serializable {

    public String CountryName;

    public String CityName;

    public Calendar StartDate;

    public Calendar EndDate;

    public List<String> ActivityList;

    public CityTripEntity(String countryName, String cityName, Calendar startDate, Calendar endDate, List<String> activityList) {
        CountryName = countryName;
        CityName = cityName;
        StartDate = startDate;
        EndDate = endDate;
        ActivityList = activityList;
    }



}
