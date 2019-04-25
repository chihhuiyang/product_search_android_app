package com.example.productsearch;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.NavUtils;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.support.design.widget.TabLayout.MODE_SCROLLABLE;

public class DetailsActivity extends AppCompatActivity {
    
    private static final String TAG = "DetailsActivity";

    private MainActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor spEditor;
    
    public TextView mProgressBarMsg;
    public ProgressBar mProgressBar;
    
    private Button wish_button;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Menu menu;

    public Bundle bundle = new Bundle();
    public JSONObject jsonObject_detail;
    public JSONObject jsonObject_photo;
    public JSONObject jsonObject_similar;

    private String price_item;
    private String jsonObjItem_str;
    private String itemId;
    private String itemTitle;
    private String card_product_img;
    private String card_zipcode;
    private String card_shipping_cost;
    private String card_condition;
    private String card_price;
    private String card_wish;
    public String[] packetDetail;
    Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mContext = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Log.v(TAG, "Rainie: onCreate()");

        setProgressBarVisibility(true);
        setProgressBarIndeterminateVisibility(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSharedPreferences = this.getSharedPreferences("mySP", Context.MODE_PRIVATE);
        spEditor = mSharedPreferences.edit();

        mViewPager = (ViewPager) findViewById(R.id.detailsContainer);
        mProgressBarMsg = (TextView) findViewById(R.id.progress_bar_message_detail);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_detail);
        wish_button = (Button) findViewById(R.id.wish_button);
        tabLayout = (TabLayout) findViewById(R.id.detailsTabs);
        tabLayout.setupWithViewPager(mViewPager);
//        tabLayout.setTabMode(MODE_SCROLLABLE);
//        setupTabIcons();

        receiveData();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        wish_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addToWishList();    // TODO :
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.share:
                try {
                    shareToFacebook();
                }
                catch (JSONException e) {
                    Toast.makeText(DetailsActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(R.drawable.information_variant);
        tabLayout.getTabAt(0).setText("PRODUCT");

        tabLayout.getTabAt(1).setIcon(R.drawable.truck_delivery);
        tabLayout.getTabAt(1).setText("SHIPPING");

        tabLayout.getTabAt(2).setIcon(R.drawable.google);
        tabLayout.getTabAt(2).setText("PHOTOS");

        tabLayout.getTabAt(3).setIcon(R.drawable.equal);
        tabLayout.getTabAt(3).setText("SIMILAR");
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        Log.v(TAG, "Rainie : bundle = " + bundle);

        infoFragment mInfoFragment = new infoFragment();
        mInfoFragment.setArguments(bundle);
        adapter.addFrag(mInfoFragment, "PRODUCT");

        shippingFragment mShippingFragment = new shippingFragment();
        mShippingFragment.setArguments(bundle);
        adapter.addFrag(mShippingFragment, "SHIPPING");

//        shippingFragment mShippingFragment2 = new shippingFragment();
//        mShippingFragment2.setArguments(bundle);
//        adapter.addFrag(mShippingFragment2, "SHIPPING");

        photosFragment mPhotosFragment = new photosFragment();
        mPhotosFragment.setArguments(bundle);
        adapter.addFrag(mPhotosFragment, "PHOTOS");

        similarFragment mSimilarFragment = new similarFragment();
        mSimilarFragment.setArguments(bundle);
        adapter.addFrag(mSimilarFragment, "SIMILAR");

        viewPager.setAdapter(adapter);
        Log.v(TAG, "Rainie : Finish setupViewPager()");
    }

    public void receiveData()
    {
        Intent mIntent = getIntent();

        String receivedJsonObjItem_str = mIntent.getStringExtra("jsonObjItem_str");
        String receivedItemId = mIntent.getStringExtra("itemId");
        String receivedTitle = mIntent.getStringExtra("title");
        String receivedCard_product_img = mIntent.getStringExtra("card_product_img");
        String receivedCard_zipcode = mIntent.getStringExtra("card_zipcode");
        String receivedCard_shipping_cost = mIntent.getStringExtra("card_shipping_cost");
        String receivedCard_condition = mIntent.getStringExtra("card_condition");
        String receivedCard_price = mIntent.getStringExtra("card_price");
        String receivedCard_wish = mIntent.getStringExtra("card_wish");

        card_product_img = receivedCard_product_img;
        card_zipcode = receivedCard_zipcode;
        card_shipping_cost = receivedCard_shipping_cost;
        card_condition = receivedCard_condition;
        card_price = receivedCard_price;
        card_wish = receivedCard_wish;
        itemId = receivedItemId;
        itemTitle = receivedTitle;
        jsonObjItem_str = receivedJsonObjItem_str;

//        Log.v(TAG, "Rainie: mIntent : " + mIntent);
//        Log.v(TAG, "Rainie: receivedJsonObjItem_str : " + receivedJsonObjItem_str);
//        Log.v(TAG, "Rainie: receivedItemId : " + receivedItemId);
//        Log.v(TAG, "Rainie: receivedTitle : " + receivedTitle);
//        Log.v(TAG, "Rainie: receivedImg : " + receivedCard_product_img);
//        Log.v(TAG, "Rainie: receivedZipcode : " + receivedCard_zipcode);
//        Log.v(TAG, "Rainie: receivedCost : " + receivedCard_shipping_cost);
//        Log.v(TAG, "Rainie: receivedCondition : " + receivedCard_condition);
//        Log.v(TAG, "Rainie: receivedPrice : " + receivedCard_price);
//        Log.v(TAG, "Rainie: receivedWish : " + receivedCard_wish);

        setTitle(receivedTitle);

        if (mSharedPreferences.contains(itemId)) {
            wish_button.setBackground(getResources().getDrawable(R.drawable.circle_wish_remove));
        } else {
            wish_button.setBackground(getResources().getDrawable(R.drawable.circle_wish_add));
        }
        requestDetails2(receivedTitle, receivedItemId);
    }



    // mTitle is short title
    public void requestDetails2(String mTitle, String mItemId) {
        String mUrl = "http://chihhuiy-app.us-east-2.elasticbeanstalk.com/?itemId_single=" + mItemId;
        Log.v(TAG, "Rainie : single api : " + mUrl);
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
//                    mProgressBar.setVisibility(View.GONE);
//                    mProgressBarMsg.setVisibility(View.GONE);

                    jsonObject_detail = new JSONObject(response);
                    Log.v(TAG, "Rainie: Detail JSON : " + jsonObject_detail.toString());
                    String full_title = jsonObject_detail.getJSONObject("Item").getString("Title");

                    price_item = jsonObject_detail.getJSONObject("Item").getJSONObject("CurrentPrice").getString("Value");

                    // DONE : transfer single jsonObjItem_str
                    bundle.putString("jsonObjectItem", jsonObjItem_str);
                    Log.v(TAG, "Rainie : bundle jsonObjectItem = " + jsonObjItem_str);
                    bundle.putString("jsonObject_detail", jsonObject_detail.toString());
                    Log.v(TAG, "Rainie : bundle jsonObject_detail = " + jsonObject_detail.toString());

                        // DONE : request photo api
                        String mPhotoUrl = "http://chihhuiy-app.us-east-2.elasticbeanstalk.com/?keyword_photo=" + full_title;
                        Log.v(TAG, "Rainie : mPhotoUrl = " + mPhotoUrl);
                        RequestQueue queue2 = Volley.newRequestQueue(mContext);
                        StringRequest stringRequest2 = new StringRequest(Request.Method.GET, mPhotoUrl, new Response.Listener<String>()
                        {
                            @Override
                            public void onResponse(String response)
                            {
                                try
                                {
                                    jsonObject_photo = new JSONObject(response);
                                    Log.v(TAG, "Rainie: Photo JSON : " + jsonObject_photo.toString());
                                    bundle.putString("jsonObject_photo", jsonObject_photo.toString());
                                    Log.v(TAG, "Rainie : bundle jsonObject_photo = " + jsonObject_photo.toString());

                                    // DONE : request similar api
                                    String mSimilarUrl = "http://chihhuiy-app.us-east-2.elasticbeanstalk.com/?similar=true&itemId_similar=" + itemId;
                                    Log.v(TAG, "Rainie : mSimilarUrl = " + mSimilarUrl);
                                    RequestQueue queue3 = Volley.newRequestQueue(mContext);
                                    StringRequest stringRequest3 = new StringRequest(Request.Method.GET, mSimilarUrl, new Response.Listener<String>()
                                    {
                                        @Override
                                        public void onResponse(String response)
                                        {
                                            try
                                            {
                                                jsonObject_similar = new JSONObject(response);
                                                Log.v(TAG, "Rainie: Similar JSON : " + jsonObject_similar.toString());

                                                bundle.putString("jsonObject_similar", jsonObject_similar.toString());
                                                Log.v(TAG, "Rainie : bundle jsonObject_similar = " + jsonObject_similar.toString());

                                                mProgressBar.setVisibility(View.GONE);
                                                mProgressBarMsg.setVisibility(View.GONE);

                                                // go to 4 TAB page
                                                Log.v(TAG, "Rainie : Start setupViewPager()");
                                                setupViewPager(mViewPager);
//                                                tabLayout.setupWithViewPager(mViewPager);
                                                setupTabIcons();
                                            }
                                            catch (JSONException e)
                                            {
                                                mProgressBar.setVisibility(View.GONE);
                                                mProgressBarMsg.setVisibility(View.GONE);
                                                Toast.makeText(DetailsActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    },
                                            new Response.ErrorListener()
                                            {
                                                @Override
                                                public void onErrorResponse(VolleyError error)
                                                {
                                                    mProgressBar.setVisibility(View.GONE);
                                                    mProgressBarMsg.setVisibility(View.GONE);
                                                    Toast.makeText(DetailsActivity.this, "No Network connection!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                    queue3.add(stringRequest3);
                                }
                                catch (JSONException e)
                                {
                                    mProgressBar.setVisibility(View.GONE);
                                    mProgressBarMsg.setVisibility(View.GONE);
                                    Toast.makeText(DetailsActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {
                                        mProgressBar.setVisibility(View.GONE);
                                        mProgressBarMsg.setVisibility(View.GONE);
                                        Toast.makeText(DetailsActivity.this, "No Network connection!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                        queue2.add(stringRequest2);
                }
                catch (JSONException e)
                {
                    mProgressBar.setVisibility(View.GONE);
                    mProgressBarMsg.setVisibility(View.GONE);
                    Toast.makeText(DetailsActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        mProgressBar.setVisibility(View.GONE);
                        mProgressBarMsg.setVisibility(View.GONE);
                        Toast.makeText(DetailsActivity.this, "No Network connection!", Toast.LENGTH_SHORT).show();
                    }
                });
        queue.add(stringRequest);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }


        public void addFrag(Fragment fragment, String title) {
            Log.v(TAG, "Rainie : addFrag()");
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }


        @Override
        public Fragment getItem(int position) {
            Log.v(TAG, "Rainie : getItem() = " + position);
            return mFragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
//            Log.v(TAG, "Rainie : getPageTitle() = " + position);
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
//            Log.v(TAG, "Rainie : getCount() = " + mFragmentList.size());
            return mFragmentList.size();
        }
    }

    public void shareToFacebook() throws JSONException {

        String url = jsonObject_detail.getJSONObject("Item").getString("ViewItemURLForNaturalSearch");
        String quote = "Buy " + itemTitle + " for $" + price_item + " from Ebay!";
        String link = "https://www.facebook.com/dialog/share?app_id=412937185919670&display=popup&href=" + url + "&quote=" + urlEncode(quote) + "&hashtag=%23CSCI571Spring2019Ebay";

        Uri uriUrl = Uri.parse(link);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        mContext.startActivity(launchBrowser);

    }

    private String urlEncode(String s) {
        try
        {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            Toast.makeText(DetailsActivity.this, "JSONException", Toast.LENGTH_SHORT).show();
            return "";
        }
    }

    public void addToWishList() {
        if (mSharedPreferences.contains(itemId)) {
            spEditor.remove(itemId);
            spEditor.apply();
            Log.v(TAG, "Rainie : remove wish size = " + mSharedPreferences.getAll().size());
            wish_button.setBackground(getDrawable(R.drawable.circle_wish_add));
            Toast.makeText(this, itemTitle + " was removed from wish list", Toast.LENGTH_SHORT).show();
        } else {
            packetDetail = new String[9];
            packetDetail[0] = itemId;
            packetDetail[1] = card_product_img;
            packetDetail[2] = itemTitle;
            packetDetail[3] = card_zipcode;
            packetDetail[4] = card_shipping_cost;
            packetDetail[5] = card_condition;
            packetDetail[6] = card_price;
            packetDetail[7] = card_wish;
            packetDetail[8] = jsonObjItem_str;

            Gson gson = new Gson();
            String myStr = gson.toJson(packetDetail);

            spEditor.putString(itemId, myStr);
            spEditor.apply();
            Log.v(TAG, "Rainie : add wish size = " + mSharedPreferences.getAll().size());

            wish_button.setBackground(getDrawable(R.drawable.circle_wish_remove));
            Toast.makeText(this, itemTitle + " was added to wish list", Toast.LENGTH_SHORT).show();
        }
    }
}
