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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.List;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {


    private static final String TAG = "RecycleViewAdapter";

    private Context mContext;
    private List<item> mData;

    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor spEditor;
    private String[] saveStr;


    public RecycleViewAdapter(Context mContext, List<item> mData) {
        mSharedPreferences = mContext.getSharedPreferences("mySP", Context.MODE_PRIVATE);
        spEditor = mSharedPreferences.edit();

        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.cardview_item, viewGroup, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {
//        Picasso.get().load(itemIcon[position]).into(iconView);
//        textName.setText(itemName[position]);
//        Log.v(TAG, "Rainie : Picasso : getIcon()" + mData.get(i).getIcon() );
//        myViewHolder.img_item_icon.setImage


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
                        addToFavorite(i);
                    }
                }
        );

//        if (itemPlaceId[position] != null)
//        {
//            Picasso.get().load(itemIcon[position]).into(iconView);
//            textName.setText(itemName[position]);
//            textAddress.setText(itemAddress[position]);
//
//            if (itemFavorite[position] == "no")
//            {
//                favoriteView.setImageResource(R.drawable.cart_plus);
//            }
//            else
//            {
//                favoriteView.setImageResource(R.drawable.cart_remove);
//            }
//        }
//        else
//        {
//            resultLayout.setVisibility(View.GONE);
//            iconView.setVisibility(View.GONE);
//            favoriteView.setVisibility(View.GONE);
//            textName.setVisibility(View.GONE);
//            textAddress.setVisibility(View.GONE);
//            rowView.setVisibility(View.GONE);
//        }

//        favoriteView.setOnClickListener(
//                new View.OnClickListener()
//                {
//                    public void onClick(View view)
//                    {
//                        addToFavorite(position);
//                    }
//                }
//        );


//        // set click listener
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);

                // passing data to the detail activity
                intent.putExtra("title", mData.get(i).getTitle());
                intent.putExtra("itemId", mData.get(i).getItemId());
                intent.putExtra("jsonObjItem_str", mData.get(i).getJsonObjItem_str());

                // equal to redirect()
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

            cardView = (CardView) itemView.findViewById(R.id.cardview_id);

            img_item_img = (ImageView) itemView.findViewById(R.id.item_img_id);
            tv_item_title = (TextView) itemView.findViewById(R.id.item_title_id);
            tv_item_zip = (TextView) itemView.findViewById(R.id.item_zip_id);
            tv_item_ship = (TextView) itemView.findViewById(R.id.item_ship_id);
            tv_item_condition = (TextView) itemView.findViewById(R.id.item_condition_id);
            tv_item_price  = (TextView) itemView.findViewById(R.id.item_price_id);
            img_item_wish = (ImageView) itemView.findViewById(R.id.item_wish_id);
        }
    }


//    itemFavorite[position] == mData.get(i).getFavorite()
    public void addToFavorite(int position) {
        String item_title = mData.get(position).getTitle();
        if (mData.get(position).getWish() == "yes") {
            mData.get(position).setWish("no");
            spEditor.remove(mData.get(position).getItemId());
            spEditor.apply();

            this.notifyDataSetChanged();
            Toast.makeText(mContext, item_title + " was removed from favorites", Toast.LENGTH_SHORT).show();
        } else {
            mData.get(position).setWish("yes");
            saveStr = new String[8];
            saveStr[0] = mData.get(position).getItemId();
            saveStr[1] = mData.get(position).getProductImg();
            saveStr[2] = mData.get(position).getTitle();
            saveStr[3] = mData.get(position).getZipcode();
            saveStr[4] = mData.get(position).getShippingCost();
            saveStr[5] = mData.get(position).getCondition();
            saveStr[6] = mData.get(position).getPrice();
            saveStr[7] = mData.get(position).getWish();

            Gson gson = new Gson();
            String myStr = gson.toJson(saveStr);

            spEditor.putString(mData.get(position).getItemId(), myStr);
            spEditor.apply();

            this.notifyDataSetChanged();
            Toast.makeText(mContext, item_title + " was added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

}



