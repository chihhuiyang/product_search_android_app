package com.example.productsearch;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;

import static java.lang.Float.parseFloat;
import static java.lang.Integer.parseInt;

public class infoFragment extends Fragment
{
    private static final String TAG = "infoFragment";

//    product_img_gallery

    public String jsonObjectItem_str;   // from 50 api , and only 1 item
    public JSONObject jsonObjectItem;   // from 50 api , and only 1 item

    public String jsonObject_detail_str;
    public JSONObject jsonObject_detail_item;


    public HorizontalScrollView horizontalScrollView;
    public LinearLayout mGallery;


    public TextView product_title;
    public TextView product_price;
    public TextView product_ship;

    public View product_divider1;

    public ImageView product_highlight_img;
    public TextView product_highlight_txt;
    public LinearLayout product_highlight_subtitle; // could be N/A
    public TextView product_highlight_subtitle_name;
    public TextView product_highlight_subtitle_value;
    public TextView product_highlight_price_name;
    public TextView product_highlight_price_value;
    public LinearLayout product_highlight_brand; // could be N/A
    public TextView product_highlight_brand_name;
    public TextView product_highlight_brand_value;

    public View product_divider2;

    public ImageView product_spec_img;
    public TextView product_spec_txt;


    public String[] specList_value;
    public ListView specListView;

    public LayoutInflater minflater;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        minflater = inflater;

        View view = inflater.inflate(R.layout.fragment_info, container, false);

        Log.v(TAG, "Rainie : onCreateView()");

        horizontalScrollView = (HorizontalScrollView)view.findViewById(R.id.horizontal_scroll_gallery);
        mGallery = (LinearLayout) view.findViewById(R.id.product_img_gallery);

        product_title = (TextView)view.findViewById(R.id.product_title);
        product_price = (TextView)view.findViewById(R.id.product_price);
        product_ship = (TextView)view.findViewById(R.id.product_ship);

        product_divider1 = (View)view.findViewById(R.id.product_divider1);

        product_highlight_img = (ImageView)view.findViewById(R.id.product_highlight_img);
        product_highlight_txt = (TextView)view.findViewById(R.id.product_highlight_txt);
        product_highlight_subtitle = (LinearLayout)view.findViewById(R.id.product_highlight_subtitle);
        product_highlight_subtitle_name = (TextView)view.findViewById(R.id.product_highlight_subtitle_name);
        product_highlight_subtitle_value = (TextView)view.findViewById(R.id.product_highlight_subtitle_value);
        product_highlight_price_name = (TextView)view.findViewById(R.id.product_highlight_price_name);
        product_highlight_price_value = (TextView)view.findViewById(R.id.product_highlight_price_value);
        product_highlight_brand = (LinearLayout)view.findViewById(R.id.product_highlight_brand);
        product_highlight_brand_name = (TextView)view.findViewById(R.id.product_highlight_brand_name);
        product_highlight_brand_value = (TextView)view.findViewById(R.id.product_highlight_brand_value);

        product_divider2 = (View)view.findViewById(R.id.product_divider2);
        product_spec_img = (ImageView)view.findViewById(R.id.product_spec_img);
        product_spec_txt = (TextView)view.findViewById(R.id.product_spec_txt);

        specListView = (ListView)view.findViewById(R.id.specList);


        Bundle bundle;
        bundle = this.getArguments();
        jsonObject_detail_str = bundle.getString("jsonObject_detail");
        jsonObjectItem_str = bundle.getString("jsonObjectItem");    // 1 item from 50 api

        Log.v(TAG, "Rainie: jsonObject_detail_str = " + jsonObject_detail_str);
        Log.v(TAG, "Rainie: jsonObjectItem_str = " + jsonObjectItem_str);

        try
        {
            JSONObject jsonObject_detail = new JSONObject(jsonObject_detail_str);
            jsonObject_detail_item = jsonObject_detail.getJSONObject("Item");

            jsonObjectItem = new JSONObject(jsonObjectItem_str);    // 1 item from 50 api


            generateDetails();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return view;
    }



    public void generateDetails() throws JSONException
    {
        Log.v(TAG, "Rainie : generateDetails()");


        if (jsonObject_detail_item.has("PictureURL")) {
            int count = jsonObject_detail_item.getJSONArray("PictureURL").length();
            String[] pic_urls = new String[count];
            for (int i = 0; i < count; i++) {
                pic_urls[i] = jsonObject_detail_item.getJSONArray("PictureURL").getString(i);

//                Log.v(TAG, "Rainie : pic_urls = " + pic_urls[i]);

                View view = minflater.inflate(R.layout.gallery_item, mGallery, false);
                ImageView img = (ImageView) view.findViewById(R.id.id_index_gallery_item_image);
                Picasso.get().load(pic_urls[i]).into(img);
                mGallery.addView(view);
            }
            horizontalScrollView.setVisibility(View.VISIBLE);
        }

        if (jsonObject_detail_item.has("Title")) {
            String title = jsonObject_detail_item.getString("Title");
            product_title.setText(title);
        }

        String price = "$";
        if (jsonObject_detail_item.has("CurrentPrice")) {
            double price_double = jsonObject_detail_item.getJSONObject("CurrentPrice").getDouble("Value");
            price = price + Double.toString(price_double);
            product_price.setText(price);
        }

        if (!jsonObjectItem.isNull("shippingInfo")) {
            if (!jsonObjectItem.getJSONArray("shippingInfo").getJSONObject(0).isNull("shippingServiceCost")) {
                if (!jsonObjectItem.getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost").getJSONObject(0).isNull("__value__")) {
                    String shippingCost = jsonObjectItem.getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost").getJSONObject(0).getString("__value__");
                    double shippingCost_double = Double.parseDouble(shippingCost);
                    if (shippingCost_double == 0.0) {
                        shippingCost = "With Free Shipping";
                    } else {
                        shippingCost = "With $" + shippingCost + " Shipping";
                    }
                    product_ship.setText(shippingCost);
                    product_ship.setVisibility(View.VISIBLE);
                }
            }
        }

        product_highlight_img.setImageResource(R.drawable.information);

        if (jsonObject_detail_item.has("Subtitle")) {
            String subtitle = jsonObject_detail_item.getString("Subtitle");
            setVisible(product_highlight_subtitle_name, product_highlight_subtitle_value, subtitle);
        }

        product_highlight_price_value.setText(price);


        if (jsonObject_detail_item.has("ItemSpecifics")) {
            if (jsonObject_detail_item.getJSONObject("ItemSpecifics").has("NameValueList")) {
                JSONArray jsonArray_specs = jsonObject_detail_item.getJSONObject("ItemSpecifics").getJSONArray("NameValueList");

                specList_value = new String[jsonArray_specs.length()];

                for (int i = 0; i < jsonArray_specs.length(); i++) {
                    String name = jsonArray_specs.getJSONObject(i).getString("Name");
                    String val = jsonArray_specs.getJSONObject(i).getJSONArray("Value").getString(0);
                    specList_value[i] = "・" + val;
                    if (name.equals("Brand")) {
                        product_highlight_brand.setVisibility(View.VISIBLE);
                        setVisible(product_highlight_brand_name, product_highlight_brand_value, val);
                    }
                }

                product_spec_img.setImageResource(R.drawable.wrench);
                product_divider2.setVisibility(View.VISIBLE);
                product_spec_img.setVisibility(View.VISIBLE);
                product_spec_txt.setVisibility(View.VISIBLE);

                // setAdapterForListView();
                spec_item spec_itemAdapter = new spec_item(this.getActivity(), specList_value);
                specListView.setAdapter(spec_itemAdapter);

                voidsetListViewHeightBasedOnChildren(specListView); // adjust listView height

            }

        }


    }

    public void setVisible(TextView title, TextView content, String text)
    {
        Log.v(TAG, "Rainie : setVisible()");

        title.setVisibility(View.VISIBLE);
        content.setVisibility(View.VISIBLE);
        content.setText(text);
    }


    // adjust listview height
    public void voidsetListViewHeightBasedOnChildren(ListView listView)
    {
        ListAdapter listAdapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++)
        {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height= totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1)) + 200;
        listView.setLayoutParams(params);
    }
}
