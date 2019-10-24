package com.example.triptourguide.Models;

import java.util.List;

public class ConsulateModel {

    public ConsulateModel(String name, String address, String telephone, String fax, String homepage, List<String> jurisdiction) {
        Name = name;
        Address = address;
        Telephone = telephone;
        Fax = fax;
        Homepage = homepage;
        Jurisdiction = jurisdiction;
    }

    public String Name;

    public String Address;

    public String Telephone;

    public String Fax;

    public String Homepage;

    public List<String> Jurisdiction;
}
