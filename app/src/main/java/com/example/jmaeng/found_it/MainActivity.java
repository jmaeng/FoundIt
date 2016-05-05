package com.example.jmaeng.found_it;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //TODO java.lang.OutOfMemoryError: Failed to allocate a 921612 byte allocation with 761904 free bytes and 744KB until OOM
    // TODO there is an out of memory error when trying to get to this activity multiple times, NEED TO FIX

    private NavigationView navigationView;
    private MainDB mainDatabase;

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
                /*
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        */
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

        //set image carousel
        //FOR TESTING
        (new DownloadFromDB()).execute(mainDatabase);

    }

    private class DownloadFromDB extends AsyncTask<MainDB, Void, ArrayList<Room>> {

        @Override
        protected  ArrayList<Room> doInBackground(MainDB... params) {
            MainDB db = params[0];
            //Query for all the images and put them in the images array I already created.
            return db.getAllRoomImages();
        }

        protected void onPostExecute(final ArrayList<Room> roomArray) {
            byte[] image = null;

            for (Room room: roomArray) {
                image = room.getImage();
                addImagesToImageCarousel(image, R.id.popular_image_carousel);
                addImagesToImageCarousel(image, R.id.recently_added_image_carousel);
                addImagesToImageCarousel(image, R.id.last_viewed_image_carousel);
                addImagesToImageCarousel(image, R.id.all_image_carousel);
            }
        }
    }

    /*
    Adds images to the image carousel
     */
   private void addImagesToImageCarousel(byte[] image, int layoutID) {
       LinearLayout imageCarousel = (LinearLayout)findViewById(layoutID);
       ImageView myImage = new ImageView(this);
       Bitmap b = BitmapFactory.decodeByteArray(image, 0, image.length);
       //rescale image
       float aspectRatio = b.getWidth()/b.getHeight();
       int width = 480;
       int height = Math.round((width/aspectRatio));
       //set the image
       myImage.setImageBitmap(Bitmap.createScaledBitmap(b, width, height, false));
       myImage.setLayoutParams(new AbsListView.LayoutParams(
               AbsListView.LayoutParams.MATCH_PARENT,
               AbsListView.LayoutParams.WRAP_CONTENT));
       imageCarousel.addView(myImage);
    }

    /*
    Helps save the state of the UI. Do not use to store persistent data (data that is saved to database),
     use onPause() for that instead for that.
     Overriding in case we want to save additional information than the default
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

    @SuppressWarnings("StatementWithEmptyBody")
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mainDatabase.closeDB();
    }


    /**
     *
     */
    public void callAddItemActivity() {
        startActivity(new Intent(this, AddItemActivity.class));
    }
}
