package com.example.productsearch;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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

import java.util.ArrayList;
import java.util.List;


public class ResultsActivity extends AppCompatActivity
{
    public TextView mProgressBarMsg;
    public ProgressBar mProgressBar;

    private static final String TAG = "ResultsActivity";
    SharedPreferences.Editor spEditor;
    SharedPreferences mSharedPreferences;

    public LinearLayout mShowingResult;
    public TextView mNum_results;
    public TextView mSearch_keyword;

    public RecyclerView mRecyclerView;
    public List<item> listItem;
    public List<item> copylistItem; // for details go back to result (update wish icon)
    public String[] list_itemId;
    public String[] list_productImg;
    public String[] list_title;
    public String[] list_zipcode;
    public String[] list_shippingCost;
    public String[] list_condition;
    public String[] list_wish;
    public String[] list_price;
    public String[] list_jsonArray_item;

    public TextView noResultsView;

    public JSONArray jsonArray_items;   // 50 items



    private boolean ifFisrtTime;

    public Intent newIntent;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        setTitle("Search Results");

        Log.v(TAG, "Rainie: onCreate()");

        setProgressBarVisibility(true);
        setProgressBarIndeterminateVisibility(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spEditor = this.getSharedPreferences("mySP", Context.MODE_PRIVATE).edit();
        mSharedPreferences = this.getSharedPreferences("mySP", Context.MODE_PRIVATE);


        mShowingResult = (LinearLayout)findViewById(R.id.showingResult);
        mNum_results = (TextView)findViewById(R.id.num_results);
        mSearch_keyword = (TextView)findViewById(R.id.search_keyword);

        mRecyclerView = (RecyclerView) findViewById(R.id.resultsList2);



        noResultsView = (TextView) findViewById(R.id.noResults);

        mProgressBarMsg = (TextView) findViewById(R.id.progress_bar_message);;
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);

        ifFisrtTime = true;

        newIntent = new Intent(this, DetailsActivity.class);

        try
        {
            receiveData();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onResume()
    {

        Log.v(TAG, "Rainie: onResume()");
        super.onResume();
        if (!ifFisrtTime)
        {
            Log.v(TAG, "Rainie: not first time");
            checkIfFavorite();
            setAdapterForListView();
        }
//        ifFisrtTime = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void receiveData() throws JSONException
    {
        Intent mIntent = getIntent();


        String receivedUrl = mIntent.getStringExtra("url");
        String receivedKeyword = mIntent.getStringExtra("keyword");
        Log.v(TAG, "Rainie: receivedUrl : " + receivedUrl);
        Log.v(TAG, "Rainie: receivedKeyword : " + receivedKeyword);


        mSearch_keyword.setText(receivedKeyword);



            // Instantiate the RequestQueue.
            RequestQueue queue = Volley.newRequestQueue(this);


//         Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, receivedUrl, new Response.Listener<String>()
            {
                @Override
                public void onResponse(String response)
                {
                    try
                    {

                        mProgressBar.setVisibility(View.GONE);
                        mProgressBarMsg.setVisibility(View.GONE);


                        JSONObject jsonObject = new JSONObject(response);

                        Log.v(TAG, "Rainie: JSON : " + jsonObject.toString());

                        if (jsonObject.getJSONArray("findItemsAdvancedResponse").getJSONObject(0).getJSONArray("searchResult") == null) {   // null searchResult
                            noResults();
                        } else {    // valid item
                            String count_str = jsonObject.getJSONArray("findItemsAdvancedResponse").getJSONObject(0).getJSONArray("searchResult").getJSONObject(0).getString("@count");
                            int count_items = Integer.parseInt(count_str);
                            if (count_items == 0) {
                                noResults();
                            } else {
                                Log.v(TAG, "Rainie: Before generateTable() !!!");
                                hasResults();
                                generateTable2(jsonObject);
                            }
                        }

                    }
                    catch (JSONException e)
                    {
                        Toast.makeText(ResultsActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
                        Log.v(TAG, "Rainie: JSONException");
                        mProgressBar.setVisibility(View.GONE);
                        mProgressBarMsg.setVisibility(View.GONE);
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener()
            {
                @Override
                public void onErrorResponse(VolleyError error)
                {
                    Toast.makeText(ResultsActivity.this, "No connection! Please check your internet connection.", Toast.LENGTH_SHORT).show();

                    Log.v(TAG, "Rainie: VolleyError");
                    mProgressBar.setVisibility(View.GONE);
                    mProgressBarMsg.setVisibility(View.GONE);
                    System.out.println(error);
                }
            });
            queue.add(stringRequest);


    }

    public void setAdapterForListView()
    {
        copylistItem = new ArrayList<>();
        for (int i = 0; i < listItem.size(); i++) {
            copylistItem.add(new item(list_itemId[i], list_productImg[i], list_title[i], list_zipcode[i], list_shippingCost[i], list_condition[i], list_price[i], list_wish[i], list_jsonArray_item[i]));
        }

        RecycleViewAdapter myAdapter = new RecycleViewAdapter(this, copylistItem);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2)); // for card view
        mRecyclerView.setAdapter(myAdapter);
    }


    public void generateTable2(JSONObject jsonObject) throws JSONException {
        try
        {

            jsonArray_items = jsonObject.getJSONArray("findItemsAdvancedResponse").getJSONObject(0).getJSONArray("searchResult").getJSONObject(0).getJSONArray("item");
            int count_items = jsonArray_items.length();

            list_itemId = new String[count_items];
            list_productImg = new String[count_items];
            list_title = new String[count_items];
            list_zipcode = new String[count_items];
            list_shippingCost = new String[count_items];
            list_condition = new String[count_items];
            list_wish = new String[count_items];
            list_price = new String[count_items];
            list_jsonArray_item = new String[count_items];

            mNum_results.setText(String.valueOf(count_items));

            Log.v(TAG, "Rainie : count_items = " + count_items);
            listItem = new ArrayList<>();
            for (int i = 0; i < count_items; i++) {
                // itemId
                String itemId = jsonArray_items.getJSONObject(i).getJSONArray("itemId").getString(0);
//                Log.v(TAG, "Rainie : itemId[" + i + "] = " + itemId);

                // productImg
                String productImg = jsonArray_items.getJSONObject(i).getJSONArray("galleryURL").getString(0);
//                Log.v(TAG, "Rainie : productImg[" + i + "] = " + productImg);

                // short title
                String title = jsonArray_items.getJSONObject(i).getJSONArray("title").getString(0);
                if (title.length() > 35) {
                    title = title.substring(0,35) + "...";
                }
//                Log.v(TAG, "Rainie : title[" + i + "] = " + title);

                // zipcode (could be N/A)
                String zipcode = "N/A";
                if (!jsonArray_items.getJSONObject(i).isNull("postalCode")) {
                    zipcode = "Zip: " + jsonArray_items.getJSONObject(i).getJSONArray("postalCode").getString(0);
                }
//                Log.v(TAG, "Rainie : zipcode[" + i + "] = " + zipcode);

                // free shipping (could be N/A)
                String shippingCost = "N/A";
                if (!jsonArray_items.getJSONObject(i).isNull("shippingInfo")) {
                    if (!jsonArray_items.getJSONObject(i).getJSONArray("shippingInfo").getJSONObject(0).isNull("shippingServiceCost")) {
                        if (!jsonArray_items.getJSONObject(i).getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost").getJSONObject(0).isNull("__value__")) {
                            shippingCost = jsonArray_items.getJSONObject(i).getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost").getJSONObject(0).getString("__value__");
                            double shippingCost_double = Double.parseDouble(shippingCost);
                            if (shippingCost_double == 0.0) {
                                shippingCost = "Free Shipping";
                            } else {
                                shippingCost = "$" + shippingCost;
                            }
                        }
                    }
                }
//                Log.v(TAG, "Rainie : shippingCost[" + i + "] = " + shippingCost);

                // condition (could be N/A)
                String condition = "N/A";
                if (!jsonArray_items.getJSONObject(i).isNull("condition")) {
//                    Log.v(TAG, "Rainie : condition");
                    if (!jsonArray_items.getJSONObject(i).getJSONArray("condition").getJSONObject(0).isNull("conditionDisplayName")) {
//                        Log.v(TAG, "Rainie : conditionDisplayName");
                        condition = jsonArray_items.getJSONObject(i).getJSONArray("condition").getJSONObject(0).getJSONArray("conditionDisplayName").getString(0);
                    }
                }
//                Log.v(TAG, "Rainie : condition[" + i + "] = " + condition);

                // price
                String price = jsonArray_items.getJSONObject(i).getJSONArray("sellingStatus").getJSONObject(0).getJSONArray("currentPrice").getJSONObject(0).getString("__value__");
                price = "$" + price;
//                Log.v(TAG, "Rainie : price[" + i + "] = " + price);

                list_itemId[i] = itemId;
                list_productImg[i] = productImg;
                list_title[i] = title;
                list_zipcode[i] = zipcode;
                list_shippingCost[i] = shippingCost;
                list_condition[i] = condition;
                list_price[i] = price;
                list_jsonArray_item[i] = jsonArray_items.getJSONObject(i).toString();

                if (mSharedPreferences.contains(list_itemId[i])) {
                    list_wish[i] = "yes";
                } else {
                    list_wish[i] = "no";
                }

                // TODO : add _jsonObjItem_str variables

                listItem.add(new item(list_itemId[i], list_productImg[i], list_title[i], list_zipcode[i], list_shippingCost[i], list_condition[i], list_price[i], list_wish[i], list_jsonArray_item[i]));

            }
            RecycleViewAdapter myAdapter = new RecycleViewAdapter(this, listItem);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mRecyclerView.setAdapter(myAdapter);

            ifFisrtTime = false;

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void checkIfFavorite()
    {
        if (list_itemId != null) {
            Log.v(TAG, "Rainie : list_itemId is not null");
            for (int i = 0; i < list_itemId.length; i++) {
                if (mSharedPreferences.contains(list_itemId[i])) {
                    Log.v(TAG, "Rainie : yes = " + list_itemId[i]);
                    list_wish[i] = "yes";
                } else {
                    list_wish[i] = "no";
                }
            }
        }
    }




    public void redirect()
    {
        this.startActivity(newIntent);
    }


    public void hasResults()
    {
        mShowingResult.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        noResultsView.setVisibility(View.GONE);
    }

    public void noResults()
    {
        mShowingResult.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.GONE);
        noResultsView.setVisibility(View.VISIBLE);
    }
}
