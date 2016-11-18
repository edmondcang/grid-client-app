package com.shoppinggai.gridpos.gridposclient;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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

/**
 * Created by admin0 on 4/30/16.
 */
public class GridFragment extends Fragment {

    FakeDataManager fake;
    JSONObject grid;
    JSONArray products;
    Context context;
    android.app.FragmentManager fragmentManager;
    NumberFormat moneyFormat;
    ActionBar actionBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

//        setHasOptionsMenu(true);

        context = getActivity();
        fragmentManager = getFragmentManager();
        if (context != null)
            actionBar = ((AppCompatActivity) context).getSupportActionBar();

        moneyFormat = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setCurrency(Currency.getInstance("HKD"));
        ((DecimalFormat) moneyFormat).setDecimalFormatSymbols(symbols);

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.grid_fragment, container, false);

        ListView list = (ListView) layout.findViewById(R.id.listView);

        ArrayList<Integer> productIdList = new ArrayList<>();
        ArrayList<String> productNameList = new ArrayList<>();
        ArrayList<String> productPriceList = new ArrayList<>();
        ArrayList<String> productQtyList = new ArrayList<>();

        Bundle extras = getArguments();

        int gridId = extras.getInt("grid_id");

//        Log.d(String.valueOf(gridId), "grid-id");

        fake = new FakeDataManager(context);

        try {

            grid = fake.findGridById(gridId);
            actionBar.setTitle(grid.getString("code"));

            products = fake.findProductsByGridId(grid.getInt("id"));
            if (products.length() == 0) {
                TextView msg = new TextView(context);
                msg.setText(getString(R.string.no_product));
                layout.addView(msg);
            }
            for (int i = 0; i < products.length(); i++) {

                JSONObject product = products.getJSONObject(i);

                int productId = product.getInt("id");
                String productName = product.getString("name");
                String productPrice = moneyFormat.format(product.getDouble("price"));
                String productQty = String.valueOf(product.getInt("qty"));

                productIdList.add(productId);
                productNameList.add(productName);
                productPriceList.add(productPrice);
                productQtyList.add(productQty + ' ' + getString(R.string.piece) + getString(R.string.product_store));
            }

            RowListAdapter adapter = new RowListAdapter(
                    context,
                    productIdList.toArray(new Integer[productIdList.size()]),
                    productNameList.toArray(new String[productNameList.size()]),
                    productPriceList.toArray(new String[productPriceList.size()]),
                    productQtyList.toArray(new String[productQtyList.size()])
            );

            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    TextView productId = (TextView) view.findViewById(R.id.row_id);

                    Bundle data = new Bundle();
//                    data.putString("gridCode", titleTextView.getText().toString());
                    data.putInt("product_id", Integer.parseInt(productId.getText().toString()));

                    ProductFragment frag = new ProductFragment();

                    frag.setArguments(data);

                    FragmentTransaction transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.fragmentContainer, frag, "productFragment");
                    transaction.addToBackStack("replaced by productFragment");
                    transaction.commit();
                }
            });

        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        return layout;
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

//    @Override
//    public void onResume() {
//        super.onResume();
//        try {
//            actionBar.setTitle(grid.getString("code"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        actionBar.setTitle(getString(R.string.my_grids));
//    }
}
