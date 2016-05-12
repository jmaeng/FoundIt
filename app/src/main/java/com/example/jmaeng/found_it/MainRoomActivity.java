package com.example.jmaeng.found_it;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainRoomActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String roomName;
    private MainDB mainDatabase;
    private ArrayList<RoomFace> roomFaceArray;
    private GridView gridView;
    private final static int THUMBNAIL_SIZE = 400;
    private final static int PADDING = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Set up GridView
        gridView = (GridView)findViewById(R.id.mainRoomGridView);

        //ClickListener for each grid (room thumbnail) in gridview TODO
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TODO this should go straight into the bigger image of the room_face with location of items
               /* Intent intent = new Intent(MainRoomActivity.this, MainRoomActivity.class);
                intent.putExtra("roomName", roomArray.get(position).getName());
                startActivity(intent);*/ //TODO need to edit this to be the correct one when clicking a roomFace image
            }
        });



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b.containsKey("roomName")){
            roomName = (String)b.get("roomName"); //got name of room we want to show that already exists
            getSupportActionBar().setTitle(roomName);
            // System.out.println("intent returns: " + roomName); //WHOO HOO IT WORKS!
        }

        //handle database actions
        mainDatabase = MainDB.getInstance(getApplicationContext());
        (new DownloadFromDB()).execute(mainDatabase);
    }

    /*
    AsyncTask class that will obtain all room_faces of the current room from the room_faces table
    and show it as a gridView here.
     */
    private class DownloadFromDB extends AsyncTask<MainDB, Void, ArrayList<RoomFace>> {

        @Override
        protected  ArrayList<RoomFace> doInBackground(MainDB... params) {
            MainDB db = params[0];
            //Query for all the images and put them in the images array I already created.
            return db.getRoomFaceImages(roomName);
        }

        //TODO right now it is showing all the rooms walls, when I want to just pick one of the walls and put a name on the image as well
        protected void onPostExecute(final ArrayList<RoomFace> roomFaces) {
            roomFaceArray = roomFaces;
            gridView.setAdapter(new ImageAdapter(MainRoomActivity.this));
        }
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
            return roomFaceArray.size();
        }

        @Override
        public Object getItem(int position) {
            return roomFaceArray.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        //create new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            RoomFace roomFace = roomFaceArray.get(position);

            convertView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Context context = getApplicationContext();
                    CharSequence text = "Hello toast!";
                    int duration = Toast.LENGTH_SHORT;

                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();

                    return true;
                }
            });

            if (convertView == null) {
                imageView = new ImageView(context);
                imageView.setImageBitmap(roomFace.getBitmap());
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new GridView.LayoutParams(THUMBNAIL_SIZE, THUMBNAIL_SIZE));
                imageView.setPadding(PADDING, PADDING, PADDING, PADDING);


            } else {
                imageView = (ImageView)convertView;
            }

            return imageView;
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
        getMenuInflater().inflate(R.menu.main_room, menu);
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
            //TODO
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
