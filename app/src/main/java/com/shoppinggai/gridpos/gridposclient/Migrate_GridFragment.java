
package com.shoppinggai.gridpos.gridposclient;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentTransaction;
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
import android.widget.AdapterView;
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
public class Migrate_GridFragment extends Fragment {

    RelativeLayout fragmentLayout;

    static Context parentContext;
    static ActionBar actionBar;
    static FakeDataManager fake;
    static JSONObject grid;
    static android.app.FragmentManager fragmentManager;
    static SlidingTabLayout mTabs;
    static ViewPager mPager;

    int TAB_SIZE = 2;

    CharSequence[] tabs = new CharSequence[TAB_SIZE];

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentLayout = (RelativeLayout) inflater.inflate(R.layout.grid_fragment, container, false);

        tabs[0] = getString(R.string.products);
        tabs[1] = getString(R.string.invoices);

        parentContext = getActivity();
        actionBar = ((AppCompatActivity) parentContext).getSupportActionBar();
        fragmentManager = getFragmentManager();

        Bundle extras = getArguments();

        int gridId = extras.getInt("grid_id");

        fake = new FakeDataManager(parentContext);

        try {

            grid = fake.findGridById(gridId);
            actionBar.setTitle(grid.getString("code") +" #" + gridId);

            mPager = (ViewPager) ((AppCompatActivity) parentContext).findViewById(R.id.pager2);
            mPager.setAdapter(new PagerAdapter(((AppCompatActivity) parentContext).getSupportFragmentManager()));

            mTabs = (SlidingTabLayout) ((AppCompatActivity) parentContext).findViewById(R.id.tabs2);
            mTabs.setViewPager(mPager);

            mPager.setVisibility(View.VISIBLE);
            mTabs.setVisibility(View.VISIBLE);

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return fragmentLayout;
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

                    layout = inflater.inflate(R.layout.grid_product_list_tab_fragment, container, false);

                    ListView list = (ListView) layout.findViewById(R.id.listView);

                    ArrayList<Integer> invoiceIdList = new ArrayList<>();
                    ArrayList<String> invoiceRefList = new ArrayList<>();
                    ArrayList<String> invoiceAmountList = new ArrayList<>();
                    ArrayList<String> invoiceDateList = new ArrayList<>();

                    try {

                        JSONArray products = fake.findProductsByGridId(grid.getInt("id"));
                        if (products.length() == 0) {
                            TextView msg = (TextView) layout.findViewById(R.id.msg);
                            msg.setText(getString(R.string.no_product));
                        }
                        for (int i = 0; i < products.length(); i++) {

                            JSONObject product = products.getJSONObject(i);

                            int productId = product.getInt("id");
                            String productName = product.getString("name");
                            String productPrice = moneyFormat.format(product.getDouble("price"));
                            String productQty = String.valueOf(product.getInt("qty"));

                            invoiceIdList.add(productId);
                            invoiceRefList.add(productName);
                            invoiceAmountList.add(productPrice);
                            invoiceDateList.add(productQty + ' ' + getString(R.string.piece) + getString(R.string.product_store));
                        }

                        RowListAdapter adapter = new RowListAdapter(
                                parentContext,
                                invoiceIdList.toArray(new Integer[invoiceIdList.size()]),
                                invoiceRefList.toArray(new String[invoiceRefList.size()]),
                                invoiceAmountList.toArray(new String[invoiceAmountList.size()]),
                                invoiceDateList.toArray(new String[invoiceDateList.size()])
                        );

                        list.setAdapter(adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                mPager.setVisibility(View.GONE);
                                mTabs.setVisibility(View.GONE);

                                TextView productId = (TextView) view.findViewById(R.id.row_id);

                                Bundle data = new Bundle();

                                data.putInt("product_id", Integer.parseInt(productId.getText().toString()));

                                ProductFragment frag = new ProductFragment();

                                frag.setArguments(data);

                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.fragmentContainer, frag, "productFragment");
                                transaction.addToBackStack("replaced by productFragment");
                                transaction.commit();
                            }
                        });

                    } catch (JSONException | IOException e) {
                        e.printStackTrace();
                    }

                    return layout;

                case 1:

                    layout = inflater.inflate(R.layout.grid_invoice_list_tab_fragment, container, false);

                    list = (ListView) layout.findViewById(R.id.listView);

                    invoiceIdList = new ArrayList<>();
                    invoiceRefList = new ArrayList<>();
                    invoiceAmountList = new ArrayList<>();
                    invoiceDateList = new ArrayList<>();

                    try {

                        JSONArray invoices = fake.findInvoicesByGridId(grid.getInt("id"));
                        if (invoices.length() == 0) {
                            TextView msg = (TextView) layout.findViewById(R.id.msg);
                            msg.setText(getString(R.string.no_record));
                        }
                        for (int i = 0; i < invoices.length(); i++) {

                            JSONObject invoice = invoices.getJSONObject(i);

                            int invoiceId = invoice.getInt("id");
                            String invoiceRef = invoice.getString("ref");
                            String invoiceAmount = moneyFormat.format(invoice.getDouble("amount"));
                            String invoiceDate = invoice.getString("date");

                            invoiceIdList.add(invoiceId);
                            invoiceRefList.add(invoiceRef);
                            invoiceAmountList.add(invoiceAmount);
                            invoiceDateList.add(invoiceDate);
                        }

                        RowListAdapter adapter = new RowListAdapter(
                                parentContext,
                                invoiceIdList.toArray(new Integer[invoiceIdList.size()]),
                                invoiceRefList.toArray(new String[invoiceRefList.size()]),
                                invoiceAmountList.toArray(new String[invoiceAmountList.size()]),
                                invoiceDateList.toArray(new String[invoiceDateList.size()])
                        );

                        list.setAdapter(adapter);
                        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                mPager.setVisibility(View.GONE);
                                mTabs.setVisibility(View.GONE);

                                TextView invoiceId = (TextView) view.findViewById(R.id.row_id);

                                Bundle data = new Bundle();

                                data.putInt("invoice_id", Integer.parseInt(invoiceId.getText().toString()));

                                InvoiceFragment frag = new InvoiceFragment();

                                frag.setArguments(data);

                                FragmentTransaction transaction = fragmentManager.beginTransaction();
                                transaction.replace(R.id.fragmentContainer, frag, "invoiceFragment");
                                transaction.addToBackStack("replaced by invoiceFragment");
                                transaction.commit();
                            }
                        });
                    } catch (JSONException | IOException e) {
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