package com.example.jmaeng.found_it;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class SearchableActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView noResults;
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchable);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        noResults = (TextView)findViewById(R.id.no_search_results);
        mListView = (ListView)findViewById(R.id.listView);

        // Get results from DB
        MainDB db = MainDB.getInstance(getApplicationContext());
        String query = "";
        Intent intent = getIntent();

        // Preform search for query in item names
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            getSupportActionBar().setTitle("Search Results For \"" + query + "\"");
            (new DownloadFromDB(db, query)).execute();
        } else {
            getSupportActionBar().setTitle("Search Results For \"" + query + "\"");
            displayResults(new ArrayList<String>());
        }


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /*
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);

        android.support.v7.widget.SearchView searchView = (
                android.support.v7.widget.SearchView) MenuItemCompat.getActionView(searchItem);

        SearchManager searchManager = (SearchManager)getSystemService(Context.SEARCH_SERVICE);
        ComponentName componentName = new ComponentName(this, SearchableActivity.class);

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName));

        return super.onCreateOptionsMenu(menu);
        */
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.searchable, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_home) {
            intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_all_rooms) {
            intent = new Intent(this, AllRoomsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_all_items) {
            intent = new Intent(this, AllItemsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class DownloadFromDB extends AsyncTask<MainDB, Void, ArrayList<String>> {

        private MainDB db;
        private String query;

        public DownloadFromDB(MainDB db, String query) {
            this.db = db;
            this.query = query;
        }

        @Override
        protected  ArrayList<String> doInBackground(MainDB... params) {
            return db.getAllItemLikeNames(query);
        }

        protected void onPostExecute(final ArrayList<String> results) {
            Log.d(SearchableActivity.class.getSimpleName(),
                    "[SearchableActivity]: Search for \'" + query + "\' " + results.toString());
            displayResults(results);
        }
    }

    private void displayResults(ArrayList<String> itemsFound) {
        if(itemsFound.isEmpty()) {
            noResults.setText("Nothing found.");
            return;
        }

        final ArrayList<String> results = itemsFound;
        ArrayAdapter<String> itemAdapter = new ArrayAdapter<>(this, R.layout.search_list, results);
        mListView.setAdapter(itemAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int itemPos = position;
                String itemName = (String)mListView.getItemAtPosition(position);
                Intent intent = new Intent(getApplicationContext(), MainItemActivity.class);
                intent.putExtra("itemName", results.get(position));
                startActivity(intent);
            }
        });
    }


}
