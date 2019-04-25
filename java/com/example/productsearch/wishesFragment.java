package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class wishesFragment extends Fragment {
    private static final String TAG = "wishesFragment";

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor spEditor;

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

    public static TextView noWishesView;
    public Intent newIntent;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v(TAG, "Rainie: onCreateView()");
        View view = inflater.inflate(R.layout.fragment_wishlist, container, false);
        //setUserVisibleHint(true);

        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        mSharedPreferences = this.getActivity().getSharedPreferences("mySP", Context.MODE_PRIVATE);
        spEditor = mSharedPreferences.edit();

        mRecyclerWishView = (RecyclerView) view.findViewById(R.id.wishList2);
        num_wishlist = (TextView) view.findViewById(R.id.num_wishlist);
        total_cost = (TextView) view.findViewById(R.id.total_cost);

        noWishesView = (TextView)view.findViewById(R.id.noWishes);
        newIntent = new Intent(this.getActivity(), wishesFragment.class);

        getWishList();
        return view;
    }

    public void onResume() {
        super.onResume();
        getWishList();
    }

    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser || isResumed()) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
        }
    }

    public void getWishList() {
        wishItem = new ArrayList<>();

        int count_sp = mSharedPreferences.getAll().size();
        Log.v(TAG, "Rainie : count_sp = " + count_sp);

        if (count_sp == 0) {
            noWishesView.setVisibility(View.VISIBLE);
//            mFavoriteListView.setVisibility(View.GONE);
            mRecyclerWishView.setVisibility(View.GONE);

        } else {
            noWishesView.setVisibility(View.GONE);
//            mFavoriteListView.setVisibility(View.VISIBLE);
            mRecyclerWishView.setVisibility(View.VISIBLE);

            num_wishlist.setText(String.valueOf(count_sp));

            list_itemId = new ArrayList<>(count_sp);
            list_productImg = new ArrayList<>(count_sp);
            list_title = new ArrayList<>(count_sp);
            list_zipcode = new ArrayList<>(count_sp);
            list_shippingCost = new ArrayList<>(count_sp);
            list_condition = new ArrayList<>(count_sp);
            list_price = new ArrayList<>(count_sp);
            list_wish = new ArrayList<>(count_sp);
            list_jsonItem = new ArrayList<>(count_sp);

            Map<String,?> keys = mSharedPreferences.getAll();

            int total_wishlist_cost = 0;
            String[] part;
            int index = 0;
            for(Map.Entry<String,?> entry : keys.entrySet()) {
//                Log.v(TAG, "Rainie : map values = " + entry.getKey() + " : " + entry.getValue());

                Gson gson = new Gson();
                String array = entry.getValue().toString();
                part = gson.fromJson(array, String[].class);
//
//                for (int i = 0; i < part.length; i++) {
//                    Log.v(TAG, "Rainie : part[" + i + "] : " + part[i]);
//                }

                list_itemId.add(index,      part[0]);
                list_productImg.add(index,  part[1]);
                list_title.add(index,       part[2]);
                list_zipcode.add(index,     part[3]);
                list_shippingCost.add(index,part[4]);
                list_condition.add(index,   part[5]);
                list_price.add(index,       part[6]);
                list_wish.add(index,        part[7]);
                list_jsonItem.add(index,    part[8]);

                double wish_cost = Double.parseDouble(part[6].substring(1)) * 100;
                int wish_cost_int = (int) wish_cost;
//                Log.v(TAG, "Rainie : wish_cost_int = " + wish_cost_int);
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
//                Log.v(TAG, "Rainie : wishItem = " + list_jsonItem.get(index));

                index++;
            }

//            Log.v(TAG, "Rainie : total_wishlist_cost = " + total_wishlist_cost);
            total_shopping_cost = new Double(total_wishlist_cost) / 100.0;
            total_cost.setText("$" + Double.toString(total_shopping_cost));


            setAdapterForWishesView();

        }
    }

    public void setAdapterForWishesView() {
//        Log.v(TAG, "Rainie : setAdapterForWishesView() : wishItem = " + wishItem.size());
        RecycleViewAdapterWish myAdapter = new RecycleViewAdapterWish(this.getActivity(), wishItem);
        mRecyclerWishView.setLayoutManager(new GridLayoutManager(this.getActivity(), 2));
        mRecyclerWishView.setAdapter(myAdapter);
    }

}
