package com.example.jmaeng.found_it;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class CreateRoomActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MainDB mainDatabase;
    private EditText roomNameField;
    private Room room;
    private Button createRoomButton;
    private ArrayList<byte[]> roomFaceImageArray;
    private ArrayList<Bitmap> selectedRoomFacesArray;
    private final static int REQUEST_CODE = 0;
    private final static int PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainDatabase = MainDB.getInstance(getApplicationContext());

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /* Getting all the info for the new room being created */
        roomFaceImageArray = new ArrayList<byte[]>();
        selectedRoomFacesArray = new ArrayList<Bitmap>();

        roomNameField = (EditText)findViewById(R.id.room_name_field);
        createRoomButton = (Button)findViewById(R.id.create_room_button);

        /* Have the create room button access the photo gallery of the phone and upload images here */
        createRoomButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overridden method

                } else {
                    Toast.makeText(CreateRoomActivity.this, "Until you grant the permission, we cannot access your gallery", Toast.LENGTH_SHORT).show();
                }
            }

        });

         /*This fab button will finalize the room creation process if pressed*/
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!finalizeRoomCreation()) {
                    Snackbar.make(view, "Room name must be unique.",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                } else {
                    Intent intent = new Intent(CreateRoomActivity.this, MainRoomActivity.class);
                    intent.putExtra("roomName", room.getName());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == RESULT_OK && data != null) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                if (c != null) {
                    c.moveToFirst();

                    int colIdx = c.getColumnIndex(filePathColumn[0]);
                    String filePath = c.getString(colIdx);
                    c.close();

                    Bitmap selectedImg = BitmapFactory.decodeFile(filePath);
                    selectedRoomFacesArray.add(selectedImg);

                    /* TODO Send the image to the grid view to show and allow for more uploads or
                    going ahead and add this room entry */

                    //Convert and same image as byte array so we can save this to DB later
                    byte[] roomImage = getBitmapAsByteArray(selectedImg);
                    roomFaceImageArray.add(roomImage);
                }


            } else {
                Toast.makeText(this, "You haven't picked Image",
                        Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
            Toast.makeText(this, "Something went wrong", Toast.LENGTH_LONG)
                    .show();
        }

    }

    public static byte[] getBitmapAsByteArray(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        //bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
        return outputStream.toByteArray();
    }
    /* Will create the new room i*/
    public boolean finalizeRoomCreation(){
        String roomName = roomNameField.getText().toString();

        //Room name must be unique
        if (mainDatabase.checkRoomInDB(roomName)) {
            return false;
        }

        //Add to room database
        mainDatabase.addNewRoomToDB(new Room(roomName, roomFaceImageArray.get(0)));

        //Add all room faces to database
        int i = 1;
        for (byte[] roomFaceImg: roomFaceImageArray) {
            mainDatabase.addNewFaceToDB(new RoomFace(roomName + " " + i, roomName, roomFaceImg));
            i++;
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
            startActivity(intent);

        } else if (id == R.id.nav_all_items) {

            //TODO

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_CODE);
            } else {
                Toast.makeText(this, "Until you grant the permission, we cannot access your gallery", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
