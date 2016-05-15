package com.example.jmaeng.found_it;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PinsActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String roomFaceTitle;
    private MainDB mainDatabase;
    private ImageView roomFaceImage;
    private TextView roomFaceName;
    private RoomFace roomFace;
    private static final String TAG = PinsActivity.class.getSimpleName();
    private Intent receivedIntent;
    private static final int RADIUS = 800;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pins);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //get info from DB for this activity
        mainDatabase = MainDB.getInstance(getApplicationContext());

        //Set up FAB button
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AddItemActivity.class);
                startActivity(intent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        roomFaceImage = (ImageView) findViewById(R.id.room_face_image);
        roomFaceName = (TextView) findViewById(R.id.room_face_name);

        /* Getting Intent from outside the activity like from MainRoomActivity */
        receivedIntent = getIntent();
        //receiving all room face information
        roomFaceTitle = receivedIntent.getExtras().getString("roomFaceName");
        //Log.d(TAG, "GOT ROOM NAME FROM INTENT: " + roomFaceTitle);

        /* Setting up the image with pin ability */
        BitmapFactory.Options myOptions = new BitmapFactory.Options();
        myOptions.inDither = true;
        myOptions.inScaled = false;
        myOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// important


        getSupportActionBar().setTitle(roomFaceTitle);

        (new DownloadFromDB()).execute(mainDatabase);

    }

    final float[] getPointerCoords(ImageView view, MotionEvent e) {
        final int index = e.getActionIndex();
        final float[] coords = new float[] { e.getX(index), e.getY(index) };
        Matrix matrix = new Matrix();
        view.getImageMatrix().invert(matrix);
        matrix.postTranslate(view.getScrollX(), view.getScrollY());
        matrix.mapPoints(coords);
        return coords;
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
        getMenuInflater().inflate(R.menu.pins, menu);
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
            intent.putExtra("action","view");
            startActivity(intent);

        } else if (id == R.id.nav_all_items) {
            intent = new Intent(this, AllItemsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class DownloadFromDB extends AsyncTask<MainDB, Void, RoomFace> {

        @Override
        protected RoomFace doInBackground(MainDB... params) {
            MainDB db = params[0];
            //Query for all the images and put them in the images array I already created.
            //return db.getRoomFaceImages(roomFaceTitle);
            return db.getFaceFromDB(roomFaceTitle);
        }

        //TODO right now it is showing all the rooms walls, when I want to just pick one of the walls and put a name on the image as well
        protected void onPostExecute(final RoomFace rface) {
            roomFace = rface;
            Bitmap bitmap = roomFace.getBitmap();

            final Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setColor(Color.MAGENTA);

            DisplayMetrics metrics = getBaseContext().getResources().getDisplayMetrics();
            int w = metrics.widthPixels;
            int h = metrics.heightPixels;

            //Bitmap workingBitmap = Bitmap.createBitmap(bitmap);
            final Bitmap mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

            final Canvas canvas = new Canvas(mutableBitmap);
            canvas.setBitmap(mutableBitmap);

            final ImageView imageView = (ImageView)findViewById(R.id.room_face_image);
            imageView.setAdjustViewBounds(true);
            imageView.setImageBitmap(mutableBitmap);

             /* Tapping to create a pin if intent is from Add Items Activity */
            if (getIntent().getExtras().containsKey("AddItemRequest")) {
                //Log.d(TAG, "IN PIN ACTIVITY FROM ADD ITEM -- FINAL DESTINATION");
                roomFaceImage.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        final float[] coords = getPointerCoords(roomFaceImage, event);
                        switch (event.getAction()) {
                            case MotionEvent.ACTION_DOWN:
                                canvas.drawCircle(coords[0], coords[1], 5, paint);
                                imageView.invalidate();
                            case MotionEvent.ACTION_UP:
                                RoomFace rf = mainDatabase.getFaceFromDB(roomFaceTitle);
                                rf.setImage(mutableBitmap);
                                mainDatabase.updateFaceInDB(rf);

                            /* Returning to the previous add item activity that call this activity
                            * in the first place */
                                Intent intent = new Intent();
                                intent.putExtra("RoomFaceName",roomFace.getRoomFace());
                                intent.putExtra("X_coord", coords[0]);
                                intent.putExtra("Y_coord", coords[1]);
                                setResult(RESULT_OK, intent);
                                finish();
                        }
                        return true;
                    }
                });
            } else {
                //if this activity is not called by the add item activity, then make all coord
                // points clickable
                //query for all items in items table that lives in this room face and get their x y coords
                (new DownloadAllItemsFromDB()).execute(mainDatabase);

            }
        }
    }

    private class DownloadAllItemsFromDB extends AsyncTask<MainDB, Void, ArrayList<Item>> {

        @Override
        protected ArrayList<Item> doInBackground(MainDB... params) {
            MainDB db = params[0];
            //Query for all the images and put them in the images array I already created.
            //return db.getRoomFaceImages(roomFaceTitle);
            return db.getAllItemsInRoomFaceFromDB(roomFaceTitle);
        }

        protected void onPostExecute(final ArrayList<Item> itArray) {
            //what to do with alls the items in this room face?
            Log.d(TAG, "FOUND " + itArray.size() + " ITEMS IN THIS ROOM FACE");
            roomFaceImage.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    final int action = event.getAction();
                    final float x = event.getX();
                    final float y = event.getY();
                    switch(action) {
                        case MotionEvent.ACTION_UP :
                            Item i;
                            Item foundItem = getItemWithXandY(itArray, x, y);
                            if (foundItem != null) {
                                Intent sendingIntent = new Intent(PinsActivity.this, MainItemActivity.class);
                                sendingIntent.putExtra("itemName", foundItem.get_ITEM_NAME());
                                startActivity(sendingIntent);
                            } else {
                                Toast toast = Toast.makeText(getApplicationContext(), "No Item in that Spot!", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                    }
                    return true;
                }
            });
        }
    }

    private Item getItemWithXandY(ArrayList<Item> itArray, float x, float y) {
        for (Item i: itArray) {
            //Log.d(TAG, "Got item " + i.get_ITEM_NAME() + " with x and y of : " + i.get_ITEM_X() + " "  + i.get_ITEM_Y());
            if (isWithinRange(i.get_ITEM_X(), i.get_ITEM_Y(), RADIUS,  x, y)) {
                return i;
            }
        }
        return null;
    }
    private boolean isWithinRange(float center_x, float center_y, int radius, float x, float y){
        //Log.d(TAG, "isWithinRange = " + center_x + " " + center_y + " " + x  + " " + y);
        int dist = (int)Math.sqrt((center_x - x)*(center_x - x) + (center_y - y)*(center_y - y));
        //Log.d(TAG, "dis = " + dist + ", radius = " + radius);
        return dist <= radius;
    }


}
