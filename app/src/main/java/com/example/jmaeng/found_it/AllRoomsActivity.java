package com.example.jmaeng.found_it;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AllRoomsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MainDB mainDatabase;
    private ArrayList<Room> roomArray;
    private GridView gridView;
    private static final int THUMBNAIL_SIZE = 400;
    private static final int PADDING = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rooms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set up GridView
        gridView = (GridView)findViewById(R.id.allRoomGridView);

        //get info from DB for this activity
        mainDatabase = MainDB.getInstance(getApplicationContext());
        (new DownloadFromDB()).execute(mainDatabase);

        //ClickListener for each grid (room thumbnail) in gridview TODO
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO this should go straight in to the Room Info activity with an intent with a message
                Intent intent = new Intent(AllRoomsActivity.this, MainRoomActivity.class);
                intent.putExtra("roomName", roomArray.get(position).getName());
                startActivity(intent);
            }
        });

        //Set up FAB button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show(); //TODO
            }
        });

        //Set up Create Room Button
        Button createRoomButton = (Button)findViewById(R.id.create_room_button);
        createRoomButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Make This button do something bro", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                        //TODO waiting for create room activity with camera and photo upload functionality to be complete by Tyler
            }
        });

        //Set up navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //Obtain intents
        Intent intent = getIntent();

    }

    /*
    ImageAdapter for all the images in the grid representing different rooms
     */
    public class ImageAdapter extends BaseAdapter {
        private Context context;

        public ImageAdapter(Context c) {
            context = c;
        }
        @Override
        public int getCount() {
            return roomArray.size();
        }

        @Override
        public Object getItem(int position) {
            return roomArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        //create new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            TextView textView;
            View view;
            Room room = roomArray.get(position);

            if (convertView == null) {
                LayoutInflater li = getLayoutInflater();
                view = li.inflate(R.layout.room_grid, null);
                textView = (TextView)view.findViewById(R.id.room_name);
                textView.setText(room.getName());
                imageView = (ImageView)view.findViewById(R.id.room_image);
                imageView.setImageBitmap(room.getBitmap());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

                view.setLayoutParams(new GridView.LayoutParams(THUMBNAIL_SIZE, THUMBNAIL_SIZE));
                view.setPadding(PADDING, PADDING, PADDING, PADDING);

            } else {
                view = convertView;
            }

            return view;
        }
    }

    /*
    AsyncTask class that will obtain info from the database about all the rooms and then set up the
    gridview layout with all the obtained information
     */
    private class DownloadFromDB extends AsyncTask<MainDB, Void, ArrayList<Room>> {

        @Override
        protected  ArrayList<Room> doInBackground(MainDB... params) {
            MainDB db = params[0];
            //Query for all the images and put them in the images array I already created.
            return db.getAllRooms();
        }

        //TODO right now it is showing all the rooms walls, when I want to just pick one of the walls and put a name on the image as well
        protected void onPostExecute(final ArrayList<Room> rooms) {
            roomArray = rooms;
            gridView.setAdapter(new ImageAdapter(AllRoomsActivity.this));
        }
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
        getMenuInflater().inflate(R.menu.all_rooms, menu);
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
            //TODO do nothing?
        } else if (id == R.id.nav_all_items) {
            //TODO
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
