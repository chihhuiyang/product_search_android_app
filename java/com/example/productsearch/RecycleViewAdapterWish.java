package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

import static com.example.productsearch.wishesFragment.mRecyclerWishView;
import static com.example.productsearch.wishesFragment.noWishesView;
import static com.example.productsearch.wishesFragment.num_wishlist;
import static com.example.productsearch.wishesFragment.total_cost;


public class RecycleViewAdapterWish extends RecyclerView.Adapter<RecycleViewAdapterWish.MyViewHolder> {
    private static final String TAG = "RecycleViewAdapterWish";

    private Context mContext;
    private List<item> mData;

    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor spEditor;
    private String[] saveStr;
    public double total_shopping_cost;

    public RecycleViewAdapterWish(Context mContext, List<item> mData) {
        mSharedPreferences = mContext.getSharedPreferences("mySP", Context.MODE_PRIVATE);
//        Log.v(TAG, "Rainie : RecycleViewAdapterWish() : mSharedPreferences.getAll().size() = " + mSharedPreferences.getAll().size());
        spEditor = mSharedPreferences.edit();

        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public RecycleViewAdapterWish.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item_wish, viewGroup, false);
        return new RecycleViewAdapterWish.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecycleViewAdapterWish.MyViewHolder myViewHolder, final int i) {
//        Log.v(TAG, "Rainie : onBindViewHolder() : " );

        Picasso.get().load(mData.get(i).getProductImg()).error(mContext.getDrawable(R.drawable.image_outline)).into(myViewHolder.img_item_img);
//        Picasso.get().load(mData.get(i).getProductImg()).into(myViewHolder.img_item_img);
        myViewHolder.tv_item_title.setText(mData.get(i).getTitle().toUpperCase());
        myViewHolder.tv_item_zip.setText(mData.get(i).getZipcode());
        myViewHolder.tv_item_ship.setText(mData.get(i).getShippingCost());
        myViewHolder.tv_item_condition.setText(mData.get(i).getCondition());
        myViewHolder.tv_item_price.setText(mData.get(i).getPrice());

        // add wish list
        Picasso.get().load(mData.get(i).getWish()).into(myViewHolder.img_item_wish);
        if (mData.get(i).getWish() == "no") {
            myViewHolder.img_item_wish.setImageResource(R.drawable.cart_plus);
        } else {
            myViewHolder.img_item_wish.setImageResource(R.drawable.cart_remove);
        }
        myViewHolder.img_item_wish.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        deleteFromFavorite(i);
                    }
                }
        );


        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);

                intent.putExtra("itemId", mData.get(i).getItemId());
                intent.putExtra("title", mData.get(i).getTitle());
                intent.putExtra("card_product_img", mData.get(i).getProductImg());
                intent.putExtra("card_zipcode", mData.get(i).getZipcode());
                intent.putExtra("card_shipping_cost", mData.get(i).getShippingCost());
                intent.putExtra("card_condition", mData.get(i).getCondition());
                intent.putExtra("card_price", mData.get(i).getPrice());
                intent.putExtra("card_wish", mData.get(i).getWish());
                intent.putExtra("jsonObjItem_str", mData.get(i).getJsonObjItem_str());

//                Log.v(TAG, "Rainie : itemId = " + mData.get(i).getItemId());
//                Log.v(TAG, "Rainie : title = " + mData.get(i).getTitle());
//                Log.v(TAG, "Rainie : card_product_img = " + mData.get(i).getProductImg());
//                Log.v(TAG, "Rainie : card_zipcode = " + mData.get(i).getZipcode());
//                Log.v(TAG, "Rainie : card_shipping_cost = " + mData.get(i).getShippingCost());
//                Log.v(TAG, "Rainie : card_condition = " + mData.get(i).getCondition());
//                Log.v(TAG, "Rainie : card_price = " + mData.get(i).getPrice());
//                Log.v(TAG, "Rainie : card_wish = " + mData.get(i).getWish());
//                Log.v(TAG, "Rainie : jsonObjItem_str = " + mData.get(i).getJsonObjItem_str());

                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        } else {
            return mData.size();
        }
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        ImageView img_item_img;
        TextView tv_item_title;
        TextView tv_item_zip;
        TextView tv_item_ship;
        TextView tv_item_condition;
        TextView tv_item_price;
        ImageView img_item_wish;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview_id_wish);

            img_item_img = (ImageView) itemView.findViewById(R.id.item_img_id_wish);
            tv_item_title = (TextView) itemView.findViewById(R.id.item_title_id_wish);
            tv_item_zip = (TextView) itemView.findViewById(R.id.item_zip_id_wish);
            tv_item_ship = (TextView) itemView.findViewById(R.id.item_ship_id_wish);
            tv_item_condition = (TextView) itemView.findViewById(R.id.item_condition_id_wish);
            tv_item_price  = (TextView) itemView.findViewById(R.id.item_price_id_wish);
            img_item_wish = (ImageView) itemView.findViewById(R.id.item_wish_id_wish);
        }
    }


    public void deleteFromFavorite(int position) {
        Toast.makeText(mContext, mData.get(position).getTitle() + " was removed from wish list", Toast.LENGTH_SHORT).show();
//        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getItemId());
//        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getTitle());
//        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getWish());
//        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getPrice());
//        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getCondition());
//        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getShippingCost());
//        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getZipcode());
//        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getProductImg());

        spEditor.remove(mData.get(position).getItemId());
        spEditor.apply();

        // update total_shopping_cost
        int count_sp = mSharedPreferences.getAll().size();
        Log.v(TAG, "Rainie : After deleteFromFavorite() : count_sp = " + count_sp);
        Map<String,?> keys = mSharedPreferences.getAll();

        int total_wishlist_cost = 0;
        String[] part;
        for(Map.Entry<String,?> entry : keys.entrySet()) {
            Log.v(TAG, "Rainie : map values = " + entry.getKey() + " : " + entry.getValue());

            Gson gson = new Gson();
            String array = entry.getValue().toString();
            part = gson.fromJson(array, String[].class);

            double wish_cost = Double.parseDouble(part[6].substring(1)) * 100;

            int wish_cost_int = (int) wish_cost;
//            Log.v(TAG, "Rainie : wish_cost_int = " + wish_cost_int);
            total_wishlist_cost += wish_cost_int;
        }
        total_shopping_cost = new Double(total_wishlist_cost) / 100.0;
        total_cost.setText("$" + Double.toString(total_shopping_cost));

        // update total number
        num_wishlist.setText(String.valueOf(count_sp));

        mData.remove(position);
        
        if (mData.size() == 0) {
            noWishesView.setVisibility(View.VISIBLE);
            mRecyclerWishView.setVisibility(View.GONE);
        }

        this.notifyDataSetChanged();
    }

}
