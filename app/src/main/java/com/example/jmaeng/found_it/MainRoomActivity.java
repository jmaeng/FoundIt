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
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainRoomActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //TODO NEED TO UPDATE ROOM GRIDVIEW, IF ROOM DATABASE CHANGES

    private MainDB mainDatabase;
    private ArrayList<RoomFace> roomFacesArray;
    private GridView gridView;
    private static final int THUMBNAIL_SIZE = 400;
    private static final int PADDING = 10;
    private static final int COLS = 2;
    private String roomName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rooms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get info from DB for this activity
        mainDatabase = MainDB.getInstance(getApplicationContext());
        (new DownloadFromDB()).execute(mainDatabase);

        roomName = getIntent().getStringExtra(roomName);

        /** Dont need FAB here??
         //Set up FAB button
         FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
         fab.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
        startActivity(intent);
        }
        });\
         */

        //Set up navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private class DownloadFromDB extends AsyncTask<MainDB, Void, ArrayList<RoomFace>> {

        @Override
        protected  ArrayList<RoomFace> doInBackground(MainDB... params) {
            if (getIntent() != null){
                roomName = getIntent().getStringExtra("roomName");
            }
            MainDB db = params[0];
            //Query for all the images and put them in the images array I already created.
            return db.getRoomFaceImages(roomName);
        }

        //TODO right now it is showing all the rooms walls, when I want to just pick one of the walls and put a name on the image as well
        protected void onPostExecute(final ArrayList<RoomFace> roomFaces) {
            roomFacesArray = roomFaces;
            //gridView.setAdapter(new ImageAdapter(AllRoomsActivity.this));
            //TODO How is this really saving us resources if we inflate recycler view continuously?
            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view); //this is returning null and I don't know why...
            GridLayoutManager glm = new GridLayoutManager(MainRoomActivity.this, COLS);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(glm);
            recyclerView.setAdapter(new RecyclerViewAdaptor());
        }
    }

    //the Recycler Adaptor that will connect the room data to UI
    public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.ViewHolder> {
        public RecyclerViewAdaptor() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.room_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final RoomFace roomFace = roomFacesArray.get(position);
            holder.getImageView().setImageBitmap(roomFace.getBitmap());
            holder.getNameView().setText(roomFace.getRoomFace());

            holder.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  //create intent that goes to image and pin dots..
                }
            });

            holder.getImageView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final RoomFace tempFace = mainDatabase.getFaceFromDB(roomFace.getRoomFace());

                    mainDatabase.deleteFaceFromDB(roomFace);
                    roomFacesArray.remove(roomFace);
                    notifyDataSetChanged();

                    Snackbar snackbar = Snackbar
                            .make(v, roomFace.getRoomFace() + " has been deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //add back to database
                                    mainDatabase.addNewFaceToDB(tempFace);
                                    roomFacesArray.add(tempFace);
                                    //refresh RecyclerView again
                                    notifyDataSetChanged();
                                }
                            });
                    snackbar.show();
                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            if (roomFacesArray != null)
                return roomFacesArray.size();
            return 0;
        }


        //setting up each view, which are inflated from room_view.xml
        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView roomName;
            private final ImageView roomImage;

            public ViewHolder(View roomView) {
                super(roomView);
                roomName = (TextView) roomView.findViewById(R.id.room_name);
                roomImage = (ImageView)roomView.findViewById(R.id.room_image);
                roomView.setLayoutParams(new GridView.LayoutParams(THUMBNAIL_SIZE, THUMBNAIL_SIZE));
                roomView.setPadding(PADDING, PADDING, PADDING, PADDING);

            }

            public ImageView getImageView() {
                return roomImage;
            }

            public TextView getNameView(){
                return roomName;
            }
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

