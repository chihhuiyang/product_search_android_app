package com.example.productsearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
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
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class DetailsActivity extends AppCompatActivity
{

    public TextView mProgressBarMsg;
    public ProgressBar mProgressBar;


    private static final String TAG = "DetailsActivity";
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private MainActivity.SectionsPagerAdapter mSectionsPagerAdapter;

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor spEditor;

    private Button wish_button;
    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Menu menu;

    public Bundle bundle = new Bundle();

//    public JSONObject placeDetails;

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


    public String[] saveStr;


//    private String placeId;
//    private String placeName;

    Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {

        mContext = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Log.v(TAG, "Rainie: onCreate()");

        setProgressBarVisibility(true);
        setProgressBarIndeterminateVisibility(true);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSharedPreferences = this.getSharedPreferences("mySP", Context.MODE_PRIVATE);
        spEditor = mSharedPreferences.edit();




        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.detailsContainer);

        mProgressBarMsg = (TextView) findViewById(R.id.progress_bar_message_detail);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_detail);

        wish_button = (Button) findViewById(R.id.wish_button);




        tabLayout = (TabLayout) findViewById(R.id.detailsTabs);
        tabLayout.setupWithViewPager(mViewPager);
//        tabLayout.setTabMode(MODE_SCROLLABLE);

//        setupTabIcons();


        try
        {
            receiveData();
//            placeName = placeDetails.getString("name");
//            setTitle(placeName);
//            bundle.putString("jsonObj", jsonObject.toString());
//            setupViewPager(mViewPager);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        wish_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                addToFavorite();    // TODO :
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        this.menu = menu;

        if (mSharedPreferences.contains(itemId))
        {
            menu.getItem(1).setIcon(R.drawable.cart_remove);
        }

        return super.onCreateOptionsMenu(menu);
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

            case R.id.share:
                try
                {
                    shareToFacebook();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                return true;

            case R.id.like:
//                try
//                {
                    addToFavorite();
//                }
//                catch (JSONException e)
//                {
//                    e.printStackTrace();
//                }
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

        shippingFragment mShippingFragment2 = new shippingFragment();
        mShippingFragment2.setArguments(bundle);
        adapter.addFrag(mShippingFragment2, "SHIPPING");

//        photosFragment mPhotosFragment = new photosFragment();
//        mPhotosFragment.setArguments(bundle);
//        adapter.addFrag(mPhotosFragment, "PHOTOS");

        similarFragment mSimilarFragment = new similarFragment();
        mSimilarFragment.setArguments(bundle);
        adapter.addFrag(mSimilarFragment, "SIMILAR");


        viewPager.setAdapter(adapter);
        Log.v(TAG, "Rainie : Finish setupViewPager()");
    }

    public void receiveData() throws JSONException
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

        Log.v(TAG, "Rainie: mIntent : " + mIntent);
        Log.v(TAG, "Rainie: receivedJsonObjItem_str : " + receivedJsonObjItem_str);
        Log.v(TAG, "Rainie: receivedItemId : " + receivedItemId);
        Log.v(TAG, "Rainie: receivedTitle : " + receivedTitle);
        Log.v(TAG, "Rainie: receivedImg : " + receivedCard_product_img);
        Log.v(TAG, "Rainie: receivedZipcode : " + receivedCard_zipcode);
        Log.v(TAG, "Rainie: receivedCost : " + receivedCard_shipping_cost);
        Log.v(TAG, "Rainie: receivedCondition : " + receivedCard_condition);
        Log.v(TAG, "Rainie: receivedPrice : " + receivedCard_price);
        Log.v(TAG, "Rainie: receivedWish : " + receivedCard_wish);

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

        String mUrl = "http://chihhuiy-nodejs.us-east-2.elasticbeanstalk.com/?itemId_single=" + mItemId;

        Log.v(TAG, "Rainie : single api : " + mUrl);

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

                    mProgressBar.setVisibility(View.GONE);
                    mProgressBarMsg.setVisibility(View.GONE);

                    jsonObject_detail = new JSONObject(response);
                    Log.v(TAG, "Rainie: Detail JSON : " + jsonObject_detail.toString());
                    String full_title = jsonObject_detail.getJSONObject("Item").getString("Title");

                    price_item = jsonObject_detail.getJSONObject("Item").getJSONObject("CurrentPrice").getString("Value");

                    // TODO : transfer single jsonObjItem_str
                    bundle.putString("jsonObjectItem", jsonObjItem_str);
                    Log.v(TAG, "Rainie : bundle jsonObjectItem = " + jsonObjItem_str);
                    bundle.putString("jsonObject_detail", jsonObject_detail.toString());
                    Log.v(TAG, "Rainie : bundle jsonObject_detail = " + jsonObject_detail.toString());

                        // TODO : request photo api
                        // Instantiate the RequestQueue.
                        String mPhotoUrl = "http://chihhuiy-nodejs.us-east-2.elasticbeanstalk.com/?keyword_photo=" + full_title;

                        Log.v(TAG, "Rainie : mPhotoUrl = " + mPhotoUrl);

                        RequestQueue queue2 = Volley.newRequestQueue(mContext);

                        // Request a string response from the provided URL.
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



                                    // TODO : request similar api
                                    String mSimilarUrl = "http://chihhuiy-nodejs.us-east-2.elasticbeanstalk.com/?similar=true&itemId_similar=" + itemId;

                                    Log.v(TAG, "Rainie : mSimilarUrl = " + mSimilarUrl);

                                    RequestQueue queue3 = Volley.newRequestQueue(mContext);

                                    // Request a string response from the provided URL.
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

                                                // go to 4 TAB page
                                                Log.v(TAG, "Rainie : Start setupViewPager()");
                                                setupViewPager(mViewPager);
//                                                tabLayout.setupWithViewPager(mViewPager);
                                                setupTabIcons();
                                            }
                                            catch (JSONException e)
                                            {
                                                bundle.putString("jsonObject_similar", "");

                                                // go to 4 TAB page
                                                Log.v(TAG, "Rainie : Start setupViewPager()");
                                                setupViewPager(mViewPager);
                                                setupTabIcons();

                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                            new Response.ErrorListener()
                                            {
                                                @Override
                                                public void onErrorResponse(VolleyError error)
                                                {

                                                    Toast.makeText(DetailsActivity.this, "No connection! Please check your internet connection.", Toast.LENGTH_SHORT).show();
                                                    System.out.println("Request error!");
                                                    System.out.println(error);
                                                    Log.v(TAG, "Rainie: VolleyError");
                                                }
                                            });
                                    queue3.add(stringRequest3);



                                }
                                catch (JSONException e)
                                {
                                    bundle.putString("jsonObject_photo", "");
                                    bundle.putString("jsonObject_similar", "");

                                    // go to 4 TAB page
                                    Log.v(TAG, "Rainie : Start setupViewPager()");
                                    setupViewPager(mViewPager);
                                    setupTabIcons();

                                    e.printStackTrace();
                                }
                            }
                        },
                                new Response.ErrorListener()
                                {
                                    @Override
                                    public void onErrorResponse(VolleyError error)
                                    {

                                        Toast.makeText(DetailsActivity.this, "No connection! Please check your internet connection.", Toast.LENGTH_SHORT).show();
                                        System.out.println("Request error!");
                                        System.out.println(error);
                                        Log.v(TAG, "Rainie: VolleyError");
                                    }
                                });
                        queue2.add(stringRequest2);










                    // go to 4 TAB page
//                    Log.v(TAG, "Rainie : Start setupViewPager()");
//                    setupViewPager(mViewPager);
//                    setupTabIcons();
                }
                catch (JSONException e)
                {
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

                        Toast.makeText(DetailsActivity.this, "No connection! Please check your internet connection.", Toast.LENGTH_SHORT).show();

                        System.out.println("Request error!");
                        System.out.println(error);

                        Log.v(TAG, "Rainie: VolleyError");
                        mProgressBar.setVisibility(View.GONE);
                        mProgressBarMsg.setVisibility(View.GONE);
                    }
                });
        queue.add(stringRequest);
    }




    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter
    {
        private final List<Fragment> mFragmentList = new ArrayList<>();
//        private final List<Spannable> mFragmentTitleList = new ArrayList<>();

        private final List<String> mFragmentTitleList = new ArrayList<>();


        public SectionsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

//        public void addFragment(Fragment fragment, Spannable title)
//        {
//            mFragmentList.add(fragment);
//            mFragmentTitleList.add(title);
//        }


        public void addFrag(Fragment fragment, String title) {
            Log.v(TAG, "Rainie : addFrag()");
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public void addTitleOnly(String title) {
            mFragmentTitleList.add(title);
        }

        public void addFragmentOnly(Fragment fragment) {
            mFragmentList.add(fragment);
        }

        @Override
        public Fragment getItem(int position)
        {
            Log.v(TAG, "Rainie : getItem() = " + position);
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return mFragmentList.get(position);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position)
        {
//            Log.v(TAG, "Rainie : getPageTitle() = " + position);
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
//            Log.v(TAG, "Rainie : getCount() = " + mFragmentList.size());
            // Show 2 total pages.
            return mFragmentList.size();
        }
    }

    public void shareToFacebook() throws JSONException
    {

        String url = jsonObject_detail.getJSONObject("Item").getString("ViewItemURLForNaturalSearch");
        String quote = "Buy " + itemTitle + " for $" + price_item + " from Ebay!";
        String link = "https://www.facebook.com/dialog/share?app_id=412937185919670&display=popup&href=" + url + "&quote=" + urlEncode(quote) + "&hashtag=%23CSCI571Spring2019Ebay";

        Uri uriUrl = Uri.parse(link);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        mContext.startActivity(launchBrowser);

    }

    private String urlEncode(String s)
    {
        try
        {
            return URLEncoder.encode(s, "UTF-8");
        }
        catch (UnsupportedEncodingException e)
        {
            return "";
        }
    }

    public void addToFavorite()
    {
        if (mSharedPreferences.contains(itemId))
        {
            spEditor.remove(itemId);
            spEditor.apply();
            Log.v(TAG, "Rainie : remove wish size = " + mSharedPreferences.getAll().size());
            wish_button.setBackground(getResources().getDrawable(R.drawable.circle_wish_add));
            menu.getItem(1).setIcon(R.drawable.cart_plus);
            Toast.makeText(this, itemTitle + " was removed from wish list", Toast.LENGTH_SHORT).show();
        }
        else
        {
            saveStr = new String[9];
            saveStr[0] = itemId;
            saveStr[1] = card_product_img;
            saveStr[2] = itemTitle;
            saveStr[3] = card_zipcode;
            saveStr[4] = card_shipping_cost;
            saveStr[5] = card_condition;
            saveStr[6] = card_price;
            saveStr[7] = card_wish;
            saveStr[8] = jsonObjItem_str;

            Gson gson = new Gson();
            String myStr = gson.toJson(saveStr);

            spEditor.putString(itemId, myStr);
            spEditor.apply();
            Log.v(TAG, "Rainie : add wish size = " + mSharedPreferences.getAll().size());

            wish_button.setBackground(getResources().getDrawable(R.drawable.circle_wish_remove));
            menu.getItem(1).setIcon(R.drawable.cart_remove);
            Toast.makeText(this, itemTitle + " was added to wish list", Toast.LENGTH_SHORT).show();
        }
    }
}
