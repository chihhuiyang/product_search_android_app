package com.example.productsearch;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;

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

    public TextView mShowingResult;

    public RecyclerView mRecyclerView;
    public List<item> listItem;
    public String[] list_itemId;
    public String[] list_productImg;
    public String[] list_title;
    public String[] list_zipcode;
    public String[] list_shippingCost;
    public String[] list_condition;
    public String[] list_wish;
    public String[] list_price;

    public ListView mListView;
    public TextView noResultsView;
    public String[] placeIdList;
    public String[] iconList;
    public String[] nameList;
    public String[] addressList;
    public String[] favoriteList;

    public JSONArray jsonArray;
    public JSONArray jsonArray_items;   // 50 items

    public String[][][] rowData = new String[3][5][20];


    public ProgressDialog mProgressDialog;
    public String nextPageToken;
    public boolean[] ifHasNextPage = new boolean[3];
    public Button mPrevButton;
    public Button mNextButton;
    public int currentPage = 0;
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


        mShowingResult = (TextView)findViewById(R.id.showingResult);
        mNextButton = (Button)findViewById(R.id.nextButton);
        mPrevButton = (Button)findViewById(R.id.previousButton);

        mListView = (ListView)findViewById(R.id.resultsList);
        mRecyclerView = (RecyclerView) findViewById(R.id.resultsList2);

        listItem = new ArrayList<>();

        noResultsView = (TextView) findViewById(R.id.noResults);

        mProgressBarMsg = (TextView) findViewById(R.id.progress_bar_message);;
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);


        newIntent = new Intent(this, DetailsActivity.class);

        try
        {
            receiveData();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        mNextButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        getNextPage();
                    }
                }
        );

        mPrevButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        getPreviousPage();
                    }
                }
        );

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                newIntent = new Intent(ResultsActivity.this, DetailsActivity.class);
                requestDetails(rowData[currentPage][2][position], rowData[currentPage][0][position]);
            }
        });


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
        ifFisrtTime = false;
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
        Log.v(TAG, "Rainie: mIntent : " + mIntent);
        Log.v(TAG, "Rainie: receivedUrl : " + receivedUrl);


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
                                ifFisrtTime = true;
                                Log.v(TAG, "Rainie: count_items = " + count_items);
                                hasResults();
                                generateTable2(jsonObject);
                            }
                        }



//                                if (jsonObject.getString("status").equals("OK"))
//                                {
//                                    ifFisrtTime = true;
//                                    Log.v(TAG, "Rainie: response OK");
//                                    hasResults();
//                                    generateTable(jsonObject);
//                                }
//                                else
//                                {
//                                    noResults();
//                                }
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
//        resultsListFragment resultsListAdapter = new resultsListFragment(this,rowData[currentPage][0], rowData[currentPage][1],
//                rowData[currentPage][2], rowData[currentPage][3], rowData[currentPage][4]);
//        mListView.setAdapter(resultsListAdapter);


//        List<item> copy_listItem = new ArrayList<>();
//        for (int i = 0; i < rowData[currentPage][0].length; i++) {
//            copy_listItem.add(new item(rowData[currentPage][0][i], rowData[currentPage][1][i], rowData[currentPage][2][i], rowData[currentPage][3][i], rowData[currentPage][4][i]));
//        }

        RecycleViewAdapter myAdapter = new RecycleViewAdapter(this, listItem);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
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


            for (int i = 0; i < count_items; i++) {
                // itemId
                String itemId = jsonArray_items.getJSONObject(i).getJSONArray("itemId").getString(0);
                Log.v(TAG, "Rainie : itemId[" + i + "] = " + itemId);

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
                Log.v(TAG, "Rainie : zipcode[" + i + "] = " + zipcode);

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
                Log.v(TAG, "Rainie : shippingCost[" + i + "] = " + shippingCost);

                // condition (could be N/A)
                String condition = "N/A";
                if (!jsonArray_items.getJSONObject(i).isNull("condition")) {
//                    Log.v(TAG, "Rainie : condition");
                    if (!jsonArray_items.getJSONObject(i).getJSONArray("condition").getJSONObject(0).isNull("conditionDisplayName")) {
//                        Log.v(TAG, "Rainie : conditionDisplayName");
                        condition = jsonArray_items.getJSONObject(i).getJSONArray("condition").getJSONObject(0).getJSONArray("conditionDisplayName").getString(0);
                    }
                }
                Log.v(TAG, "Rainie : condition[" + i + "] = " + condition);

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

                if (mSharedPreferences.contains(list_itemId[i])) {
                    list_wish[i] = "yes";
                } else {
                    list_wish[i] = "no";
                }

                // TODO : add _jsonObjItem_str variables

                listItem.add(new item(list_itemId[i], list_productImg[i], list_title[i], list_zipcode[i], list_shippingCost[i], list_condition[i], list_price[i], list_wish[i], jsonArray_items.getJSONObject(i).toString()));

            }
            RecycleViewAdapter myAdapter = new RecycleViewAdapter(this, listItem);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mRecyclerView.setAdapter(myAdapter);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }


    public void generateTable(JSONObject jsonObject) throws JSONException
    {
        try
        {
            if (jsonObject.has("next_page_token"))
            {
                ifHasNextPage[currentPage] = true;
                mNextButton.setEnabled(true);
                nextPageToken = jsonObject.getString("next_page_token");
            }
            else
            {
                ifHasNextPage[currentPage] = false;
                mNextButton.setEnabled(false);
            }

            jsonArray = jsonObject.getJSONArray("results");
            placeIdList = new String[jsonArray.length()];
            iconList = new String[jsonArray.length()];
            nameList = new String[jsonArray.length()];
            addressList = new String[jsonArray.length()];
            favoriteList = new String[jsonArray.length()];

            for (int i = 0; i < jsonArray.length(); i++)
            {
                String place_id = jsonArray.getJSONObject(i).getString("place_id");
                String icon = jsonArray.getJSONObject(i).getString("icon");
                String name = jsonArray.getJSONObject(i).getString("name");
                String address = jsonArray.getJSONObject(i).getString("vicinity");

                placeIdList[i] = place_id;
                iconList[i] = icon;
                nameList[i] = name;
                addressList[i] = address;
                if (mSharedPreferences.contains(placeIdList[i]))
                {
                    favoriteList[i] = "yes";
                }
                else
                {
                    favoriteList[i] = "no";
                }

                rowData[currentPage][0][i] = placeIdList[i];
                rowData[currentPage][1][i] = iconList[i];
//                Log.v(TAG, "Rainie : iconList[" + i + "] = " + iconList[i]);
                rowData[currentPage][2][i] = nameList[i];
                rowData[currentPage][3][i] = addressList[i];
                rowData[currentPage][4][i] = favoriteList[i];


//                String placeId, String icon, String name, String address, String favorite
//                listItem.add(new item(placeIdList[i], iconList[i], nameList[i], addressList[i], favoriteList[i]));
            }
            resultsListFragment resultsListAdapter = new resultsListFragment(this, placeIdList, iconList, nameList, addressList, favoriteList);
            mListView.setAdapter(resultsListAdapter);


            RecycleViewAdapter myAdapter = new RecycleViewAdapter(this, listItem);
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
            mRecyclerView.setAdapter(myAdapter);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void checkIfFavorite()
    {
//        for (int i = 0; i < rowData[currentPage][0].length; i++)
//        {
//            if (mSharedPreferences.contains(rowData[currentPage][0][i]))
//            {
//                rowData[currentPage][4][i] = "yes";
//            }
//            else
//            {
//                rowData[currentPage][4][i] = "no";
//            }
//        }

        if (list_itemId != null) {
            Log.v(TAG, "Rainie : list_itemId is not null");
            for (int i = 0; i < list_itemId.length; i++) {
                if (mSharedPreferences.contains(list_itemId[i])) {
                    list_wish[i] = "yes";
                } else {
                    list_wish[i] = "no";
                }
            }
        }
    }

    public void getNextPage()
    {
        if (currentPage == 0)
        {
            if (rowData[currentPage+1][0][0] == null)
            {
                if (ifHasNextPage[currentPage])
                {
                    requestNextPage();
                    currentPage++;
                    mPrevButton.setEnabled(true);
                }
                else
                {
                    mNextButton.setEnabled(false);
                }
            }
            else
            {
                currentPage++;
                checkIfFavorite();
                setAdapterForListView();
                mPrevButton.setEnabled(true);
                if (ifHasNextPage[currentPage])
                {
                    mNextButton.setEnabled(true);
                }
                else
                {
                    mNextButton.setEnabled(false);
                }
            }
        }
        else if (currentPage == 1)
        {
            if (rowData[currentPage+1][0][0] == null)
            {
                if (ifHasNextPage[currentPage] == true)
                {
                    requestNextPage();
                    currentPage++;
                    mPrevButton.setEnabled(true);
                }
                else
                {
                    mNextButton.setEnabled(false);
                }
            }
            else
            {
                currentPage++;
                checkIfFavorite();
                setAdapterForListView();
                mPrevButton.setEnabled(true);
                mNextButton.setEnabled(false);
            }
        }
        else
        {
            mPrevButton.setEnabled(true);
            mNextButton.setEnabled(false);
        }

    }

    public void requestNextPage()
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Fetching next page");
        mProgressDialog.show();

        String mUrl = "http://cs571placesearch-env.us-east-2.elasticbeanstalk.com/?";
        mUrl += "nextPageToken=" + nextPageToken;
        System.out.println(mUrl);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    generateTable(jsonObject);
                    //System.out.println(jsonObject.toString());
                    mProgressDialog.dismiss();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        },
        new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                System.out.println("Request error!");
                System.out.println(error);
            }
        });
        queue.add(stringRequest);
    }

    public void getPreviousPage()
    {
        if (currentPage == 1)
        {
            currentPage--;
            checkIfFavorite();
            setAdapterForListView();
            mPrevButton.setEnabled(false);
            mNextButton.setEnabled(true);
        }
        else if (currentPage == 2)
        {
            currentPage--;
            checkIfFavorite();
            setAdapterForListView();
            mPrevButton.setEnabled(true);
            mNextButton.setEnabled(true);
        }
        else
        {
            mPrevButton.setEnabled(false);
            mNextButton.setEnabled(true);
        }
    }

    public void redirect()
    {
        this.startActivity(newIntent);
    }

    public void requestDetails(String mName, String mPlaceId)
    {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Fetching details");
        mProgressDialog.show();

        String mUrl = "https://maps.googleapis.com/maps/api/place/details/json?";
        mUrl += "placeid=" + mPlaceId;
        mUrl += "&key=AIzaSyC9HBExGTftsTmeBjHXLucUi5NH2QXCQkY";
        System.out.println(mUrl);

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    //System.out.println(jsonObject.toString());
                    newIntent.putExtra("jsonObj", jsonObject.toString());
                    redirect();
                    mProgressDialog.dismiss();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        System.out.println("Request error!");
                        System.out.println(error);
                    }
                });
        queue.add(stringRequest);
    }

    public void hasResults()
    {
        mShowingResult.setVisibility(View.VISIBLE);

        mListView.setVisibility(View.VISIBLE);

        mRecyclerView.setVisibility(View.VISIBLE);

//        mNextButton.setVisibility(View.VISIBLE);
//        mPrevButton.setVisibility(View.VISIBLE);
        noResultsView.setVisibility(View.GONE);
    }

    public void noResults()
    {
        mShowingResult.setVisibility(View.GONE);

        mListView.setVisibility(View.GONE);

        mRecyclerView.setVisibility(View.GONE);

        mNextButton.setVisibility(View.GONE);
        mPrevButton.setVisibility(View.GONE);
        noResultsView.setVisibility(View.VISIBLE);
    }
}
