package com.example.productsearch;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wssholmes.stark.circular_score.CircularScoreView;

import org.json.JSONException;
import org.json.JSONObject;

public class shippingFragment extends Fragment {

    private static final String TAG = "shippingFragment";

    public String jsonObjectItem_str;   // from 50 api , and only 1 item
    public JSONObject jsonObjectItem;   // from 50 api , and only 1 item

    public String jsonObject_detail_str;
    public JSONObject jsonObject_detail_item;

    public LinearLayout sold;
    public ImageView sold_img;
    public TextView sold_txt;

    public LinearLayout store;
    public TextView store_val;
    public LinearLayout score;
    public TextView score_val;
    public LinearLayout pop;
    public CircularScoreView pop_circle;
    public TextView pop_val;
    public LinearLayout star;
    public ImageView star_img;
    public TextView star_val;

    public View divider_ship;

    public LinearLayout ship;
    public ImageView ship_img;
    public TextView ship_txt;

    public LinearLayout cost;
    public TextView cost_val;
    public LinearLayout global;
    public TextView global_val;
    public LinearLayout handing;
    public TextView handing_val;
    public LinearLayout condition;
    public TextView condition_val;

    public View divider_return;

    public LinearLayout return_policy;
    public ImageView return_img;
    public TextView return_txt;

    public LinearLayout policy;
    public TextView policy_val;
    public LinearLayout within;
    public TextView within_val;
    public LinearLayout mode;
    public TextView mode_val;
    public LinearLayout by;
    public TextView by_val;

    public LayoutInflater minflater;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        minflater = inflater;

        View view = inflater.inflate(R.layout.fragment_shipping, container, false);
        Log.v(TAG, "Rainie : onCreateView()");


        sold = (LinearLayout)view.findViewById(R.id.sold);
        sold_img = (ImageView)view.findViewById(R.id.sold_img);
        sold_txt = (TextView)view.findViewById(R.id.sold_txt);

        store = (LinearLayout)view.findViewById(R.id.store);
        store_val = (TextView)view.findViewById(R.id.store_val);
        score = (LinearLayout)view.findViewById(R.id.score);
        star_img = (ImageView)view.findViewById(R.id.star_img);   // shooting stars
        score_val = (TextView)view.findViewById(R.id.score_val);
        pop = (LinearLayout)view.findViewById(R.id.pop);
        pop_circle = (CircularScoreView) view.findViewById(R.id.pop_circle);
        pop_val = (TextView)view.findViewById(R.id.pop_val);
        star = (LinearLayout)view.findViewById(R.id.star);
        star_val = (TextView)view.findViewById(R.id.star_val);

        divider_ship = (View)view.findViewById(R.id.divider_ship);

        ship = (LinearLayout)view.findViewById(R.id.ship);
        ship_img = (ImageView)view.findViewById(R.id.ship_img);
        ship_txt = (TextView)view.findViewById(R.id.ship_txt);

        cost = (LinearLayout)view.findViewById(R.id.cost);
        cost_val = (TextView)view.findViewById(R.id.cost_val);
        global = (LinearLayout)view.findViewById(R.id.global);
        global_val = (TextView)view.findViewById(R.id.global_val);
        handing = (LinearLayout)view.findViewById(R.id.handing);
        handing_val = (TextView)view.findViewById(R.id.handing_val);
        condition = (LinearLayout)view.findViewById(R.id.condition);
        condition_val = (TextView)view.findViewById(R.id.condition_val);

        divider_return = (View)view.findViewById(R.id.divider_return);

        return_policy = (LinearLayout)view.findViewById(R.id.return_policy);
        return_img = (ImageView)view.findViewById(R.id.return_img);
        return_txt = (TextView)view.findViewById(R.id.return_txt);

        policy = (LinearLayout)view.findViewById(R.id.policy);
        policy_val = (TextView)view.findViewById(R.id.policy_val);
        within = (LinearLayout)view.findViewById(R.id.within);
        within_val = (TextView)view.findViewById(R.id.within_val);
        mode = (LinearLayout)view.findViewById(R.id.mode);
        mode_val = (TextView)view.findViewById(R.id.mode_val);
        by = (LinearLayout)view.findViewById(R.id.by);
        by_val = (TextView)view.findViewById(R.id.by_val);


        Bundle bundle;
        bundle = this.getArguments();
        jsonObject_detail_str = bundle.getString("jsonObject_detail");
        jsonObjectItem_str = bundle.getString("jsonObjectItem");    // 1 item from 50 api

//        Log.v(TAG, "Rainie: jsonObject_detail_str = " + jsonObject_detail_str);
//        Log.v(TAG, "Rainie: jsonObjectItem_str = " + jsonObjectItem_str);


        try
        {
            JSONObject jsonObject_detail = new JSONObject(jsonObject_detail_str);
            jsonObject_detail_item = jsonObject_detail.getJSONObject("Item");

            jsonObjectItem = new JSONObject(jsonObjectItem_str);    // 1 item from 50 api

            generateShipping();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return view;
    }


    public void generateShipping() throws JSONException {

        Log.v(TAG, "Rainie : generateShipping()");

        sold_img.setImageResource(R.drawable.truck);
        ship_img.setImageResource(R.drawable.ferry);
        return_img.setImageResource(R.drawable.dump_truck);


        if (jsonObject_detail_item.has("Storefront")) {
            if (jsonObject_detail_item.getJSONObject("Storefront").has("StoreName") && jsonObject_detail_item.getJSONObject("Storefront").has("StoreURL")) {
                store.setVisibility(View.VISIBLE);

                String store_name = jsonObject_detail_item.getJSONObject("Storefront").getString("StoreName");
                String store_url = jsonObject_detail_item.getJSONObject("Storefront").getString("StoreURL");

//                String text = "<a href=\"http://hello.com\">hello</a>";
                String text = "<a href=\"" + store_url +  "\">" + store_name + "</a>";
                Log.v(TAG, "Rainie : store url : " + text);
                store_val.setText(Html.fromHtml(text));
//                store_val.setText(store_name);
                store_val.setMovementMethod(LinkMovementMethod.getInstance());
            }
        }

        if (jsonObject_detail_item.has("Seller")) {
            if (jsonObject_detail_item.getJSONObject("Seller").has("FeedbackScore")) {
                score.setVisibility(View.VISIBLE);
//                double score_double = jsonObject_detail_item.getJSONObject("Seller").getDouble("FeedbackScore");
//                String score_str = Double.toString(score_double);
//                score_val.setText(score_str);
                String score_str = jsonObject_detail_item.getJSONObject("Seller").getString("FeedbackScore");
                score_val.setText(score_str);

                // shooting star or border star
                int score_int = Integer.parseInt(score_str);
                if (score_int >= 0 && score_int < 10000) {
                    star_img.setImageResource(R.drawable.star_circle_outline);

                    if (score_int >= 0 && score_int <= 9) {
//                        star.setVisibility(View.GONE);
                    } else if (score_int >= 10 && score_int < 50) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_yellow));
                    } else if (score_int >= 50 && score_int < 100) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_blue));
                    } else if (score_int >= 100 && score_int < 500) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_turquoise));
                    } else if (score_int >= 500 && score_int < 1000) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_purple));
                    } else if (score_int >= 1000 && score_int < 5000) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_red));
                    } else if (score_int >= 5000 && score_int < 10000) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_green));
                    }
                } else { // score_int >= 10000
                    star_img.setImageResource(R.drawable.star_circle);

                    if (score_int >= 10000 && score_int < 25000) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_yellow));
                    } else if (score_int >= 25000 && score_int < 50000) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_turquoise));
                    } else if (score_int >= 50000 && score_int < 100000) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_purple));
                    } else if (score_int >= 100000 && score_int < 500000) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_red));
                    } else if (score_int >= 500000 && score_int < 1000000) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_green));
                    } else if (score_int >= 1000000) {
                        star_img.setColorFilter(getResources().getColor(R.color.color_silver));
                    }
                }



            }
            if (jsonObject_detail_item.getJSONObject("Seller").has("PositiveFeedbackPercent")) {
                pop.setVisibility(View.VISIBLE);
                double pop_double = jsonObject_detail_item.getJSONObject("Seller").getDouble("PositiveFeedbackPercent");

                int pop_int = (int) Math.round(pop_double);
                pop_circle.setScore(pop_int);

                String pop_str = Double.toString(pop_double);
                pop_val.setText(pop_str);
            }
            if (jsonObject_detail_item.getJSONObject("Seller").has("FeedbackRatingStar")) {
                star.setVisibility(View.VISIBLE);
                String star_str = jsonObject_detail_item.getJSONObject("Seller").getString("FeedbackRatingStar");
                star_val.setText(star_str);
            }
        }



        int count_shipping = 0;

        // free shipping (could be N/A)
        if (!jsonObjectItem.isNull("shippingInfo")) {
            if (!jsonObjectItem.getJSONArray("shippingInfo").getJSONObject(0).isNull("shippingServiceCost")) {
                if (!jsonObjectItem.getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost").getJSONObject(0).isNull("__value__")) {
                    count_shipping++;
                    cost.setVisibility(View.VISIBLE);
                    String shippingCost = jsonObjectItem.getJSONArray("shippingInfo").getJSONObject(0).getJSONArray("shippingServiceCost").getJSONObject(0).getString("__value__");
                    double shippingCost_double = Double.parseDouble(shippingCost);
                    if (shippingCost_double == 0.0) {
                        shippingCost = "Free Shipping";
                    } else {
                        shippingCost = "$" + shippingCost;
                    }
                    cost_val.setText(shippingCost);
                    Log.v(TAG, "Rainie : shippingCost = " + shippingCost);
                }
            }
        }

        if (jsonObject_detail_item.has("GlobalShipping")) {
            count_shipping++;
            global.setVisibility(View.VISIBLE);
            String global_str = jsonObject_detail_item.getString("GlobalShipping");
            if (global_str.equals("true")) {
                global_str = "Yes";
            } else {
                global_str = "No";
            }
            global_val.setText(global_str);
        }

        if (jsonObject_detail_item.has("HandlingTime")) {
            count_shipping++;
            handing.setVisibility(View.VISIBLE);
            String handing_str = jsonObject_detail_item.getString("HandlingTime");
            int handing_int = Integer.parseInt(handing_str);
            if (handing_int == 0 || handing_int == 1) {
                handing_str = handing_str + " Day";
            } else {
                handing_str = handing_str + " Days";
            }
            handing_val.setText(handing_str);
        }

        if (jsonObject_detail_item.has("ConditionDisplayName")) {
            count_shipping++;
            condition.setVisibility(View.VISIBLE);
            String condition_str = jsonObject_detail_item.getString("ConditionDisplayName");
            condition_val.setText(condition_str);
        }

        if (count_shipping > 0) {
            divider_ship.setVisibility(View.VISIBLE);
            ship.setVisibility(View.VISIBLE);
        }


        if (jsonObject_detail_item.has("ReturnPolicy")) {
            divider_return.setVisibility(View.VISIBLE);
            return_policy.setVisibility(View.VISIBLE);

            if (jsonObject_detail_item.getJSONObject("ReturnPolicy").has("ReturnsAccepted")) {
                policy.setVisibility(View.VISIBLE);
                String policy_str = jsonObject_detail_item.getJSONObject("ReturnPolicy").getString("ReturnsAccepted");
                policy_val.setText(policy_str);
            }

            if (jsonObject_detail_item.getJSONObject("ReturnPolicy").has("ReturnsWithin")) {
                within.setVisibility(View.VISIBLE);
                String within_str = jsonObject_detail_item.getJSONObject("ReturnPolicy").getString("ReturnsWithin");
                within_val.setText(within_str);
            }

            if (jsonObject_detail_item.getJSONObject("ReturnPolicy").has("Refund")) {
                mode.setVisibility(View.VISIBLE);
                String mode_str = jsonObject_detail_item.getJSONObject("ReturnPolicy").getString("Refund");
                mode_val.setText(mode_str);
            }

            if (jsonObject_detail_item.getJSONObject("ReturnPolicy").has("ShippingCostPaidBy")) {
                by.setVisibility(View.VISIBLE);
                String by_str = jsonObject_detail_item.getJSONObject("ReturnPolicy").getString("ShippingCostPaidBy");
                by_val.setText(by_str);
            }
        }



    }


}
