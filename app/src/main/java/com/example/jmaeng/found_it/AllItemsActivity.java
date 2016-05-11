package com.example.jmaeng.found_it;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AllItemsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    private MainDB mainDatabase;
    private ArrayList<Item> itemArray;
    private GridView gridView;
    private static final int THUMBNAIL_SIZE = 400;
    private static final int PADDING = 10;
    private static final int COLS = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_items);

        Intent intent = getIntent();

        //get info from DB for this activity
        mainDatabase = MainDB.getInstance(getApplicationContext());
        (new DownloadFromDB()).execute(mainDatabase);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_items, menu);
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
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    /*
    AsyncTask class that will obtain info from the database about all the items and then set up the
    gridview layout with all the obtained information
     */
    private class DownloadFromDB extends AsyncTask<MainDB, Void, ArrayList<Item>> {

        @Override
        protected  ArrayList<Item> doInBackground(MainDB... params) {
            MainDB db = params[0];
            //Query for all the images and put them in the images array I already created.
            return db.getAllItemImages();
        }

        protected void onPostExecute(final ArrayList<Item> items) {
            itemArray = items;
            RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recycler_view_item); //this is returning null and I don't know why...
            GridLayoutManager glm = new GridLayoutManager(AllItemsActivity.this, COLS);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(glm);
            recyclerView.setAdapter(new AllItemsActivity.RecyclerViewAdaptor());
        }
    }

    //the Recycler Adaptor that will connect the item data to UI
    public class RecyclerViewAdaptor extends RecyclerView.Adapter<RecyclerViewAdaptor.ViewHolder> {

        public RecyclerViewAdaptor() {

        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            final Item item = itemArray.get(position);
            holder.getImageView().setImageBitmap(item.getBitmap());
            holder.getNameView().setText(item.get_ITEM_NAME());

            holder.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    /*
                    TODO
                    Intent intent = new Intent(AllItemsActivity.this, MainItemActivity.class);
                    intent.putExtra("itemName", item.get_ITEM_NAME());
                    startActivity(intent);
                    */
                }

            });
        }

        @Override
        public int getItemCount(){
            if(itemArray != null)
                return itemArray.size();
            return 0;
        }


        //setting up each view, which are inflated from item_view.xml
        public class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView itemName;
            private final ImageView itemImage;

            public ViewHolder(View itemView) {
                super(itemView);
                itemName = (TextView) itemView.findViewById(R.id.item_name);
                itemImage = (ImageView)itemView.findViewById(R.id.item_image);
                itemView.setLayoutParams(new GridView.LayoutParams(THUMBNAIL_SIZE, THUMBNAIL_SIZE));
                itemView.setPadding(PADDING, PADDING, PADDING, PADDING);

            }

            public ImageView getImageView() {
                return itemImage;
            }

            public TextView getNameView(){
                return itemName;
            }

        }
    }

}
