package com.example.jmaeng.found_it;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CreateRoomActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MainDB mainDatabase;
    private EditText roomNameField;
    private Room room;
    private Button createRoomButton;
    private GridView gridView;
    private ArrayList<Uri> mArrayUri = null;
    private final static int THUMBNAIL_SIZE = 400;
    private final static int PADDING = 10;
    private final static int PICK_IMAGE_REQUEST = 1;
    private static final String TAG = CreateRoomActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gridView = (GridView)findViewById(R.id.mainRoomGridView);
        mainDatabase = MainDB.getInstance(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        roomNameField = (EditText)findViewById(R.id.room_name_field);
        createRoomButton = (Button)findViewById(R.id.create_room_button);
        mArrayUri=new ArrayList<Uri>();

        /* Have the create room button access the photo gallery of the phone and upload images here */
        createRoomButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), PICK_IMAGE_REQUEST);
            }

        });

         /*This fab button will finalize the room creation process if pressed*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(finalizeRoomCreation()) {
                    Intent intent = new Intent(CreateRoomActivity.this, MainRoomActivity.class);
                    intent.putExtra("roomName", room.getName());
                    intent.putExtra("action","view");
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            //If Single image selected then it will fetch from Gallery
            if (data.getData() != null) {
                //One image selected
                Uri mImageUri = data.getData();
                mArrayUri.add((Uri) mImageUri);

            } else {
                //Multiple images selected
                if (data.getClipData() != null) {
                    ClipData mClipData = data.getClipData();

                    for (int i = 0; i < mClipData.getItemCount(); i++) {
                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);
                    }
                    Log.d(TAG, "Selected Images" + mArrayUri.size());
                }
            }
            if (mArrayUri != null) {
                //LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mylin);
                //linearLayout.removeAllViews();
                ((TextView) findViewById(R.id.imageSelected)).setVisibility(View.VISIBLE);
                for (int i = 0; i < mArrayUri.size(); i++) {
                    ImageView iv = new ImageView(this);
                    iv.setLayoutParams(new AbsListView.LayoutParams(
                            AbsListView.LayoutParams.MATCH_PARENT,
                            AbsListView.LayoutParams.MATCH_PARENT));

                    try {
                        gridView.setAdapter(new ImageAdapter(CreateRoomActivity.this));
                        /*Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mArrayUri.get(i));
                        iv.setImageBitmap(bitmap);
                        linearLayout.addView(iv);*/
                    } catch (/*IO*/Exception e) {
                        Log.e("LOG_TAG", "Caught IOException: " + e.getMessage()); //Should never get here
                    }

                }

            }
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
            return mArrayUri.size();
        }

        @Override
        public Object getItem(int position) {
            return mArrayUri.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }
        //create new ImageView for each item referenced by the Adapter
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView;
            //Uri uri = mArrayUri.get(position);

            if (convertView == null && mArrayUri != null) {
                imageView = new ImageView(context);

                try {
                    imageView.setImageBitmap(MediaStore.Images.Media.getBitmap(getContentResolver(), mArrayUri.get(position)));
                } catch (IOException e) {
                    Log.e("LOG_TAG", "Caught IOException: " + e.getMessage()); //Should never get here
                    e.printStackTrace();
                }
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setLayoutParams(new GridView.LayoutParams(THUMBNAIL_SIZE, THUMBNAIL_SIZE));
                imageView.setPadding(PADDING, PADDING, PADDING, PADDING);

            } else {
                imageView = (ImageView)convertView;
            }

            return imageView;
        }
    }


    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        return outputStream.toByteArray();
    }

    /* Will create the new room i*/
    public boolean finalizeRoomCreation(){
        String roomName = roomNameField.getText().toString();

        boolean nameExists = mainDatabase.checkRoomInDB(roomName);
        //Room name must be unique
        if (nameExists || mArrayUri == null || roomName.equals("") || mArrayUri.size() == 0) {
            if (nameExists) {
                selectPicPlz(2);
            } else if (mArrayUri == null || mArrayUri.size() == 0){
                selectPicPlz(1);
            } else {
                selectPicPlz(0);
            }

            return false;
        }

        if (mArrayUri != null && !roomName.equals("")) {
            int increment = 0;
            for (int i = 0; i < mArrayUri.size(); i++ ) {
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mArrayUri.get(i));

                    byte[] byteArray = getBitmapAsByteArray(bmp);

                    if (i == 0) {
                        room = new Room(roomName,byteArray);
                        mainDatabase.addNewRoomToDB(room);
                    }
                    RoomFace rf = new RoomFace(roomName + "_" + i, roomName, byteArray);
                    //Log.d(TAG, "ROOM FACE " + rf.getRoomFace() + " has image " + rf.getImage().length);
                    mainDatabase.addNewFaceToDB(rf);

                } catch (IOException e) {
                    Log.e("LOG_TAG", "Caught IOException: " + e.getMessage()); //Should never get here
                }
            }
            mArrayUri.clear();
            gridView.setAdapter(null);
            ((TextView) findViewById(R.id.imageSelected)).setVisibility(View.INVISIBLE);


        }
        return true;
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
        getMenuInflater().inflate(R.menu.create_room, menu);
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
            intent.putExtra("activity","createRoom");
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Tells user to select a picture in a toast
     * wrong -> 0 = name is missing
     *         1 = pics are missing
     */
    private void selectPicPlz(int wrong){
        Context context = getApplicationContext();
        CharSequence text = "";
        if (wrong == 0) {
            text = "Please enter a room name.";
        } else if (wrong == 1) {
            text = "Please select an image.";
        } else if (wrong == 2) {
            text = "Room Name must be unique.";
        }

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
