package com.example.jmaeng.found_it;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

public class AllRoomsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_rooms);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        //set image carousel
        //TODO need to change this to an array that is dynamic with our database of room images
        Integer images[] = {R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery,
                R.drawable.ic_menu_gallery, R.drawable.ic_menu_gallery};

        for (Integer image: images) {
            addImagestoImageCarousel(image);
        }
    }

    /*
    Adds images to the image carousel
     */
    private void addImagestoImageCarousel(Integer image) {
        LinearLayout imageCarousel = (LinearLayout)findViewById(R.id.image_carousel);
        ImageView myImage = new ImageView(this);
        myImage.setImageResource(image);
        myImage.setLayoutParams(new AbsListView.LayoutParams(
                AbsListView.LayoutParams.MATCH_PARENT,
                AbsListView.LayoutParams.WRAP_CONTENT));
        imageCarousel.addView(myImage);
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
