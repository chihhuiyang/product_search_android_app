package com.example.productsearch;

import android.util.Log;

public class item {

    public String TAG = "item";

    private String itemId;
    private String productImg;
    private String title;
    private String zipcode;
    private String shippingCost;
    private String condition;
    private String price;
    private String wish;
    private String jsonObjItem_str;

    public item(String _itemId, String _productImg, String _title, String _zipcode, String _shippingCost, String _condition, String _price, String _wish, String _jsonObjItem_str) {
        itemId = _itemId;
        productImg = _productImg;
        title = _title;
        zipcode = _zipcode;
        shippingCost = _shippingCost;
        condition = _condition;
        price = _price;
        wish = _wish;
        jsonObjItem_str = _jsonObjItem_str;
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

    public String getJsonObjItem_str() {
        Log.v(TAG, "Rainie : getJsonObjItem_str() = " + jsonObjItem_str);
        return jsonObjItem_str;
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

    public void setJsonObjItem_str(String jsonObjItem_str) {
        this.jsonObjItem_str = jsonObjItem_str;
    }

}
