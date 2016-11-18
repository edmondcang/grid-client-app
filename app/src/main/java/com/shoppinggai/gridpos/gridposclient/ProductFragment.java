package com.shoppinggai.gridpos.gridposclient;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;

import classes.FakeDataManager;
import tabs.SlidingTabLayout;

/**
 * Created by admin0 on 5/1/16.
 */
public class ProductFragment extends Fragment {

    RelativeLayout productFragmentLayout;

    static Context parentContext;
    static ActionBar actionBar;
    static FakeDataManager fake;
    static JSONObject product;
    static android.app.FragmentManager fragmentManager;
    static SlidingTabLayout mTabs;
    static ViewPager mPager;

    int TAB_SIZE = 2;

    CharSequence[] tabs = new CharSequence[TAB_SIZE];

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        productFragmentLayout = (RelativeLayout) inflater.inflate(R.layout.product_fragment, container, false);

        tabs[0] = getString(R.string.general_info);
        tabs[1] = getString(R.string.sales_record);

        parentContext = getActivity();
        actionBar = ((AppCompatActivity) parentContext).getSupportActionBar();
        fragmentManager = getFragmentManager();

        Bundle extras = getArguments();

        int productId = extras.getInt("product_id");

        fake = new FakeDataManager(parentContext);

        try {

            product = fake.findProductById(productId);
            actionBar.setTitle(product.getString("name") +" #" + productId);

            mPager = (ViewPager) ((AppCompatActivity) parentContext).findViewById(R.id.pager);
            mPager.setAdapter(new PagerAdapter(((AppCompatActivity) parentContext).getSupportFragmentManager()));

            mTabs = (SlidingTabLayout) ((AppCompatActivity) parentContext).findViewById(R.id.tabs);
            mTabs.setViewPager(mPager);

            mPager.setVisibility(View.VISIBLE);
            mTabs.setVisibility(View.VISIBLE);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return productFragmentLayout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

        view.setFocusableInTouchMode(true);
        view.requestFocus();
        view.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

//                Log.d("keyEvent", String.valueOf(keyEvent));

                if (keyCode == keyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

                    fragmentManager.popBackStack();

                    return true;
                }
                return false;
            }
        });
    }

    class PagerAdapter extends FragmentPagerAdapter {

        public PagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            ProductTabFragment productTabFragment = null;
            try {
                productTabFragment = ProductTabFragment.getInstance(position);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            }
            return productTabFragment;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabs[position];
        }

        @Override
        public int getCount() {
            return TAB_SIZE;
        }
    }

    public static class ProductTabFragment extends android.support.v4.app.Fragment {

        public static ProductTabFragment getInstance(int position) throws JSONException, IOException {
            ProductTabFragment productTabFragment = new ProductTabFragment();

            Bundle args = new Bundle();

            args.putInt("position", position);

            productTabFragment.setArguments(args);

            return productTabFragment;
        }

        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            Bundle data = getArguments();
            View layout;

            NumberFormat moneyFormat = NumberFormat.getCurrencyInstance();
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setCurrency(Currency.getInstance("HKD"));
            ((DecimalFormat) moneyFormat).setDecimalFormatSymbols(symbols);

            switch (data.getInt("position")) {

                case 0:

                    layout = inflater.inflate(R.layout.product_general_info_tab_fragment, container, false);

                    TextView price = (TextView) layout.findViewById(R.id.price);
                    TextView qty = (TextView) layout.findViewById(R.id.qty);
                    TextView desc = (TextView) layout.findViewById(R.id.upper);
                    TextView belongToGrid = (TextView) layout.findViewById(R.id.belong_to_grid);

                    try {
                        JSONObject grid = fake.findGridById(product.getInt("grid_id"));

                        price.setText(moneyFormat.format(product.getInt("price")));
                        qty.setText(String.valueOf(product.getInt("qty")));
                        belongToGrid.setText(grid.getString("code"));

                        String descText = product.getString("desc");
                        if (descText.equals("null")) {
                            desc.setText('('+ getString(R.string.no_desc) + ')');
                            desc.setTextColor(Color.parseColor("#CCCCCC"));
                        }
                        else {
                            desc.setText(descText);
                        }

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    return layout;

                case 1:

                    layout = inflater.inflate(R.layout.product_sales_list_tab_fragment, container, false);

                    ListView list = (ListView) layout.findViewById(R.id.listView);
                    TextView salesTotalQtyText = (TextView) layout.findViewById(R.id.sales_total_qty);
                    TextView salesTotalPriceText = (TextView) layout.findViewById(R.id.sales_total_price);

                    ArrayList<String> salesDateList = new ArrayList<>();
                    ArrayList<String> salesQtyList = new ArrayList<>();
                    ArrayList<String> salesAmountList = new ArrayList<>();

                    try {

                        JSONArray sales = fake.findDateAndQtyByProductId(product.getInt("id"));

                        Double salesTotalPrice = 0.0;
                        int salesTotalQty = 0;

                        for (int i = 0; i < sales.length(); i++) {

                            JSONObject sale = sales.getJSONObject(i);

                            String saleDate = sale.getString("date");
                            String saleQty = getString(R.string.sold) +' '+ sale.getInt("qty") +' '+ getString(R.string.piece);

                            salesTotalQty += sale.getInt("qty");
                            Double salesTotal = sale.getInt("qty") * product.getDouble("price");
                            salesTotalPrice += salesTotal;
                            String salesAmount = moneyFormat.format(salesTotal);

                            salesDateList.add(saleDate);
                            salesQtyList.add(saleQty);
                            salesAmountList.add(salesAmount);
                        }

                        salesTotalQtyText.setText(String.valueOf(salesTotalQty) +' '+ getString(R.string.piece));
                        salesTotalPriceText.setText(moneyFormat.format(salesTotalPrice));

                        RowListAdapter adapter = new RowListAdapter(
                                parentContext,
                                null,
                                salesDateList.toArray(new String[salesDateList.size()]),
                                salesQtyList.toArray(new String[salesQtyList.size()]),
                                salesAmountList.toArray(new String[salesAmountList.size()])
                        );

                        list.setAdapter(adapter);

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                    return layout;

                default:
                    return null;
            }
        }

        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {

            super.onViewCreated(view, savedInstanceState);

//            ((AppCompatActivity) parentContext).findViewById(R.id.pager).setVisibility(View.GONE);
//            ((AppCompatActivity) parentContext).findViewById(R.id.tabs).setVisibility(View.GONE);
//            ((AppCompatActivity) parentContext).findViewById(R.id.pager2).setVisibility(View.GONE);
//            ((AppCompatActivity) parentContext).findViewById(R.id.tabs2).setVisibility(View.GONE);

            view.setFocusableInTouchMode(true);
            view.requestFocus();
            view.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {

                    Log.d("keyEvent", String.valueOf(keyEvent));

                    if (keyCode == keyEvent.KEYCODE_BACK && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {

                        fragmentManager.popBackStack();

                        return true;
                    }
                    return false;
                }
            });
        }

        @Override
        public void onDetach() {
            super.onDetach();

            mPager.setVisibility(View.GONE);
            mTabs.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

        mPager.setVisibility(View.GONE);
        mTabs.setVisibility(View.GONE);
    }
}
