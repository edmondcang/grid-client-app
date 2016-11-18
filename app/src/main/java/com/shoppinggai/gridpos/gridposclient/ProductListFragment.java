package com.shoppinggai.gridpos.gridposclient;

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
import android.widget.RelativeLayout;

import classes.FakeDataManager;

/**
 * Created by admin0 on 4/27/16.
 */
public class ProductListFragment extends Fragment {

    android.app.FragmentManager fragmentManager;
    FakeDataManager fake;
    SharedPreferences prefs;
    Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();
        fragmentManager = getFragmentManager();

        fake = new FakeDataManager(context);
        prefs = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        ((AppCompatActivity) context).getSupportActionBar().setTitle(getString(R.string.product_list));

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.product_list_fragment, container, false);

        return layout;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);

//        ((AppCompatActivity) context).findViewById(R.id.pager).setVisibility(View.GONE);
//        ((AppCompatActivity) context).findViewById(R.id.tabs).setVisibility(View.GONE);

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

    @Override
    public void onDetach() {
        super.onDetach();
//        ((AppCompatActivity) context).getSupportActionBar().setTitle(getString(R.string.my_grids));
    }
}
