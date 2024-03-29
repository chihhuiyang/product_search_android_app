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

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {

    private static final String TAG = "RecycleViewAdapter";

    private Context mContext;
    private List<item> mData;

    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor spEditor;
    private String[] packetDetail;

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
        Picasso.get().load(mData.get(i).getProductImg()).error(mContext.getDrawable(R.drawable.image_outline)).into(myViewHolder.img_item_img);

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
                new View.OnClickListener() {
                    public void onClick(View view) {
                        addToWishList(i);
                    }
                }
        );


        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);

                intent.putExtra("jsonObjItem_str", mData.get(i).getJsonObjItem_str());
                intent.putExtra("itemId", mData.get(i).getItemId());
                intent.putExtra("title", mData.get(i).getTitle());
                intent.putExtra("card_product_img", mData.get(i).getProductImg());
                intent.putExtra("card_zipcode", mData.get(i).getZipcode());
                intent.putExtra("card_shipping_cost", mData.get(i).getShippingCost());
                intent.putExtra("card_condition", mData.get(i).getCondition());
                intent.putExtra("card_price", mData.get(i).getPrice());
                intent.putExtra("card_wish", mData.get(i).getWish());

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


    public void addToWishList(int position) {
        String item_title = mData.get(position).getTitle();
        if (mData.get(position).getWish() == "yes") {
            mData.get(position).setWish("no");
            spEditor.remove(mData.get(position).getItemId());
            spEditor.apply();

            this.notifyDataSetChanged();
            Toast.makeText(mContext, item_title + " was removed from wish list", Toast.LENGTH_SHORT).show();
        } else {
            mData.get(position).setWish("yes");
            packetDetail = new String[9];
            packetDetail[0] = mData.get(position).getItemId();
            packetDetail[1] = mData.get(position).getProductImg();
            packetDetail[2] = mData.get(position).getTitle();
            packetDetail[3] = mData.get(position).getZipcode();
            packetDetail[4] = mData.get(position).getShippingCost();
            packetDetail[5] = mData.get(position).getCondition();
            packetDetail[6] = mData.get(position).getPrice();
            packetDetail[7] = mData.get(position).getWish();
            packetDetail[8] = mData.get(position).getJsonObjItem_str();

            Gson gson = new Gson();
            String myStr = gson.toJson(packetDetail);

            spEditor.putString(mData.get(position).getItemId(), myStr);
            spEditor.apply();

            this.notifyDataSetChanged();
            Toast.makeText(mContext, item_title + " was added to wish list", Toast.LENGTH_SHORT).show();
        }
    }

}



