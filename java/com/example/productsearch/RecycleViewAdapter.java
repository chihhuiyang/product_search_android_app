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

        myViewHolder.tv_item_title.setText(mData.get(i).getName());
//        Picasso.get().load(itemIcon[position]).into(iconView);
//        textName.setText(itemName[position]);
//        Log.v(TAG, "Rainie : Picasso : getIcon()" + mData.get(i).getIcon() );
        Picasso.get().load(mData.get(i).getIcon()).into(myViewHolder.img_item_icon);
//        myViewHolder.img_item_icon.setImage


        // add wish list
        // itemFavorite[position] == mData.get(i).getFavorite()
        // favoriteView == myViewHolder.img_item_wish
        Picasso.get().load(mData.get(i).getFavorite()).into(myViewHolder.img_item_wish);
        if (mData.get(i).getFavorite() == "no") {
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
                intent.putExtra("name", mData.get(i).getName());
                intent.putExtra("place", mData.get(i).getPlaceId());


                // equal to redirect()
                mContext.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tv_item_title;
        ImageView img_item_icon;
        CardView cardView;

        ImageView img_item_wish;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_item_title = (TextView) itemView.findViewById(R.id.item_title_id);
            img_item_icon = (ImageView) itemView.findViewById(R.id.item_img_id);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);

            img_item_wish = (ImageView) itemView.findViewById(R.id.item_wish_id);

        }
    }


//    itemFavorite[position] == mData.get(i).getFavorite()
    public void addToFavorite(int position) {
        String placeName = mData.get(position).getName();
        if (mData.get(position).getFavorite() == "yes")
        {
            mData.get(position).setFavorite("no");
            spEditor.remove(mData.get(position).getPlaceId());
            spEditor.apply();

            this.notifyDataSetChanged();
            Toast.makeText(mContext, placeName + " was removed from favorites", Toast.LENGTH_SHORT).show();
        }
        else
        {
            mData.get(position).setFavorite("yes");
            saveStr = new String[5];
            saveStr[0] = mData.get(position).getPlaceId();
            saveStr[1] = mData.get(position).getIcon();
            saveStr[2] = mData.get(position).getName();
            saveStr[3] = mData.get(position).getAddress();
            saveStr[4] = mData.get(position).getFavorite();

            Gson gson = new Gson();
            String myStr = gson.toJson(saveStr);

            spEditor.putString(mData.get(position).getPlaceId(), myStr);
            spEditor.apply();

            this.notifyDataSetChanged();
            Toast.makeText(mContext, placeName + " was added to favorites", Toast.LENGTH_SHORT).show();
        }
    }

}



