package com.example.jmaeng.found_it;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainItemActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MainDB database;
    private Item itemObject;
    private String itemName;
    private ImageView itemImage;
    private TextView itemDesc;
    private TextView itemRoom;
    private static final String TAG = MainItemActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_item);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Connect with layout
        itemImage = (ImageView)findViewById(R.id.main_item_image);
        itemDesc = (TextView)findViewById(R.id.main_item_desc);
        itemRoom = (TextView)findViewById(R.id.main_item_room);

        // Click on room to display pin location
       /* itemRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                *//*
                Intent callIntent = new Intent(this, _some_class_name_);
                callIntent.putExtra("pinLocation", item);
                startActivity(callIntent);

                //TODO retrieve item using the following in the class that is called, then use Item's get X, Y to set pin
                Intent intent = getIntent();
                Bundle bundle = intent.getExtras();
                Item item = null;

                if (bundle.containsKey("pinLocation")){
                    item = (Item)bundle.get("pinLocation");
                    getSupportActionBar().setTitle(item.get_ITEM_NAME() + " Location");
                }
                *//*
                Intent intent = new Intent(MainItemActivity.this, PinsActivity.class);
                intent.putExtra("roomFaceName", itemObject.get_ITEM_LOCATION());
                startActivity(intent);
            }
        });*/


        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        // Grab item name from intent -- might throw null pointerexception??
        if (bundle.containsKey("itemName")){
            itemName = (String)bundle.get("itemName");
            getSupportActionBar().setTitle(itemName);
        }

        database = MainDB.getInstance(getApplicationContext());
        (new DownloadFromDB()).execute(database);

        //itemObject = database.getItemFromDB(itemName); redundant code
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_item, menu);
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
        } else if (id == R.id.action_delete) {
            database.deleteItemFromDB(itemObject);

            Intent intent = new Intent(this, AllItemsActivity.class);
            intent.putExtra("activity","mainItem");
            intent.putExtra("itemName", itemName);
            startActivity(intent);
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
            intent.putExtra("action","view");
            startActivity(intent);

        } else if (id == R.id.nav_all_items) {
            intent = new Intent(this, AllItemsActivity.class);
            intent.putExtra("activity","mainItem");
            intent.putExtra("itemName","none");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class DownloadFromDB extends AsyncTask<MainDB, Void, Item> {

        @Override
        protected  Item doInBackground(MainDB... params) {
            itemObject = database.getItemFromDB(itemName);

            //Query for all the images and put them in the images array I already created.
            return itemObject;
        }

        protected void onPostExecute(Item item) {

            itemRoom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainItemActivity.this, PinsActivity.class);
                    intent.putExtra("roomFaceName", itemObject.get_ITEM_LOCATION());
                    Log.d(TAG, "ROOM FACE" + itemObject.get_ITEM_LOCATION());
                    startActivity(intent);
                }
            });

            // Update activity display
            itemImage.setImageBitmap(item.getBitmap());
            itemDesc.setText(item.get_ITEM_DESC());
            String trim = item.get_ITEM_LOCATION();
            if (trim.contains("_")) {
                int cutoff = trim.indexOf('_');
                itemRoom.setText(trim.substring(0, cutoff) + " -- Tap to see pin location");
            }

            // Update item values
            item.inc_VIEW_CNT();
            Calendar calendar = Calendar.getInstance();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String datetime = dateFormat.format(calendar.getTime());
            item.set_ITEM_ACCESS(datetime);

            database.updateItemInDB(item);



        }
    }
}
