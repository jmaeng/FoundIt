package com.example.jmaeng.found_it;

import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class CreateRoomActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MainDB mainDatabase;
    private ArrayList<Uri> mArrayUri = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_room);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mainDatabase = MainDB.getInstance(getApplicationContext());
        mArrayUri=new ArrayList<Uri>();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               addRooms();
            }
        });

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


    /**
     * On click gallery will send an intent to open pictures from phones gallery
     * note have to old down button for multiple pictures
     */
    private int PICK_IMAGE_REQUEST = 1;

    public void gallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Camera takes pictures of room with camera
     * @param view
     */
    public void camera(View view) {
        //TODO we arent implemnting this??
    }


    /**
     * This makes an iageview the thmbnail
     *
     * */


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        /*
        //Following code is for recieveng camera intent but we aren't doing that?
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                ImageView iv = (ImageView)findViewById(R.id.mImageView);
                iv.setImageBitmap(BitmapFactory.decodeFile(path));
                // Image captured and saved to fileUri specified in the Intent
                // Toast.makeText(this, "Image saved to:\n" +
                //       data.getData(), Toast.LENGTH_LONG).show();\
                //data.getData();
                Toast.makeText(this, "Image saved to:\n" + path,
                        Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
        }
        */


        //Gallery

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null){// && data.getData() != null) {




            //If Single image selected then it will fetch from Gallery

            if(data.getData()!=null){
                //One image selected
                Uri mImageUri=data.getData();
                mArrayUri.add((Uri)mImageUri);


            }else{
                //Multiple images selected
                if(data.getClipData()!=null){

                    ClipData mClipData=data.getClipData();

                    for(int i=0;i<mClipData.getItemCount();i++){

                        ClipData.Item item = mClipData.getItemAt(i);
                        Uri uri = item.getUri();
                        mArrayUri.add(uri);

                    }



                   /*
                    LinearLayout linearLayout = (LinearLayout) findViewById(R.id.mylin);
                    for (int i = 0; i < mArrayUri.size(); i ++) {
                        ImageView iv = new ImageView(this);
                        iv.setLayoutParams(new AbsListView.LayoutParams(
                                AbsListView.LayoutParams.MATCH_PARENT,
                                AbsListView.LayoutParams.WRAP_CONTENT));

                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), mArrayUri.get(i));
                            iv.setImageBitmap(bitmap);
                            linearLayout.addView(iv);
                        } catch (Exception e) {

                        }

                    }
                    */
                    Log.v("LOG_TAG", "Selected Images" + mArrayUri.size());
                }

            }

        }

    }

    /**
     * Add room just made to the database
     */
    private void addRooms() {

        EditText et = (EditText)findViewById(R.id.editName);
        String name = et.getText().toString();

        if (mArrayUri != null && !name.equals("")) {
            int increment = 0;
            for (int i = 0; i < mArrayUri.size(); i++ ) {
                try {
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mArrayUri.get(i));
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();


                    String tempName = name;
                    while(mainDatabase.getRoomFromDB(tempName) != null){ //If room name already exsists add an increment to it
                        tempName = name + (++increment);
                    }
                    name = tempName;
                    if (i == 0) {
                        Room room = new Room(name,byteArray);
                        mainDatabase.addNewRoomToDB(room);
                    } else {
                        RoomFace rf = new RoomFace(name+"_" + i,name,byteArray);
                        mainDatabase.addNewFaceToDB(rf);
                    }
                    //TODO where to go after fab is pressed
                } catch (IOException e) {
                    Log.e("LOG_TAG", "Caught IOException: " + e.getMessage()); //Should never get here
                }
            }
        } else {
            if (name.equals("")) {
                selectPicPlz(0);
            } else if (mArrayUri == null) {
                selectPicPlz(1);
            }
        }

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
            text = "Please select a picture.";
        }

        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
