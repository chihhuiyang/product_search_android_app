package com.example.productsearch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;


public class AutoSuggestAdapter extends ArrayAdapter<String> implements Filterable {

    private List<String> mAutoList;

    public AutoSuggestAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        mAutoList = new ArrayList<>();
    }

    public void setData(List<String> list) {
        mAutoList.clear();
        mAutoList.addAll(list);
    }

    @Nullable
    @Override
    public String getItem(int position) {
        return mAutoList.get(position);
    }

    @Override
    public int getCount() {
        return mAutoList.size();
    }

    @NonNull
    @Override
    public Filter getFilter() {
        Filter dataFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    filterResults.values = mAutoList;
                    filterResults.count = mAutoList.size();
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && (results.count > 0)) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };
        return dataFilter;
    }
}
