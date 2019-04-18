package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
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

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.MyViewHolder> {


    private static final String TAG = "RecycleViewAdapter";

    private Context mContext;
    private List<item> mData;

    public RecycleViewAdapter(Context mContext, List<item> mData) {
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


//        // set click listener
        myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, DetailsActivity.class);

                // passing data to the detail activity
                intent.putExtra("name", mData.get(i).getName());
                intent.putExtra("place", mData.get(i).getPlaceId());

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

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_item_title = (TextView) itemView.findViewById(R.id.item_title_id);
            img_item_icon = (ImageView) itemView.findViewById(R.id.item_img_id);
            cardView = (CardView) itemView.findViewById(R.id.cardview_id);
        }
    }

}
