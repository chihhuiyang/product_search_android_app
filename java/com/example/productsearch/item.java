package com.example.productsearch;

public class item {


    private String itemId;
    private String productImg;
    private String title;
    private String zipcode;
    private String shippingCost;
    private String condition;
    private String price;
    private String wish;

    public item(String _itemId, String _productImg, String _title, String _zipcode, String _shippingCost, String _condition, String _price, String _wish) {
        itemId = _itemId;
        productImg = _productImg;
        title = _title;
        zipcode = _zipcode;
        shippingCost = _shippingCost;
        condition = _condition;
        price = _price;
        wish = _wish;
    }

    public String getItemId() {
        return itemId;
    }

    public String getProductImg() {
        return productImg;
    }

    public String getTitle() {
        return title;
    }

    public String getZipcode() {
        return zipcode;
    }

    public String getShippingCost() {
        return shippingCost;
    }

    public String getCondition() {
        return condition;
    }

    public String getPrice() {
        return price;
    }

    public String getWish() {
        return wish;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public void setProductImg(String productImg) {
        this.productImg = productImg;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }

    public void setShippingCost(String shippingCost) {
        this.shippingCost = shippingCost;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setWish(String wish) {
        this.wish = wish;
    }


    //    private String PlaceId;
//    private String Icon;
//    private String Name;
//    private String Address;
//    private String Favorite;

//    public item(String placeId, String icon, String name, String address, String favorite) {
//        PlaceId = placeId;
//        Icon = icon;
//        Name = name;
//        Address = address;
//        Favorite = favorite;
//    }


//    public String getPlaceId() {
//        return PlaceId;
//    }
//
//    public String getIcon() {
//        return Icon;
//    }
//
//    public String getName() {
//        return Name;
//    }
//
//    public String getAddress() {
//        return Address;
//    }
//
//    public String getFavorite() {
//        return Favorite;
//    }
//
//
//    public void setPlaceId(String placeId) {
//        PlaceId = placeId;
//    }
//
//    public void setIcon(String icon) {
//        Icon = icon;
//    }
//
//    public void setName(String name) {
//        Name = name;
//    }
//
//    public void setAddress(String address) {
//        Address = address;
//    }
//
//    public void setFavorite(String favorite) {
//        Favorite = favorite;
//    }
}
