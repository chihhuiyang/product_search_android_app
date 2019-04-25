package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;


public class photoListFragment extends RecyclerView.Adapter<photoListFragment.MyViewHolder> {

    private static final String TAG = "photoListFragment";

    private Context mContext;
    private String[][] mData;

    public photoListFragment(Context mContext, String[][] mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public photoListFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.photo_item, viewGroup, false);

        return new photoListFragment.MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull photoListFragment.MyViewHolder myViewHolder, final int i) {
        Picasso.get().load(mData[i][0]).error(mContext.getDrawable(R.drawable.image_outline)).into(myViewHolder.image);
    }


    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        } else {
            return mData.length;
        }
    }



    public static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;

        ImageView image;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview_photo);

            image = (ImageView) itemView.findViewById(R.id.photo_item);
        }
    }
}
