package com.example.productsearch;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class spec_item extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] specList_value;

    public ImageView favoriteView;

    public spec_item(Activity context, String[] spec_value) {
        super(context, R.layout.spec_item, spec_value);

        this.context = context;
        this.specList_value = spec_value;

    }

    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.spec_item, null,true);

        TextView spec = (TextView) rowView.findViewById(R.id.spec);

        if (specList_value[position] != null) {
            spec.setText(specList_value[position]);
        } else {
            spec.setVisibility(View.GONE);
        }

        return rowView;
    }


}
