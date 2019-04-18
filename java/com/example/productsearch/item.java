package com.example.productsearch;

public class item {

    private String PlaceId;
    private String Icon;



    private String Name;
    private String Address;
    private String Favorite;

    public item() {

    }


    public item(String placeId, String icon, String name, String address, String favorite) {
        PlaceId = placeId;
        Icon = icon;
        Name = name;
        Address = address;
        Favorite = favorite;
    }


    public String getPlaceId() {
        return PlaceId;
    }

    public String getIcon() {
        return Icon;
    }

    public String getName() {
        return Name;
    }

    public String getAddress() {
        return Address;
    }

    public String getFavorite() {
        return Favorite;
    }


    public void setPlaceId(String placeId) {
        PlaceId = placeId;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public void setName(String name) {
        Name = name;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public void setFavorite(String favorite) {
        Favorite = favorite;
    }
}
