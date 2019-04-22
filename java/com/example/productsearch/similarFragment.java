package com.example.productsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Comparator;

public class similarFragment extends Fragment {

    private static final String TAG = "similarFragment";

    public JSONObject jsonObject_similar;
    public String jsonObject_similar_str;
    public JSONArray jsonObj_similar_array;
    public String itemId;

    public final String[] sort_option = {"Default", "Name", "Price", "Days"};
    public final String[] sort_direction = {"Ascending", "Descending"};

    public Spinner mOptionSpinner;
    public Spinner mDirectionSpinner;
    public RecyclerView mSimilarList;
    public TextView noResultsView;

    public String[][] defaultArray;
    public String[][] sortedArray;

    public String[][] googleReviewsArr;
    public String[][] originalGoogleReviewsArr;
    public String[][] yelpReviewsArr;
    public String[][] originalYelpReviewsArr;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.v(TAG, "Rainie : onCreateView()");

        View view = inflater.inflate(R.layout.fragment_similar, container, false);
        mSimilarList = (RecyclerView)view.findViewById(R.id.similarList);
        noResultsView = (TextView)view.findViewById(R.id.noResults);

        mOptionSpinner = (Spinner)view.findViewById(R.id.reviewSpinner);
        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, sort_option);
        adapter1.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mOptionSpinner.setAdapter(adapter1);

        mDirectionSpinner = (Spinner)view.findViewById(R.id.orderSpinner);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, sort_direction);
        adapter2.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mDirectionSpinner.setAdapter(adapter2);


        mOptionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                apiAndOrderSelector(mOptionSpinner.getSelectedItem().toString(), mDirectionSpinner.getSelectedItem().toString());
            }
            public void onNothingSelected(AdapterView<?> parent)
            {
                //Must select one of them, so nothing here...
            }
        });

        mDirectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                apiAndOrderSelector(mOptionSpinner.getSelectedItem().toString(), mDirectionSpinner.getSelectedItem().toString());
            }
            public void onNothingSelected(AdapterView<?> parent)
            {
                //Must select one of them, so nothing here...
            }
        });



        Bundle bundle;
        bundle = this.getArguments();
        jsonObject_similar_str = bundle.getString("jsonObject_similar");

        Log.v(TAG, "Rainie: jsonObject_similar_str = " + jsonObject_similar_str);

        try
        {

            jsonObject_similar = new JSONObject(jsonObject_similar_str);

            if (jsonObject_similar.has("getSimilarItemsResponse")) {
                if (jsonObject_similar.getJSONObject("getSimilarItemsResponse").has("itemRecommendations")) {
                    if (jsonObject_similar.getJSONObject("getSimilarItemsResponse").getJSONObject("itemRecommendations").has("item")) {
                        jsonObj_similar_array = jsonObject_similar.getJSONObject("getSimilarItemsResponse").getJSONObject("itemRecommendations").getJSONArray("item");
                        int count = jsonObj_similar_array.length();
                        if (count == 0) {
                            noResultsView.setVisibility(View.VISIBLE);
                        } else {
                            noResultsView.setVisibility(View.GONE);
                            defaultArray = new String[count][6];
                            sortedArray = new String[count][6];
                            for (int i = 0; i < count; i++) {
                                defaultArray[i][0] = jsonObj_similar_array.getJSONObject(i).getString("imageURL");
                                defaultArray[i][1] = jsonObj_similar_array.getJSONObject(i).getString("title");
                                defaultArray[i][2] = jsonObj_similar_array.getJSONObject(i).getJSONObject("shippingCost").getString("__value__");
                                String timeLeft_str = jsonObj_similar_array.getJSONObject(i).getString("timeLeft");
                                int a = timeLeft_str.indexOf("P");
                                int b = timeLeft_str.indexOf("D");
                                defaultArray[i][3] = timeLeft_str.substring(a+1, b);
                                defaultArray[i][4] = jsonObj_similar_array.getJSONObject(i).getJSONObject("buyItNowPrice").getString("__value__");
                                defaultArray[i][5] = jsonObj_similar_array.getJSONObject(i).getString("viewItemURL");

                                // copy to sortedArray
                                sortedArray[i][0] = defaultArray[i][0];
                                sortedArray[i][1] = defaultArray[i][1];
                                sortedArray[i][2] = defaultArray[i][2];
                                sortedArray[i][3] = defaultArray[i][3];
                                sortedArray[i][4] = defaultArray[i][4];
                                sortedArray[i][5] = defaultArray[i][5];
                            }
                        }
                    } else {
                        noResultsView.setVisibility(View.VISIBLE);
                    }
                } else {
                    noResultsView.setVisibility(View.VISIBLE);
                }
            } else {
                noResultsView.setVisibility(View.VISIBLE);
            }


        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return view;
    }



    public void setAdapterForListView(String[][] operateArr)
    {
        similarListFragment similarListAdapter = new similarListFragment(this.getActivity(), operateArr);
        mSimilarList.setAdapter(similarListAdapter);
    }



    public void apiAndOrderSelector(String mOption, String mDirection) {
        if (mOption.equals("Default")) {
            setAdapterForListView(defaultArray);

            // TODO : disable mDirection

        } else if (mOption.equals("Name")) {

            if (mDirection.equals("Ascending")) {
                Log.v(TAG, "Rainie: ^");
                sortAscending(sortedArray, 1);
                setAdapterForListView(sortedArray);
            } else {
                Log.v(TAG, "Rainie: v");
                sortDescending(sortedArray, 1);
                setAdapterForListView(sortedArray);
            }

        } else if (mOption.equals("Price")) {

            if (mDirection.equals("Ascending")) {
                Log.v(TAG, "Rainie: ^");
                sortAscending(sortedArray, 4);
                setAdapterForListView(sortedArray);
            } else {
                Log.v(TAG, "Rainie: v");
                sortDescending(sortedArray, 4);
                setAdapterForListView(sortedArray);
            }

        } else if (mOption.equals("Days")) {

            if (mDirection.equals("Ascending")) {
                Log.v(TAG, "Rainie: ^");
                sortAscending(sortedArray, 3);
                setAdapterForListView(sortedArray);
            } else {
                Log.v(TAG, "Rainie: v");
                sortDescending(sortedArray, 3);
                setAdapterForListView(sortedArray);
            }

        }
    }



    public void sortAscending(String[][] data, final int index)
    {
        Arrays.sort(data, new Comparator<String[]>()
        {
            @Override
            public int compare(final String[] entry1, final String[] entry2)
            {
                final String time1 = entry1[index];
                final String time2 = entry2[index];
                return time1.compareTo(time2);
            }
        });
    }

    public void sortDescending(String[][] data, final int index)
    {
        Arrays.sort(data, new Comparator<String[]>()
        {
            @Override
            public int compare(final String[] entry1, final String[] entry2)
            {
                final String time1 = entry1[index];
                final String time2 = entry2[index];
                return time2.compareTo(time1);
            }
        });
    }


}
