package com.shoppinggai.gridpos.gridposclient;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
 * Created by admin0 on 4/27/16.
 */
public class InvoiceFragment extends Fragment {

    android.app.FragmentManager fragmentManager;
    FakeDataManager fake;
    SharedPreferences prefs;
    Context context;
    JSONObject invoice;
    Bundle data;
    NumberFormat moneyFormat;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.invoice_fragment, container, false);

        context = getActivity();
        fragmentManager = getFragmentManager();

        data = getArguments();

        fake = new FakeDataManager(context);

        moneyFormat = NumberFormat.getCurrencyInstance();
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setCurrency(Currency.getInstance(Constants.CURRENCY));
        ((DecimalFormat) moneyFormat).setDecimalFormatSymbols(symbols);

        try {

            invoice = fake.getInvoiceDetails(data.getInt("invoice_id"));

            ((AppCompatActivity) context).getSupportActionBar().setTitle(invoice.getString("invoice_ref"));

            TextView date = (TextView) layout.findViewById(R.id.date);
            date.setText(invoice.getString("invoice_date"));

            TextView time = (TextView) layout.findViewById(R.id.time);
            time.setText(invoice.getString("invoice_time"));

            TextView totalAmount = (TextView) layout.findViewById(R.id.total_amount);
            totalAmount.setText(moneyFormat.format(invoice.getDouble("total_amount")));

            ListView list = (ListView) layout.findViewById(R.id.listView);

            JSONAdapter jsonAdapter = new JSONAdapter((Activity) context, invoice.getJSONArray("sales_records"));

            list.setAdapter(jsonAdapter);

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
    }
}
