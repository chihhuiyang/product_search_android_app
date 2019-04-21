package com.example.productsearch;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
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

//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.location.places.PlacePhotoMetadata;
//import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
//import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
//import com.google.android.gms.location.places.PlacePhotoResponse;
//import com.google.android.gms.location.places.Places;
//import com.google.android.gms.tasks.OnCompleteListener;
//import com.google.android.gms.tasks.Task;

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

//    public String jsonObject;
//    public JSONObject placeDetails;
//    public GeoDataClient mGeoDataClient;
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
//        mGeoDataClient = Places.getGeoDataClient(this.getActivity(), null);

        Bundle bundle;
        bundle = this.getArguments();
//        jsonObject = bundle.getString("jsonObj");
        jsonObject_detail_str = bundle.getString("jsonObject_detail");
        jsonObjectItem_str = bundle.getString("jsonObjectItem");    // 1 item from 50 api
        jsonObject_photo_str = bundle.getString("jsonObject_photo");

        Log.v(TAG, "Rainie: jsonObject_photo_str = " + jsonObject_photo_str);

        try
        {
//            JSONObject myJsonObject = new JSONObject(jsonObject);
//            placeDetails = myJsonObject.getJSONObject("result");
//            String placeId = placeDetails.getString("place_id");
//            getPhotos(placeId);

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
                        Log.v(TAG, "Rainie : link = " + link);


                        View pic_view = minflater.inflate(R.layout.gallery_item, mPhotoBox, false);
                        ImageView img = (ImageView) pic_view.findViewById(R.id.id_index_gallery_item_image);
                        Picasso.get().load(link).into(img);
                        mPhotoBox.addView(pic_view);


//                                    ImageView mImageView = new ImageView(getActivity());
//                                    mImageView.setImageBitmap(bitmap);
//                                    mPhotoBox.addView(mImageView);
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






    // Request photos and metadata for the specified place.
    private void getPhotos(String mPlaceId)
    {


//        final String placeId = mPlaceId;
//        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
//        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>()
//        {
//            @Override
//            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task)
//            {
//                // Get the list of photos.
//                PlacePhotoMetadataResponse photos = task.getResult();
//                // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
//                PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
//                // Get the first photo in the list.
//
//                if (photoMetadataBuffer.getCount() == 0)
//                {
//                    mLinearLayout.setVisibility(View.GONE);
//                    noPhotos.setVisibility(View.VISIBLE);
//                }
//                else
//                {
//                    mLinearLayout.setVisibility(View.VISIBLE);
//                    noPhotos.setVisibility(View.GONE);
//                    for (int i = 0; i < photoMetadataBuffer.getCount(); i++)
//                    {
//                        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i);
//                        // Get the attribution text.
//                        CharSequence attribution = photoMetadata.getAttributions();
//                        // Get a full-size bitmap for the photo.
//                        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
//                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>()
//                        {
//                            @Override
//                            public void onComplete(@NonNull Task<PlacePhotoResponse> task)
//                            {
//                                try
//                                {
//                                    PlacePhotoResponse photo = task.getResult();
//                                    Bitmap bitmap = photo.getBitmap();
//
//                                    int width = Resources.getSystem().getDisplayMetrics().widthPixels;
//                                    int height = (int) (width / bitmap.getWidth() * bitmap.getHeight());
//                                    bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
//
//                                    ImageView mImageView = new ImageView(getActivity());
//                                    mImageView.setImageBitmap(bitmap);
//                                    mPhotoBox.addView(mImageView);
//                                }
//                                catch (Exception e)
//                                {
//                                    e.printStackTrace();
//                                }
//                            }
//                        });
//                    }
//                }
//            }
//        });
    }
}
