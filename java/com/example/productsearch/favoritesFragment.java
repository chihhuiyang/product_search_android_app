package com.example.productsearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class favoritesFragment extends Fragment
{
    private static final String TAG = "favoriteFragment";

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor spEditor;


//    public static ListView mFavoriteListView;
    public static RecyclerView mRecyclerWishView;

    public static TextView num_wishlist;
    public static TextView total_cost;
    public double total_shopping_cost;

    public List<item> wishItem;

    private List<String> list_itemId;
    private List<String> list_productImg;
    private List<String> list_title;
    private List<String> list_zipcode;
    private List<String> list_shippingCost;
    private List<String> list_condition;
    private List<String> list_wish;
    private List<String> list_price;
    private List<String> list_jsonItem;

    public static TextView noFavoritesView;
    public ProgressDialog mProgressDialog;
    public Intent newIntent;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {

        Log.v(TAG, "Rainie: onCreateView()");
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        //setUserVisibleHint(true);

        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        mSharedPreferences = this.getActivity().getSharedPreferences("mySP", Context.MODE_PRIVATE);
        spEditor = mSharedPreferences.edit();

//        mFavoriteListView = (ListView)view.findViewById(R.id.favoriteList);
        mRecyclerWishView = (RecyclerView) view.findViewById(R.id.wishList2);
        num_wishlist = (TextView) view.findViewById(R.id.num_wishlist);
        total_cost = (TextView) view.findViewById(R.id.total_cost);

        noFavoritesView = (TextView)view.findViewById(R.id.noFavorites);
        newIntent = new Intent(this.getActivity(), favoritesFragment.class);

        getFavoriteList();
        return view;
    }

    public void onResume()
    {
        super.onResume();
        getFavoriteList();
    }

    public void setUserVisibleHint(boolean isVisibleToUser)
    {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser || isResumed())
        {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    public void getFavoriteList()
    {
        wishItem = new ArrayList<>();


        int numOfSP = mSharedPreferences.getAll().size();
        Log.v(TAG, "Rainie : numOfSP = " + numOfSP);

        if (numOfSP == 0)
        {
            noFavoritesView.setVisibility(View.VISIBLE);
//            mFavoriteListView.setVisibility(View.GONE);
            mRecyclerWishView.setVisibility(View.GONE);

        }
        else
        {
            noFavoritesView.setVisibility(View.GONE);
//            mFavoriteListView.setVisibility(View.VISIBLE);
            mRecyclerWishView.setVisibility(View.VISIBLE);


            num_wishlist.setText(String.valueOf(numOfSP));


            list_itemId = new ArrayList<>(numOfSP);
            list_productImg = new ArrayList<>(numOfSP);
            list_title = new ArrayList<>(numOfSP);
            list_zipcode = new ArrayList<>(numOfSP);
            list_shippingCost = new ArrayList<>(numOfSP);
            list_condition = new ArrayList<>(numOfSP);
            list_price = new ArrayList<>(numOfSP);
            list_wish = new ArrayList<>(numOfSP);
            list_jsonItem = new ArrayList<>(numOfSP);


            Map<String,?> keys = mSharedPreferences.getAll();

            int total_wishlist_cost = 0;
            String[] spElement;
            int index = 0;
            for(Map.Entry<String,?> entry : keys.entrySet())
            {
                Log.v(TAG, "Rainie : map values = " + entry.getKey() + " : " + entry.getValue());
                spElement = entry.getValue().toString().split(",");


                // be careful the first and last []
                list_itemId.add(index,      spElement[0].substring(2, spElement[0].length() - 1));
                list_productImg.add(index,  spElement[1].substring(1, spElement[1].length() - 1));
                list_title.add(index,       spElement[2].substring(1, spElement[2].length() - 1));
                list_zipcode.add(index,     spElement[3].substring(1, spElement[3].length() - 1));
                list_shippingCost.add(index,spElement[4].substring(1, spElement[4].length() - 1));
                list_condition.add(index,   spElement[5].substring(1, spElement[5].length() - 1));
                list_price.add(index,       spElement[6].substring(1, spElement[6].length() - 1));
                list_wish.add(index,        spElement[7].substring(1, spElement[7].length() - 1));
                list_jsonItem.add(index,    spElement[8].substring(1, spElement[8].length() - 2));


                double wish_cost = Double.parseDouble(spElement[6].substring(2, spElement[6].length() - 1)) * 100;
                int wish_cost_int = (int) wish_cost;
                Log.v(TAG, "Rainie : wish_cost_int = " + wish_cost_int);
                total_wishlist_cost += wish_cost_int;


                wishItem.add(new item(list_itemId.get(index), list_productImg.get(index), list_title.get(index), list_zipcode.get(index),
                        list_shippingCost.get(index), list_condition.get(index), list_price.get(index), list_wish.get(index), list_jsonItem.get(index)));
//                Log.v(TAG, "Rainie : wishItem = " + list_itemId.get(index));
//                Log.v(TAG, "Rainie : wishItem = " + list_productImg.get(index));
//                Log.v(TAG, "Rainie : wishItem = " + list_title.get(index));
//                Log.v(TAG, "Rainie : wishItem = " + list_zipcode.get(index));
//                Log.v(TAG, "Rainie : wishItem = " + list_shippingCost.get(index));
//                Log.v(TAG, "Rainie : wishItem = " + list_condition.get(index));
//                Log.v(TAG, "Rainie : wishItem = " + list_price.get(index));
//                Log.v(TAG, "Rainie : wishItem = " + list_wish.get(index));


                index++;
            }

            Log.v(TAG, "Rainie : total_wishlist_cost = " + total_wishlist_cost);
            total_shopping_cost = new Double(total_wishlist_cost) / 100.0;
            total_cost.setText("$" + Double.toString(total_shopping_cost));


            setAdapterForFavoriteListView();

//            mFavoriteListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
//            {
//                @Override
//                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
//                {
//                    newIntent = new Intent(getActivity(), DetailsActivity.class);
//                    requestDetails(placeId.get(position));
//                }
//            });

            // TODO : mRecyclerWishView listener
        }
    }

    public void setAdapterForFavoriteListView()
    {
        Log.v(TAG, "Rainie : setAdapterForFavoriteListView() : wishItem = " + wishItem.size());

        RecycleViewAdapterWish myAdapter = new RecycleViewAdapterWish(this.getActivity(), wishItem);
        mRecyclerWishView.setLayoutManager(new GridLayoutManager(this.getActivity(), 2));
        mRecyclerWishView.setAdapter(myAdapter);
    }

    public void requestDetails(String mPlaceId)
    {
        mProgressDialog = new ProgressDialog(this.getActivity());
        mProgressDialog.setMessage("Fetching details");
        mProgressDialog.show();

        String mUrl = "https://maps.googleapis.com/maps/api/place/details/json?";
        mUrl += "placeid=" + mPlaceId;
        mUrl += "&key=AIzaSyC9HBExGTftsTmeBjHXLucUi5NH2QXCQkY";
        System.out.println(mUrl);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this.getActivity());
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    newIntent.putExtra("jsonObj", jsonObject.toString());
                    getActivity().startActivity(newIntent);
                    mProgressDialog.dismiss();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        Toast.makeText(getActivity(), "No connection! Please check your internet connection.", Toast.LENGTH_SHORT).show();
                        System.out.println("Request error!");
                        System.out.println(error);
                    }
                });
        queue.add(stringRequest);
    }
}
