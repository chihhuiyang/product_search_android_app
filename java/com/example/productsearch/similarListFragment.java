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

public class similarListFragment extends RecyclerView.Adapter<similarListFragment.MyViewHolder> {

    private static final String TAG = "similarListFragment";


    private Context mContext;
    private String[][] mData;

    public similarListFragment(Context mContext, String[][] mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public similarListFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

//        Log.v(TAG, "Rainie : similarListFragment");

        View view;
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        view = mInflater.inflate(R.layout.similar_item, viewGroup, false);

        return new similarListFragment.MyViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull similarListFragment.MyViewHolder myViewHolder, final int i) {

        Picasso.get().load(mData[i][0]).into(myViewHolder.image);
        myViewHolder.title.setText(mData[i][1]);
        myViewHolder.ship_cost.setText(mData[i][6]);
        myViewHolder.left.setText(mData[i][7]);
        myViewHolder.price.setText(mData[i][8]);

        // set click listener
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Log.v(TAG, "Rainie : click similar item : " + mData[i][5]);

                Uri uriUrl = Uri.parse(mData[i][5]);
                Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
                mContext.startActivity(launchBrowser);


//                Intent intent = new Intent(mContext, DetailsActivity.class);
//
//                // passing data to the detail activity
//                intent.putExtra("title", mData.get(i).getTitle());
//                intent.putExtra("itemId", mData.get(i).getItemId());
//                intent.putExtra("jsonObjItem_str", mData.get(i).getJsonObjItem_str());
//
//                // equal to redirect()
//                mContext.startActivity(intent);
            }
        });
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
        TextView title;
        TextView ship_cost;
        TextView left;
        TextView price;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cardView = (CardView) itemView.findViewById(R.id.cardview_similar);

            image = (ImageView) itemView.findViewById(R.id.image);
            title = (TextView) itemView.findViewById(R.id.title);
            ship_cost = (TextView) itemView.findViewById(R.id.ship_cost);
            left = (TextView) itemView.findViewById(R.id.left);
            price = (TextView) itemView.findViewById(R.id.price);
        }
    }
}
