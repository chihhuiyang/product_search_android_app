package com.example.productsearch;


import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class photosFragment extends Fragment
{
    private static final String TAG = "photosFragment";


    public String jsonObject_photo_str;
    public JSONObject jsonObject_photo;

    public String jsonObjectItem_str;   // from 50 api , and only 1 item
    public JSONObject jsonObjectItem;   // from 50 api , and only 1 item

    public String jsonObject_detail_str;
    public JSONObject jsonObject_detail_item;

    public LinearLayout mLinearLayout;
    public LinearLayout mPhotoBox;
    public TextView noPhotos;

    public LayoutInflater minflater;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        minflater = inflater;

        Log.v(TAG, "Rainie : onCreateView()");

        View view = inflater.inflate(R.layout.fragment_photos, container, false);

        mLinearLayout = (LinearLayout)view.findViewById(R.id.imageLayout);
        mPhotoBox = (LinearLayout)view.findViewById(R.id.photoBox);
        noPhotos = (TextView) view.findViewById(R.id.noPhotos);

        Bundle bundle;
        bundle = this.getArguments();
//        jsonObject = bundle.getString("jsonObj");
//        jsonObject_detail_str = bundle.getString("jsonObject_detail");
//        jsonObjectItem_str = bundle.getString("jsonObjectItem");    // 1 item from 50 api
        jsonObject_photo_str = bundle.getString("jsonObject_photo");

        Log.v(TAG, "Rainie: jsonObject_photo_str = " + jsonObject_photo_str);

        try
        {

            jsonObject_photo = new JSONObject(jsonObject_photo_str);
            if (jsonObject_photo.has("items")) {
                JSONArray jsonObject_photo_arr = jsonObject_photo.getJSONArray("items");
                int count = jsonObject_photo_arr.length();
                if (count == 0) {
                    mLinearLayout.setVisibility(View.GONE);
                    noPhotos.setVisibility(View.VISIBLE);
                } else {
                    for (int i = 0; i < count; i++) {
                        String link = jsonObject_photo_arr.getJSONObject(i).getString("link");
//                        Log.v(TAG, "Rainie : link = " + link);


                        View pic_view = minflater.inflate(R.layout.photo_item, mPhotoBox, false);
                        ImageView img = (ImageView) pic_view.findViewById(R.id.photo_item);
                        Picasso.get().load(link).into(img);
                        mPhotoBox.addView(pic_view);

                    }
                }
            } else {
                mLinearLayout.setVisibility(View.GONE);
                noPhotos.setVisibility(View.VISIBLE);
            }

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return view;
    }

}
