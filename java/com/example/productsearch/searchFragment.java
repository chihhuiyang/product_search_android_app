package com.example.productsearch;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
//import com.google.android.gms.location.places.ui.PlaceAutocomplete;
//import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

import static android.content.Context.LOCATION_SERVICE;

public class searchFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener
{
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private static final String TAG = "searchFragment";
    public final String[] categories = {
            "Default",
            "Art",
            "Baby",
            "Books",
            "Clothing, Shoes & Accessories",
            "Computers/Tablets & Networking",
            "Health & Beauty",
            "Music",
            "Video Games & Consoles"};
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(25, -139), new LatLng(40, -60));

    public EditText mKeyword;
    public Spinner mSpinner;
    public EditText mDistance;

    public CheckBox mNew;
    public CheckBox mUsed;
    public CheckBox mUnspecified;
    public CheckBox mLPickup;
    public CheckBox mFree;

    public CheckBox mInput_nearby;

    public TextView mFrom;
    public RadioGroup mRadioGroup;
    public RadioButton mCurrentLocation;
    public RadioButton mOtherLocation;
    public AutoCompleteTextView mInputLocation;

    public Button mSearchButton;
    public Button mClearButton;
    public TextView mKeywordError;
    public TextView mLocationError;

//    public ProgressDialog mProgressDialog;

    public Intent mIntent;
//    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GoogleApiClient mGoogleApiClient;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        mIntent = new Intent(this.getActivity(), ResultsActivity.class);
        Log.v(TAG, "Rainie: onCreateView() , mIntent : " + mIntent + " , this.getActivity() : " + this.getActivity());

        //set spinner for categories
        mSpinner = (Spinner)view.findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mSpinner.setAdapter(adapter);

        //initialization
        mKeyword = (EditText)view.findViewById(R.id.keyword);

        // 5 checkbox
        mNew = (CheckBox) view.findViewById(R.id.input_new);
        mUsed = (CheckBox) view.findViewById(R.id.input_used);
        mUnspecified = (CheckBox) view.findViewById(R.id.input_unspecified);
        mLPickup = (CheckBox) view.findViewById(R.id.input_pickup);
        mFree = (CheckBox) view.findViewById(R.id.input_free);

        mInput_nearby = (CheckBox) view.findViewById(R.id.input_nearby);


        // invisible
        mDistance = (EditText)view.findViewById(R.id.distance);
        mFrom = (TextView) view.findViewById(R.id.textView_from);
        mRadioGroup = (RadioGroup)view.findViewById(R.id.radioGroup);
        mCurrentLocation = (RadioButton) view.findViewById(R.id.currentLocation);
        mOtherLocation = (RadioButton) view.findViewById(R.id.otherLocation);
        mInputLocation = (AutoCompleteTextView) view.findViewById(R.id.inputLocation);


        mSearchButton = (Button)view.findViewById(R.id.searchButton);
        mClearButton = (Button)view.findViewById(R.id.clearButton);
        mKeywordError = (TextView)view.findViewById(R.id.keywordError);
        mLocationError = (TextView)view.findViewById(R.id.locationError);

        mGoogleApiClient = new GoogleApiClient
                .Builder(this.getActivity())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(this.getActivity(), this)
                .build();



        // enable nearby search
        mInput_nearby.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        checkNearby();
                    }
                }
        );



//        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this.getActivity(), mGoogleApiClient, LAT_LNG_BOUNDS, null);
//        mInputLocation.setAdapter(mPlaceAutocompleteAdapter);
        mInputLocation.setOnItemClickListener(mAutocompleteClickListener);

        //validation
        //mKeyword.requestFocus();
        mCurrentLocation.setChecked(true);
        mInputLocation.setEnabled(false);
        mCurrentLocation.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        formValidation();
                    }
                }
        );

        mOtherLocation.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        formValidation();
                    }
                }
        );

        mSearchButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        searchValidation(mKeyword.getText().toString(), mInputLocation.getText().toString());
                    }
                }
        );

        mClearButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        clearInputs();
                    }
                }
        );

        if (view != null)
        {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        return view;
    }

    public AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener()
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id)
        {
            //hide the keyboard
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    };


    public void checkNearby() {
        if (mInput_nearby.isChecked()) {
            mDistance.setVisibility(View.VISIBLE);
            mFrom.setVisibility(View.VISIBLE);
            mRadioGroup.setVisibility(View.VISIBLE);
            mInputLocation.setVisibility(View.VISIBLE);
        } else {
            mDistance.setVisibility(View.GONE);
            mFrom.setVisibility(View.GONE);
            mRadioGroup.setVisibility(View.GONE);
            mInputLocation.setVisibility(View.GONE);

            // clear
            mDistance.setText("");
            mCurrentLocation.setChecked(true);
            mInputLocation.setText("");
            mInputLocation.setEnabled(false);
            mLocationError.setVisibility(View.GONE);
        }
    }


    public void formValidation()
    {
        if (mCurrentLocation.isChecked())
        {
            mInputLocation.setText("");
            mInputLocation.setEnabled(false);
        }
        else
        {
            mInputLocation.setEnabled(true);
        }
    }

    public void searchValidation(String keyword, String inputLocation)
    {
        if (mCurrentLocation.isChecked())
        {
            if (keyword.isEmpty() || keyword.trim().matches(""))
            {
                mKeywordError.setVisibility(View.VISIBLE);
                Toast.makeText(this.getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mKeywordError.setVisibility(View.GONE);
                getInputs();
            }
        }
        else
        {
            if (keyword.isEmpty() || keyword.trim().matches("")
                    || inputLocation.isEmpty() || inputLocation.trim().matches(""))
            {
                if (keyword.isEmpty() || keyword.trim().matches(""))
                {
                    mKeywordError.setVisibility(View.VISIBLE);
                }
                else
                {
                    mKeywordError.setVisibility(View.GONE);
                }
                if (inputLocation.isEmpty() || inputLocation.trim().matches(""))
                {
                    mLocationError.setVisibility(View.VISIBLE);
                }
                else
                {
                    mLocationError.setVisibility(View.GONE);
                }
                Toast.makeText(this.getActivity(), "Please fix all fields with errors", Toast.LENGTH_SHORT).show();
            }
            else
            {
                mKeywordError.setVisibility(View.GONE);
                mLocationError.setVisibility(View.GONE);
                getInputs();
            }
        }




    }

    public void clearInputs()
    {
        mKeywordError.setVisibility(View.GONE);
        mLocationError.setVisibility(View.GONE);
        mKeyword.setText("");
        mSpinner.setSelection(0);
        mDistance.setText("");
        mCurrentLocation.setChecked(true);
        mInputLocation.setText("");
        mInputLocation.setEnabled(false);
    }

    public void getInputs()
    {
//        mProgressDialog = new ProgressDialog(this.getActivity());
//        mProgressDialog.setMessage("Searching Products...");
//        mProgressDialog.show();


        String ipUrl = "http://ip-api.com/json";
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, ipUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response) {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    Log.v(TAG, "Rainie : jsonObject = " + jsonObject.toString());
                    mIntent.putExtra("jsonObj", jsonObject.toString());

                    String currentZip = jsonObject.getString("zip");
                    Log.v(TAG, "Rainie : currentZip = " + currentZip);




                    String keywordVal = mKeyword.getText().toString();
                    String categoryVal = mSpinner.getSelectedItem().toString();


                    if (categoryVal.equals("Default")) {
                        categoryVal = "default";
                    } else if (categoryVal.equals("Art")) {
                        categoryVal = "550";
                    } else if (categoryVal.equals("Baby")) {
                        categoryVal = "2984";
                    } else if (categoryVal.equals("Books")) {
                        categoryVal = "267";
                    } else if (categoryVal.equals("Clothing, Shoes & Accessories")) {
                        categoryVal = "11450";
                    } else if (categoryVal.equals("Computers/Tablets & Networking")) {
                        categoryVal = "58058";
                    } else if (categoryVal.equals("Health & Beauty")) {
                        categoryVal = "26395";
                    } else if (categoryVal.equals("Music")) {
                        categoryVal = "11233";
                    } else if (categoryVal.equals("Video Games & Consoles")) {
                        categoryVal = "1249";
                    }


                    String url_params = "http://chihhuiy-nodejs.us-east-2.elasticbeanstalk.com/?";
                    url_params += "category=" + categoryVal + "&keyword=" + keywordVal;


                    // enable nearby : hw8 server.js
                    if (mInput_nearby.isChecked()) {
                        Log.v(TAG, "Rainie : url_params=" + url_params);

                        int distanceVal;
                        if (mDistance.getText().toString().isEmpty()) {
                            distanceVal = 10;
                        } else {
                            distanceVal = Integer.parseInt(mDistance.getText().toString());
                        }
                        url_params += "&distance=" + distanceVal;

                        // current location
                        if (mCurrentLocation.isChecked()) {
                            url_params += "&zipcode=" + currentZip;
//                getCurrentGeoLocation();

                        } else {    // input zipcode location
                            String inputLocationVal = mInputLocation.getText().toString();
                            url_params += "&location=" + inputLocationVal;
//                requestResultsByInputLocation(inputLocationVal);
                        }

                    } else {    // disable nearby : hw9 (new server.js)
                        Log.v(TAG, "Rainie : url_params=" + url_params);

                        // TODO : fix server.js


                    }



                    // 5 checkbox
                    if (mNew.isChecked()) {
                        url_params += "&new_condition=" + "true";
                    } else {
                        url_params += "&new_condition=" + "false";
                    }
                    if (mUsed.isChecked()) {
                        url_params += "&used=" + "true";
                    } else {
                        url_params += "&used=" + "false";
                    }
                    if (mUnspecified.isChecked()) {
                        url_params += "&unspecified=" + "true";
                    } else {
                        url_params += "&unspecified=" + "false";
                    }
                    if (mLPickup.isChecked()) {
                        url_params += "&local_pickup=" + "true";
                    } else {
                        url_params += "&local_pickup=" + "false";
                    }
                    if (mFree.isChecked()) {
                        url_params += "&free_shipping=" + "true";
                    } else {
                        url_params += "&free_shipping=" + "false";
                    }



                    Log.v(TAG, "Rainie : (Before redirect()) getActivity() : " + getActivity());
                    mIntent.putExtra("url", url_params);
                    redirect();
                }
                catch (JSONException e)
                {
                    Toast.makeText(getActivity(), "No connection! Please check your internet connection.", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        },
        new Response.ErrorListener()
        {
            @Override
            public void onErrorResponse(VolleyError error)
            {
                Toast.makeText(getActivity(), "No connection! Please check your internet connection.", Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    public void requestResultsByInputLocation(String inputLocation)
    {
        String keywordVal = mKeyword.getText().toString();
//        keywordVal = keywordVal.replaceAll(" ", "%20").toLowerCase();
        String categoryVal = mSpinner.getSelectedItem().toString();
//        categoryVal = categoryVal.replaceAll(" ", "_").toLowerCase();

        int distanceVal;
        if (mDistance.getText().toString().isEmpty())
        {
            distanceVal = 10;
        }
        else
        {
            distanceVal = Integer.parseInt(mDistance.getText().toString());
        }

        inputLocation = inputLocation.replaceAll(" ", "%20").toLowerCase();

        String mUrl = "http://cs571placesearch-env.us-east-2.elasticbeanstalk.com/?";
        mUrl += "category=" + categoryVal + "&distance=" + distanceVal +
                "&keyword=" + keywordVal + "&location=" + inputLocation;
        System.out.println(mUrl);



        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(getActivity());

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>()
        {
            @Override
            public void onResponse(String response)
            {
                try
                {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("results");
                    //System.out.println(jsonObject.toString());
                    mIntent.putExtra("jsonObj", jsonObject.toString());
                    redirect();
//                    mProgressDialog.dismiss();
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
                        Toast.makeText(getActivity(), "No connection! Please check your internet connection.", Toast.LENGTH_SHORT).show();
//                        mProgressDialog.dismiss();
                        System.out.println(error);
                    }
                });
        queue.add(stringRequest);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {

        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    get_location();
                } else {
                    Log.d(TAG, "=== NOT got granted");
                }
                return;
            }
        }
    }

    private void get_location() {

        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new MyLocationListener();


//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new TulingLocationListener());

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
        Criteria criteria = new Criteria();
        String bestProvider = locationManager.getBestProvider(criteria, false);
//        Location location = locationManager.getLastKnownLocation(bestProvider);

        Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        Log.d(TAG, "=====location===" + location);

        double lat1;
        double long1;

        if (null != location) {
            Log.d(TAG, "开始设置经纬度");

            lat1 = location.getLatitude();
            long1 = location.getLongitude();
        } else {
            // beijing office location
            lat1 = 39.993669;
            long1 = 116.470126;
        }

        Log.d(TAG, "======after get location..=== lat:" + lat1 + "===lon:" + long1);

//        showFragment(TRAVEL_ASSISTANT_CODE);
    }

    public void getCurrentGeoLocation()
    {

        Log.d(TAG, "======before get location..");

        // for android 6.0
        if (ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this.getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "== in android 6.0, getting permission");
            ActivityCompat.requestPermissions(this.getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        } else{
            Log.d(TAG, "== in android 5.0");
            get_location();
        }

        LocationManager locationManager = (LocationManager)
                getActivity().getSystemService(LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if ( ContextCompat.checkSelfPermission( this.getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION ) != PackageManager.PERMISSION_GRANTED )
        {}
        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }


    public void redirect()
    {
        this.getActivity().startActivity(mIntent);
    }

    /*---------- Listener class to get coordinates -------------*/
    public class MyLocationListener implements LocationListener
    {
        private double[] geoLocation = new double[2];
        @Override
        public void onLocationChanged(Location loc)
        {
            double lng = loc.getLongitude();
            double lat = loc.getLatitude();
            geoLocation[0] = lng;
            geoLocation[1] = lat;

            String keywordVal = mKeyword.getText().toString();
            keywordVal = keywordVal.replaceAll(" ", "%20").toLowerCase();
            String categoryVal = mSpinner.getSelectedItem().toString();
            categoryVal = categoryVal.replaceAll(" ", "_").toLowerCase();

            int distanceVal;
            if (mDistance.getText().toString().isEmpty())
            {
                distanceVal = 10;
            }
            else
            {
                distanceVal = Integer.parseInt(mDistance.getText().toString());
            }

            String mUrl = "http://cs571placesearch-env.us-east-2.elasticbeanstalk.com/?";
            mUrl += "category=" + categoryVal + "&distance=" + distanceVal +
                    "&keyword=" + keywordVal + "&latitude=" + lat + "&longitude=" + lng;
            System.out.println(mUrl);



            Log.v(TAG, "Rainie : (Before redirect()) getActivity() : " + getActivity());
            mIntent.putExtra("url", mUrl);
            redirect();



            // Instantiate the RequestQueue.
//            RequestQueue queue = Volley.newRequestQueue(getActivity());


            // Request a string response from the provided URL.
//            StringRequest stringRequest = new StringRequest(Request.Method.GET, mUrl, new Response.Listener<String>()
//            {
//                @Override
//                public void onResponse(String response)
//                {
//                    try
//                    {
//                        JSONObject jsonObject = new JSONObject(response);
//                        JSONArray jsonArray = jsonObject.getJSONArray("results");
//                        Log.v(TAG, "Rainie: JSON : " + jsonObject.toString());
//                        mIntent.putExtra("jsonObj", jsonObject.toString());
//                        redirect();
//                        mProgressDialog.dismiss();
//                        mLocationError.setVisibility(View.GONE);
//                    }
//                    catch (JSONException e)
//                    {
//                        e.printStackTrace();
//                    }
//                }
//            },
//            new Response.ErrorListener()
//            {
//                @Override
//                public void onErrorResponse(VolleyError error)
//                {
//                    Toast.makeText(getActivity(), "No connection! Please check your internet connection.", Toast.LENGTH_SHORT).show();
//                    mProgressDialog.dismiss();
//                    System.out.println(error);
//                }
//            });
//            queue.add(stringRequest);
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

}

