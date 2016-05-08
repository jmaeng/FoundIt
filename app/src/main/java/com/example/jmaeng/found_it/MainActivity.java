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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //TODO need to create an adaptor to handle the images and stop continual inflation of views
    //need to create a ViewHolder to stop wasting time finding views
    //need to make it a RecyclerView to force it to recycle views to make it go faster


    private NavigationView navigationView;
    private MainDB mainDatabase;
    private DownloadFromDB popCarouselTask, recentCarouselTask, lastViewCarouselTask;

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
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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

        popCarouselTask = (new DownloadFromDB(R.id.popular_image_carousel));
        /*recentCarouselTask = (new DownloadFromDB(R.id.recently_added_image_carousel));
        lastViewCarouselTask = (new DownloadFromDB(R.id.last_viewed_image_carousel));*/
        popCarouselTask.execute(mainDatabase);
       /* recentCarouselTask.execute(mainDatabase);
        lastViewCarouselTask.execute(mainDatabase);*/

    }

    private class DownloadFromDB extends AsyncTask<MainDB, Void, ArrayList<Bitmap>> {

        private int layoutID;

        public DownloadFromDB(int layoutID) {
            this.layoutID = layoutID;
        }

        @Override
        protected  ArrayList<Bitmap> doInBackground(MainDB... params) {
            MainDB db = params[0];
            //Query for all the images and put them in the images array I already created.
            //TODO need to change this method to point to the items table
            ArrayList<byte[]> imageArray = db.getAllImagesWithName();

            ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
            Bitmap b, compressedBitmap;
            BitmapFactory.Options options = new BitmapFactory.Options();
            int width = 100, height = 100;

            for (byte[] image: imageArray) {
                options.inJustDecodeBounds = true;
                b = BitmapFactory.decodeByteArray(image, 0, image.length, options);

                options.inSampleSize = calculateInSampleSize(options, width, height);
                options.inJustDecodeBounds = false;

                compressedBitmap = BitmapFactory.decodeByteArray(image, 0, image.length, options);
                bitmapArray.add(compressedBitmap);
            }
            return bitmapArray;
        }

        protected void onPostExecute(final ArrayList<Bitmap> bitmapArray) {
            addImagesToImageCarousel(bitmapArray, layoutID);
        }
    }

    /*
    Adds images to the image carousel
     */
   private void addImagesToImageCarousel(ArrayList<Bitmap> bitmapArray, int layoutID) { //OOM error here
       LinearLayout imageCarousel = (LinearLayout)findViewById(layoutID);
       WeakReference<ImageView> imageViewWeakReference;
       ImageView imageView;

       for (Bitmap bitmap: bitmapArray) {
           imageView = new ImageView(this);
           imageViewWeakReference = new WeakReference<ImageView>(imageView);
           if (imageViewWeakReference != null && bitmap != null) {
               imageView = imageViewWeakReference.get();
               float aspectRatio = bitmap.getWidth()/bitmap.getHeight();
               if (imageView != null) {
                   imageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 480, Math.round((480 / aspectRatio)), false));
                   imageCarousel.addView(imageView);
               }
           }
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
    protected void onDestroy() {
        super.onDestroy();
        mainDatabase.closeDB();
    }
}
