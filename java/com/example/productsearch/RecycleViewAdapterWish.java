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

import static com.example.productsearch.favoritesFragment.mRecyclerWishView;
import static com.example.productsearch.favoritesFragment.noFavoritesView;

public class RecycleViewAdapterWish extends RecyclerView.Adapter<RecycleViewAdapterWish.MyViewHolder> {


    private static final String TAG = "RecycleViewAdapterWish";

    private Context mContext;
    private List<item> mData;

    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor spEditor;
    private String[] saveStr;


    public RecycleViewAdapterWish(Context mContext, List<item> mData) {
        mSharedPreferences = mContext.getSharedPreferences("mySP", Context.MODE_PRIVATE);
        Log.v(TAG, "Rainie : RecycleViewAdapterWish() : mSharedPreferences.getAll().size() = " + mSharedPreferences.getAll().size());
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

        Picasso.get().load(mData.get(i).getProductImg()).into(myViewHolder.img_item_img);
        myViewHolder.tv_item_title.setText(mData.get(i).getTitle());
        myViewHolder.tv_item_zip.setText(mData.get(i).getZipcode());
        myViewHolder.tv_item_ship.setText(mData.get(i).getShippingCost());
        myViewHolder.tv_item_condition.setText(mData.get(i).getCondition());
        myViewHolder.tv_item_price.setText(mData.get(i).getPrice());

        // add wish list
        // itemFavorite[position] == mData.get(i).getFavorite()
        // favoriteView == myViewHolder.img_item_wish
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


//        // set click listener
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);

                // passing data to the detail activity
                intent.putExtra("title", mData.get(i).getTitle());
                intent.putExtra("itemId", mData.get(i).getItemId());

                // equal to redirect()
//                mContext.startActivity(intent);
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
        Toast.makeText(mContext, mData.get(position).getTitle() + " was removed from favorites", Toast.LENGTH_SHORT).show();
        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getItemId());
        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getTitle());
        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getWish());
        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getPrice());
        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getCondition());
        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getShippingCost());
        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getZipcode());
        Log.v(TAG, "Rainie : mData.get(position) = " + mData.get(position).getProductImg());

        spEditor.remove(mData.get(position).getItemId());
        spEditor.apply();

        Log.v(TAG, "Rainie : After deleteFromFavorite() : mSharedPreferences.getAll().size() = " + mSharedPreferences.getAll().size());
        mData.remove(position);
        
        if (mData.size() == 0) {
            noFavoritesView.setVisibility(View.VISIBLE);
            mRecyclerWishView.setVisibility(View.GONE);
        }

        this.notifyDataSetChanged();
    }

    //    itemFavorite[position] == mData.get(i).getFavorite()



}
