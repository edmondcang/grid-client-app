package com.shoppinggai.gridpos.gridposclient;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import java.util.ArrayList;

import classes.FakeDataManager;

/**
 * Created by admin0 on 4/27/16.
 */
public class GridListFragment extends Fragment {

    FakeDataManager fake;
    SharedPreferences prefs;
    Context context;
    ActionBar actionBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        context = getActivity();
        actionBar = ((AppCompatActivity) context).getSupportActionBar();

        fake = new FakeDataManager(context);
        prefs = context.getSharedPreferences(Constants.SHARED_PREFS_NAME, Context.MODE_PRIVATE);

        actionBar.setTitle(getString(R.string.my_grids));

        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.grid_list_fragment, container, false);

//        ArrayList<TextView> items = new ArrayList<>();
        ListView list = (ListView) layout.findViewById(R.id.listView);

        ArrayList<Integer> gridIdList = new ArrayList<>();
        ArrayList<String> gridCodeList = new ArrayList<>();
        ArrayList<String> numProductList = new ArrayList<>();

        try {

            JSONArray grids = fake.findGridsByUserId(prefs.getInt("user_id", 0));

            for (int i = 0; i < grids.length(); i++) {

                JSONObject grid = grids.getJSONObject(i);

                int gridId = grid.getInt("id");
                String gridCode = grid.getString("code");
                String numProducts = grid.getString("num_products");

                gridIdList.add(gridId);
                gridCodeList.add(gridCode);
                numProductList.add(numProducts +' '+ getString(R.string.num_products));
            }

            RowListAdapter adapter = new RowListAdapter(
                    context,
                    gridIdList.toArray(new Integer[gridIdList.size()]),
                    gridCodeList.toArray(new String[gridCodeList.size()]),
                    numProductList.toArray(new String[numProductList.size()]),
                    null
            );

            list.setAdapter(adapter);
            list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    TextView gridId = (TextView) view.findViewById(R.id.row_id);

                    Bundle data = new Bundle();
//                    data.putString("gridCode", titleTextView.getText().toString());
                    data.putInt("grid_id", Integer.parseInt(gridId.getText().toString()));

                    Migrate_GridFragment frag = new Migrate_GridFragment();

                    frag.setArguments(data);

                    android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragmentContainer, frag, "gridFragment");
                    transaction.addToBackStack("replaced by gridFragment");
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

//                    getFragmentManager().popBackStack();

//                    return true;
                }
                return false;
            }
        });
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        actionBar.setTitle(getString(R.string.my_grids));
//    }
}
