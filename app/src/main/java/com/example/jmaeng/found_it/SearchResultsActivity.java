package com.example.jmaeng.found_it;

import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.ArrayList;

public class SearchResultsActivity extends AppCompatActivity {

    private ArrayList<String> items;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_results);
        System.out.println("onCreate");
        // Get list of items from database
        MainDB database = MainDB.getInstance(getApplicationContext());
        items = new ArrayList<>();
        (new DownloadFromDB(items)).execute(database);

        handleIntent(getIntent(), items);
    }

    protected void onNewIntent(Intent intent) {
        items = new ArrayList<>();
        handleIntent(intent, items);
    }
    private void handleIntent(Intent intent, ArrayList<String> items) {
        if(Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            // TODO use the query to search for data somehow
            System.out.println(query);
        }
    }


    private class DownloadFromDB extends AsyncTask<MainDB, Void, ArrayList<String>> {

        private ArrayList<String> items;

        public DownloadFromDB(ArrayList<String> items) {
            this.items = items;
        }

        @Override
        protected  ArrayList<String> doInBackground(MainDB... params) {
            MainDB db = params[0];
            items = db.getAllItemNames();
            return items;
        }

        protected void onPostExecute(final ArrayList<String> items) {
        }
    }
}
