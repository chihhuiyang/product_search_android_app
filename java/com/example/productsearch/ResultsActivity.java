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

    public ListView mListView;
    public RecyclerView mRecyclerView;

    public List<item> listItem;

    public TextView noResultsView;
    public String[] placeIdList;
    public String[] iconList;
    public String[] nameList;
    public String[] addressList;
    public String[] favoriteList;

    public JSONArray jsonArray;
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

//        mRecyclerView.


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void onResume()
    {

        Log.v(TAG, "Rainie: onResume()");
        super.onResume();
        if (!ifFisrtTime)
        {
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
                        JSONArray jsonArray = jsonObject.getJSONArray("results");
                        Log.v(TAG, "Rainie: JSON : " + jsonObject.toString());


                                if (jsonObject.getString("status").equals("OK"))
                                {

                                    ifFisrtTime = true;
                                    Log.v(TAG, "Rainie: response OK");
                                    hasResults();
                                    generateTable(jsonObject);
                                }
                                else
                                {
                                    noResults();
                                }
                    }
                    catch (JSONException e)
                    {
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
        resultsListFragment resultsListAdapter = new resultsListFragment(this,rowData[currentPage][0], rowData[currentPage][1],
                rowData[currentPage][2], rowData[currentPage][3], rowData[currentPage][4]);
        mListView.setAdapter(resultsListAdapter);


        List<item> copy_listItem = new ArrayList<>();
        for (int i = 0; i < rowData[currentPage][0].length; i++) {
            copy_listItem.add(new item(rowData[currentPage][0][i], rowData[currentPage][1][i], rowData[currentPage][2][i], rowData[currentPage][3][i], rowData[currentPage][4][i]));
        }

        RecycleViewAdapter myAdapter = new RecycleViewAdapter(this, copy_listItem);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        mRecyclerView.setAdapter(myAdapter);
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
                Log.v(TAG, "Rainie : iconList[" + i + "] = " + iconList[i]);
                rowData[currentPage][2][i] = nameList[i];
                rowData[currentPage][3][i] = addressList[i];
                rowData[currentPage][4][i] = favoriteList[i];


//                String placeId, String icon, String name, String address, String favorite
                listItem.add(new item(placeIdList[i], iconList[i], nameList[i], addressList[i], favoriteList[i]));
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
        for (int i = 0; i < rowData[currentPage][0].length; i++)
        {
            if (mSharedPreferences.contains(rowData[currentPage][0][i]))
            {
                rowData[currentPage][4][i] = "yes";
            }
            else
            {
                rowData[currentPage][4][i] = "no";
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
//        mNextButton.setVisibility(View.VISIBLE);
//        mPrevButton.setVisibility(View.VISIBLE);
        noResultsView.setVisibility(View.GONE);
    }

    public void noResults()
    {
        mShowingResult.setVisibility(View.GONE);

        mListView.setVisibility(View.GONE);
        mNextButton.setVisibility(View.GONE);
        mPrevButton.setVisibility(View.GONE);
        noResultsView.setVisibility(View.VISIBLE);
    }
}
