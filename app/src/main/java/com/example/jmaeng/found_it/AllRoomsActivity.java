package com.example.jmaeng.found_it;

import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
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
    private static final int COLS = 2;
    private RecyclerView allRoomRecyclerView;
    private GridLayoutManager gridLayoutManager;
    private RecyclerViewAdaptor allRoomAdapter;
    private DownloadFromDB allRoomTask;
    private static final String TAG = AllRoomsActivity.class.getSimpleName();
    private String pinActivityAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rooms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get info from DB for this activity
        mainDatabase = MainDB.getInstance(getApplicationContext());
        //(new DownloadFromDB()).execute(mainDatabase);


        //Set up FAB button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
                startActivity(intent);
            }
        });


        //Set up Create Room Button
        Button createRoomButton = (Button)findViewById(R.id.create_room_button);
        createRoomButton.setBackgroundColor(Color.DKGRAY);
        createRoomButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), CreateRoomActivity.class));
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

        allRoomRecyclerView = (RecyclerView)findViewById(R.id.recycler_view);
        gridLayoutManager = new GridLayoutManager(AllRoomsActivity.this, COLS);
        allRoomRecyclerView.setHasFixedSize(true);
        allRoomRecyclerView.setLayoutManager(gridLayoutManager);
    }

    private class DownloadFromDB extends AsyncTask<MainDB, Void, ArrayList<Room>> {

        @Override
        protected  ArrayList<Room> doInBackground(MainDB... params) {
            MainDB db = params[0];
            //Query for all the images and put them in the images array I already created.
            return db.getAllRoomImages();
        }

        //TODO right now it is showing all the rooms walls, when I want to just pick one of the walls and put a name on the image as well
        protected void onPostExecute(final ArrayList<Room> rooms) {
            roomArray = rooms;
            if (allRoomAdapter == null) {
                allRoomAdapter = new RecyclerViewAdaptor();
                allRoomRecyclerView.setAdapter(allRoomAdapter);
            } else {
                allRoomAdapter.notifyDataSetChanged();
            }
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
            final Room room = roomArray.get(position);
            holder.getImageView().setImageBitmap(room.getBitmap());
            holder.getNameView().setText(room.getName());

            holder.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(AllRoomsActivity.this, MainRoomActivity.class);
                    intent.putExtra("roomName", room.getName().toString());

                    if (getIntent() != null) {
                        if (getIntent().getExtras() != null) {
                            if (getIntent().getExtras().get("action") != null) {
                                intent.putExtra("action",getIntent().getExtras().getString("action"));
                            }
                        }
                    }

                    startActivity(intent);
                }
            });

            holder.getImageView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    final Room tempRoom = mainDatabase.getRoomFromDB(room.getName());

                    mainDatabase.deleteRoomFromDB(room);
                    roomArray.remove(room);
                    notifyDataSetChanged();

                    Snackbar snackbar = Snackbar
                            .make(v, room.getName() + " has been deleted", Snackbar.LENGTH_LONG)
                            .setAction("UNDO", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    //add back to database
                                    mainDatabase.addNewRoomToDB(tempRoom);
                                    roomArray.add(tempRoom);
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
            if (roomArray != null)
                return roomArray.size();
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
            intent = new Intent(this, AllItemsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRestart() {
        super.onRestart();
        //have to requery the database to change all the carousels
        allRoomTask = new DownloadFromDB();
        allRoomTask.execute(mainDatabase);
    }

    @Override
    public void onResume() {
        super.onResume();
        //have to requery the database to change all the carousels
        allRoomTask = new DownloadFromDB();
        allRoomTask.execute(mainDatabase);
    }

    @Override
    public void onStop(){
        super.onStop();
        allRoomTask.cancel(false);
    }
}

