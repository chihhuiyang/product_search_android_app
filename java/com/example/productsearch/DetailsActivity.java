package com.example.productsearch;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
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

    private ViewPager mViewPager;
    private TabLayout tabLayout;
    private Menu menu;

    public Bundle bundle = new Bundle();
    public JSONObject jsonObject;
    public JSONObject placeDetails;

    public JSONObject jsonObject_detail;
    private String itemId;
    private String itemTitle;
    private String jsonObjItem_str;


    public String[] saveStr;
    private String placeId;
    private String placeName;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
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

        mProgressBarMsg = (TextView) findViewById(R.id.progress_bar_message_detail);;
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar_detail);

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
                    shareToTwitter();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                return true;

            case R.id.like:
                try
                {
                    addToFavorite();
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupTabIcons() {
        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("PRODUCT");
        tabOne.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
//        tabOne.setGravity(Gravity.CENTER_HORIZONTAL);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.information_variant, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("SHIPPING");
        tabTwo.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tabTwo.setGravity(Gravity.CENTER_HORIZONTAL);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.truck_delivery, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("PHOTOS");
        tabThree.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.google, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("SIMILAR");
        tabFour.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.equal, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void setupViewPager(ViewPager viewPager)
    {
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(getSupportFragmentManager());
        Log.v(TAG, "Rainie : bundle = " + bundle);


        infoFragment mInfoFragment = new infoFragment();
        mInfoFragment.setArguments(bundle);
        adapter.addFrag(mInfoFragment, "ONE");

        infoFragment mInfoFragment2 = new infoFragment();
        mInfoFragment2.setArguments(bundle);
        adapter.addFrag(mInfoFragment2, "TWO");

        infoFragment mInfoFragment3 = new infoFragment();
        mInfoFragment3.setArguments(bundle);
        adapter.addFrag(mInfoFragment3, "THREE");

        infoFragment mInfoFragment4 = new infoFragment();
        mInfoFragment4.setArguments(bundle);
        adapter.addFrag(mInfoFragment4, "FOUR");



//        infoFragment mInfoFragment = new infoFragment();
//        mInfoFragment.setArguments(bundle);
//        Spannable infoSpan = new SpannableString("   PRODUCT");
//        Drawable infoImage = getBaseContext().getDrawable(R.drawable.information_variant);
//        infoImage.setBounds(25, 25, 75, 75);
//        ImageSpan infoImageSpan = new ImageSpan(infoImage, ImageSpan.ALIGN_BASELINE);
//        infoSpan.setSpan(infoImageSpan, 0,1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        adapter.addFragment(mInfoFragment, infoSpan);
//
//        photosFragment mPhotosFragment = new photosFragment();
//        mPhotosFragment.setArguments(bundle);
//        Spannable photosSpan = new SpannableString("   SHIPPING");
//        Drawable photosImage = getBaseContext().getDrawable(R.drawable.truck_delivery);
//        photosImage.setBounds(25, 25, 75, 75);
//        ImageSpan photosImageSpan = new ImageSpan(photosImage, ImageSpan.ALIGN_BASELINE);
//        photosSpan.setSpan(photosImageSpan, 0,1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        adapter.addFragment(mPhotosFragment, photosSpan);
//
//        mapFragment mMapFragment = new mapFragment();
//        mMapFragment.setArguments(bundle);
//        Spannable mapSpan = new SpannableString("   PHOTOS");
//        Drawable mapImage = getBaseContext().getDrawable(R.drawable.google);
//        mapImage.setBounds(25, 25, 75, 75);
//        ImageSpan mapImageSpan = new ImageSpan(mapImage, ImageSpan.ALIGN_BASELINE);
//        mapSpan.setSpan(mapImageSpan, 0,1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        adapter.addFragment(mMapFragment, mapSpan);
//
//        reviewsFragment mReviewsFragment = new reviewsFragment();
//        mReviewsFragment.setArguments(bundle);
//        Spannable reviewsSpan = new SpannableString("   SIMILAR");
//        Drawable reviewsImage = getBaseContext().getDrawable(R.drawable.equal);
//        reviewsImage.setBounds(25, 25, 75, 75);
//        ImageSpan reviewsImageSpan = new ImageSpan(reviewsImage, ImageSpan.ALIGN_BASELINE);
//        reviewsSpan.setSpan(reviewsImageSpan, 0,1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        adapter.addFragment(mReviewsFragment, reviewsSpan);

        viewPager.setAdapter(adapter);
        Log.v(TAG, "Rainie : Finish setupViewPager()");
    }

    public void receiveData() throws JSONException
    {
        Intent mIntent = getIntent();
//        String receivedData = mIntent.getStringExtra("jsonObj");
//        jsonObject = new JSONObject(receivedData);
//        placeDetails = jsonObject.getJSONObject("result");
//        placeId = placeDetails.getString("place_id");

//        requestDetails(receivedName, receivedPlace);


        String receivedTitle = mIntent.getStringExtra("title");
        String receivedItemId = mIntent.getStringExtra("itemId");
        String receivedJsonObjItem_str = mIntent.getStringExtra("jsonObjItem_str");

        itemId = receivedItemId;
        itemTitle = receivedTitle;
        jsonObjItem_str = receivedJsonObjItem_str;

        Log.v(TAG, "Rainie: mIntent : " + mIntent);
        Log.v(TAG, "Rainie: receivedTitle : " + receivedTitle);
        Log.v(TAG, "Rainie: receivedItemId : " + receivedItemId);
        Log.v(TAG, "Rainie: receivedJsonObjItem_str : " + receivedJsonObjItem_str);

        setTitle(receivedTitle);


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


                    // TODO : transfer single jsonObjItem_str
                    bundle.putString("jsonObjectItem", jsonObjItem_str);

                    bundle.putString("jsonObject_detail", jsonObject_detail.toString());


                    Log.v(TAG, "Rainie : Start setupViewPager()");
                    setupViewPager(mViewPager);

                    setupTabIcons();
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


    public void requestDetails(String mName, String mPlaceId)
    {
//        mProgressDialog = new ProgressDialog(this);
//        mProgressDialog.setMessage("Fetching details");
//        mProgressDialog.show();

        String mUrl = "https://maps.googleapis.com/maps/api/place/details/json?";
        mUrl += "placeid=" + mPlaceId;
        mUrl += "&key=AIzaSyC9HBExGTftsTmeBjHXLucUi5NH2QXCQkY";
        Log.v(TAG, "Rainie : mUrl = " + mUrl);

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

                    JSONObject jsonObject = new JSONObject(response);
                    //System.out.println(jsonObject.toString());
                    Log.v(TAG, "Rainie: JSON : " + jsonObject.toString());


                        placeDetails = jsonObject.getJSONObject("result");
                        placeId = placeDetails.getString("place_id");


//                    newIntent.putExtra("jsonObj", jsonObject.toString());
//                    redirect();
//                    mProgressDialog.dismiss();

                                placeName = placeDetails.getString("name");
//                                setTitle(placeName);
                                bundle.putString("jsonObj", jsonObject.toString());

                                Log.v(TAG, "Rainie : Start setupViewPager()");
                                setupViewPager(mViewPager);

                                setupTabIcons();
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
            Log.v(TAG, "Rainie : getPageTitle() = " + position);
            return mFragmentTitleList.get(position);
        }

        @Override
        public int getCount() {
            Log.v(TAG, "Rainie : getCount() = " + mFragmentList.size());
            // Show 2 total pages.
            return mFragmentList.size();
        }
    }

    public void shareToTwitter() throws JSONException
    {
        String name = placeDetails.getString("name");
        String address = placeDetails.getString("formatted_address");
        String website = placeDetails.getString("website");
        String myMessage = "Check out " + name + " located at " + address;
        if (placeDetails.has("website"))
        {
            myMessage += '\n' + "Website: " + website;
        }

        Intent tweet = new Intent(Intent.ACTION_SEND);
        tweet.putExtra(Intent.EXTRA_TEXT, "This is a new tweet.");
        tweet.setType("text/plain");

        PackageManager packManager = getPackageManager();
        List<ResolveInfo> resolvedInfoList = packManager.queryIntentActivities(tweet, PackageManager.MATCH_DEFAULT_ONLY);

        boolean resolved = false;
        for (ResolveInfo resolveInfo : resolvedInfoList)
        {
            if (resolveInfo.activityInfo.packageName.startsWith("com.twitter.android"))
            {
                tweet.setClassName(
                        resolveInfo.activityInfo.packageName,
                        resolveInfo.activityInfo.name);
                resolved = true;
                break;
            }
        }
        if (resolved)
        {
            startActivity(tweet);
        }
        else
        {
            Intent i = new Intent();
            i.putExtra(Intent.EXTRA_TEXT, myMessage);
            i.setAction(Intent.ACTION_VIEW);
            i.setData(Uri.parse("https://twitter.com/intent/tweet?text=" + urlEncode(myMessage)));
            startActivity(i);
        }
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

    public void addToFavorite() throws JSONException
    {
        if (mSharedPreferences.contains(placeId))
        {
            spEditor.remove(placeId);
            spEditor.apply();

            menu.getItem(1).setIcon(R.drawable.cart_plus);
            Toast.makeText(this, placeName + " was removed from favorites", Toast.LENGTH_SHORT).show();
        }
        else
        {
            saveStr = new String[5];
            saveStr[0] = placeId;
            saveStr[1] = placeDetails.getString("icon");
            saveStr[2] = placeDetails.getString("name");
            saveStr[3] = placeDetails.getString("vicinity");
            saveStr[4] = "yes";

            Gson gson = new Gson();
            String myStr = gson.toJson(saveStr);

            spEditor.putString(placeId, myStr);
            spEditor.apply();

            menu.getItem(1).setIcon(R.drawable.cart_remove);
            Toast.makeText(this, placeName + " was added to favorites", Toast.LENGTH_SHORT).show();
        }
    }
}
