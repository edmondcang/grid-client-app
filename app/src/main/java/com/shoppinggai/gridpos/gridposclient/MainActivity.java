package com.shoppinggai.gridpos.gridposclient;

import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import classes.FakeDataManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    android.app.FragmentManager fragmentManager;
    FakeDataManager fake;
    SharedPreferences prefs;
    String username;
    String email;
    Integer userID;

    final AnalysisFragment analysisFragment = new AnalysisFragment();
    final ProductListFragment productListFragment = new ProductListFragment();
    final GridListFragment gridListFragment = new GridListFragment();

    ActionBarDrawerToggle toggle;

//    private FragmentManager.OnBackStackChangedListener
//        mOnBackStackChangeListener = new FragmentManager.OnBackStackChangedListener() {
//        @Override
//        public void onBackStackChanged() {
//            syncActionBarArrowState();
//        }
//    };
//
//    private void syncActionBarArrowState() {
//        int backStackEntryCount =
//                getSupportFragmentManager().getBackStackEntryCount();
//        Log.d("back-stack-entry-count", String.valueOf(backStackEntryCount));
//        toggle.setDrawerIndicatorEnabled(backStackEntryCount == 0);
//    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        fake = new FakeDataManager(MainActivity.this);
        fragmentManager = getFragmentManager();
//        fragmentManager.addOnBackStackChangedListener(mOnBackStackChangeListener);
//        fragmentManager.addOnBackStackChangedListener((android.app.FragmentManager.OnBackStackChangedListener) MainActivity.this);

        // Restore preferences
        prefs = getSharedPreferences(Constants.SHARED_PREFS_NAME, MODE_PRIVATE);

        username = prefs.getString("user_name", null);
        email = prefs.getString("user_email", null);
        userID = prefs.getInt("user_id", 0);
//        Log.d(username, "username");

        setContentView(R.layout.activity_main);

//        ListView listView = (ListView) findViewById(R.id.listView);
//
//        ArrayList<Integer> gridIdList = new ArrayList<>();
//        ArrayList<String> gridCodeList = new ArrayList<>();
//        ArrayList<String> numProductList = new ArrayList<>();

        if (savedInstanceState == null) {
            replaceFragment(gridListFragment, "gridListFragment");
        }
        else {
            //
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.my_grids));
//            toolbar.setTitle(getString(R.string.app_name));
        }

        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });
        }

//        getSupportActionBar().setDisplayShowHomeEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {

            @Override
            public void onDrawerClosed(View drawerView) {
//                Log.d("EVENT", "drawer-closed");
//                syncActionBarArrowState();
//                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
//                toggle.setDrawerIndicatorEnabled(true);
//                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                Log.d("drawer-state-changed", String.valueOf(newState));
                super.onDrawerStateChanged(newState);
            }

            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
            }
        };

        if (drawer != null) {
            drawer.setDrawerListener(toggle);
        }

//        getSupportFragmentManager().addOnBackStackChangedListener(mOnBackStackChangeListener);

        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        // List out all grids of the user
//        try {
//
//            JSONArray grids = fake.findGridsByUserId(userID);
//
//            Log.d("user-id", String.valueOf(userID));
//            Log.d("grid-length", String.valueOf(grids.length()));
//
//            for (int i = 0; i < grids.length(); i++) {
//
//                JSONObject grid = grids.getJSONObject(i);
//
//                int gridId = grid.getInt("id");
//                String gridCode = grid.getString("code");
//                String numProducts = grid.getString("num_products");
//
//                gridIdList.add(gridId);
//                gridCodeList.add(gridCode);
//                numProductList.add(numProducts +' '+ getString(R.string.num_products));
//            }
//
//            RowListAdapter adapter = new RowListAdapter(
//                    MainActivity.this,
//                    gridIdList.toArray(new Integer[gridIdList.size()]),
//                    gridCodeList.toArray(new String[gridCodeList.size()]),
//                    numProductList.toArray(new String[numProductList.size()]),
//                    null
//            );
//
//            listView.setAdapter(adapter);
//            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//
//                @Override
//                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//                    TextView gridId = (TextView) view.findViewById(R.id.row_id);
//
//                    Bundle data = new Bundle();
////                    data.putString("gridCode", titleTextView.getText().toString());
//                    data.putInt("grid_id", Integer.parseInt(gridId.getText().toString()));
//
//                    GridFragment frag = new GridFragment();
//
//                    frag.setArguments(data);
//
//                    android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
//                    transaction.replace(R.id.fragmentContainer, frag, "gridFragment");
//                    transaction.addToBackStack("replaced by gridFragment");
//                    transaction.commit();
//                }
//            });
//
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
    }

//    @Override
//    protected void onDestroy() {
//        getSupportFragmentManager().removeOnBackStackChangedListener((FragmentManager.OnBackStackChangedListener) mOnBackStackChangeListener);
//        super.onDestroy();
//    }

    private void closeAllActivities() {
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("EXIT", true);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.confirm_exit))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MainActivity.this.closeAllActivities();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
//        super.onBackPressed();
//        MainActivity.this.closeAllActivities();
//        final GridFragment frag = (GridFragment) getSupportFragmentManager().findFragmentByTag("GridFragment");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);

        TextView navHeaderHomeTitle = (TextView) findViewById(R.id.nav_header_home_title);
        if (navHeaderHomeTitle != null) {
            navHeaderHomeTitle.setText(username);
        }

        TextView navHeaderHomeEmail = (TextView) findViewById(R.id.nav_header_home_email);
        if (navHeaderHomeEmail != null) {
            navHeaderHomeEmail.setText(email);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if (id == R.id.action_logout) {
            // Clear shared preferences and exit app
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.confirm_logout))
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getSharedPreferences(Constants.SHARED_PREFS_NAME, 0).edit().clear().commit();
                            MainActivity.this.closeAllActivities();
//                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
//                            startActivity(intent);
//                            finish();
                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();
        }

//        if (toggle.isDrawerIndicatorEnabled() &&
//                toggle.onOptionsItemSelected(item)) {
//            return true;
//        } else if (item.getItemId() == android.R.id.home &&
//                getSupportFragmentManager().popBackStackImmediate()) {
//            return true;
//        } else {
//            return super.onOptionsItemSelected(item);
//        }

        return super.onOptionsItemSelected(item);
    }

    private void replaceFragment(Fragment frag, String tag) {
//        Log.d("frag tag", tag);
        android.app.FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragmentContainer, frag, tag);
        transaction.addToBackStack("replaced by" +' '+ tag);
        transaction.commit();

        int count = fragmentManager.getBackStackEntryCount();
        if (count > 0) {
            android.app.FragmentManager.BackStackEntry entry = fragmentManager.getBackStackEntryAt(count - 1);
            Log.d("back-stack-change", entry.getName());
        }

        findViewById(R.id.pager).setVisibility(View.GONE);
        findViewById(R.id.tabs).setVisibility(View.GONE);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Log.d("item-id", String.valueOf(item.getItemId()));

//        if (toggle.isDrawerIndicatorEnabled() &&
//            toggle.onOptionsItemSelected(item)) {
//            return true;
//        } else if (id == R.id.nav_overview) {

        if (id == R.id.nav_overview) {

            replaceFragment(analysisFragment, "analysisFragment");

        } else if (id == R.id.nav_product_list) {

            replaceFragment(productListFragment, "productListFragment");

        } else if (id == R.id.nav_my_grids) {
            for (int i = 1; i < fragmentManager.getBackStackEntryCount(); i++) {
                fragmentManager.popBackStack();
            }
//            fragmentManager.popBackStack(null, android.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
//            replaceFragment(gridListFragment, "gridListFragment");
//            getSupportFragmentManager().popBackStackImmediate();
//            replaceFragment(gridListFragment, "gridListFragment");

//        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.action_logout) {

        } else {
//            return super.onOptionsItemSelected(item);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
