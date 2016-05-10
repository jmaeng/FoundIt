package com.example.jmaeng.found_it;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SearchView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int CAROUSEL_LIMIT = 8;
    private NavigationView navigationView;
    private MainDB mainDatabase;
    private DownloadFromDB popCarouselTask, recentCarouselTask, lastViewCarouselTask;
    //private ArrayList<Item> itemArray;

    private RecyclerView popCarouselRecView, recentCarouselRecView, lastCarouselRecView;
    private LinearLayoutManager popLM, recentLM, lastLM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callAddItemActivity();
            }
        });

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);

        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //this intent is used if called from another activity
        Intent intent = getIntent(); //TODO Do I do anything with this though? Is this necessary?

        mainDatabase = MainDB.getInstance(getApplicationContext());

        popCarouselRecView = (RecyclerView)findViewById(R.id.pop_recycler_carousel_view);
        recentCarouselRecView = (RecyclerView)findViewById(R.id.added_recycler_carousel_view);
        lastCarouselRecView = (RecyclerView)findViewById(R.id.viewed_recycler_carousel_view);

        popLM = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        recentLM = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);
        lastLM = new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false);

        popCarouselRecView.setHasFixedSize(true);
        recentCarouselRecView.setHasFixedSize(true);
        lastCarouselRecView.setHasFixedSize(true);

        popCarouselRecView.setLayoutManager(popLM);
        recentCarouselRecView.setLayoutManager(recentLM);
        lastCarouselRecView.setLayoutManager(lastLM);

        popCarouselTask = new DownloadFromDB(R.id.pop_recycler_carousel_view);
        recentCarouselTask =  new DownloadFromDB(R.id.added_recycler_carousel_view);
        lastViewCarouselTask = new DownloadFromDB(R.id.viewed_recycler_carousel_view);

        popCarouselTask.execute(mainDatabase);
        recentCarouselTask.execute(mainDatabase);
        lastViewCarouselTask.execute(mainDatabase);

    }

    private class DownloadFromDB extends AsyncTask<MainDB, Void, ArrayList<Item>> {

        private int recyclerViewID;

        public DownloadFromDB(int recyclerViewID) {
            this.recyclerViewID = recyclerViewID;
        }

        @Override
        protected  ArrayList<Item> doInBackground(MainDB... params) {
            MainDB db = params[0];
            ArrayList<Item> itArray;
            if (recyclerViewID == R.id.pop_recycler_carousel_view) {
                itArray = db.getMostPopularItemImages();
            } else if (recyclerViewID == R.id.added_recycler_carousel_view){
                itArray = db.getAllItemImages(); //TODO change this later once correct method is created
            } else {
                itArray = db.getAllItemImages(); //TODO change this later once correct method is created
            }

            Bitmap b;
            BitmapFactory.Options options = new BitmapFactory.Options();
            int width = 100, height = 100;

            for (int i = 0; i < itArray.size() && i < CAROUSEL_LIMIT; i++) {
                Item item = itArray.get(i);
                byte[] image = item.get_ITEM_IMG();
                options.inJustDecodeBounds = true;
                b = BitmapFactory.decodeByteArray(image, 0, image.length, options);

                options.inSampleSize = calculateInSampleSize(options, width, height);
                options.inJustDecodeBounds = false;

                b = BitmapFactory.decodeByteArray(image, 0, image.length, options);
                item.setBitmap(b);
            }

            return itArray;
        }

        protected void onPostExecute(final ArrayList<Item> itArray) {
            if (recyclerViewID == R.id.pop_recycler_carousel_view) {
                popCarouselRecView.setAdapter(new CarouselViewAdaptor(itArray));
            } else if (recyclerViewID == R.id.viewed_recycler_carousel_view){
                recentCarouselRecView.setAdapter(new CarouselViewAdaptor(itArray));
            } else {
                lastCarouselRecView.setAdapter(new CarouselViewAdaptor(itArray));
            }
        }
    }

    public class CarouselViewAdaptor extends RecyclerView.Adapter<CarouselViewAdaptor.ViewHolder> {

        private ArrayList<Item> itemArray;

        public CarouselViewAdaptor(ArrayList<Item> itArray){
            itemArray = itArray;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_image_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Item item = itemArray.get(position);
            holder.getImageView().setImageBitmap(item.getBitmap());

            //onclick listener goes here too.
        }

        @Override
        public int getItemCount() {
           if (itemArray != null)
                return itemArray.size();
            return 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

           private final ImageView itemImage;

            public ViewHolder(View itemView) {
                super(itemView);
                itemImage = (ImageView)itemView.findViewById(R.id.carousel_image);
            }

            public ImageView getImageView() { return itemImage; }

        }
    }

    public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    /*
    Helps save the state of the UI. Do not use to store persistent data (data that is saved to database),
     use onPause() for that instead for that.
     Overridding in case we want to save additional information than the default
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

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

    /*
    Configure the action items in menu here. (Like the search action)
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Configure the search info and add any event listeners here
        //TODO

        return super.onCreateOptionsMenu(menu);
    }

    /*
    This method is called when one of the action items in your app bar/ toolbar/ action bar
    is selected
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        super.onOptionsItemSelected(item);
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true; //TODO
        } else if (id == R.id.action_search) {
            return true;
            //TODO
        }

        return true;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent;

        if (id == R.id.nav_home) {
            //TODO do nothing? Or go to home again?

        } else if (id == R.id.nav_all_rooms) {
            intent = new Intent(this, AllRoomsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_all_items) {
                //TODO
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /*
    Cancel all AsyncTasks if activity is stopped
     */
    @Override
    protected void onStop(){
        super.onStop();
        popCarouselTask.cancel(false);
        recentCarouselTask.cancel(false);
        lastViewCarouselTask.cancel(false);

    }

    @Override
    protected void onRestart(){
        super.onRestart();
        //TODO have to requery the database to change all the carousels

    }

    @Override
    protected void onResume(){
        super.onResume();
        //TODO have to requery the database to change all the carousels.

    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainDatabase.closeDB();
    }

    private void callAddItemActivity() {
        Intent intent = new Intent(this, AddItemActivity.class);
        startActivity(intent);
    }
}
