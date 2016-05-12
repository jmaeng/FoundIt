package com.example.jmaeng.found_it;

import android.content.ClipData;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MainDB database;
    private EditText nameField;
    private ImageButton imageField;
    private TextView dateField;
    private String datetime;
    private EditText descField;
    private Button roomField;
    private Button finalizeItem;
    private Item item;
    private final static int PICK_IMAGE_REQUEST = 1;
    private byte[] itemImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        Intent intent = getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Connect to database //
        item = new Item();
        database = MainDB.getInstance(getApplicationContext());

        // Name //
        nameField = (EditText)findViewById(R.id.itemNameField);

        // Image //
        imageField = (ImageButton)findViewById(R.id.itemImageButton);
        imageField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent callIntent = new Intent(this, _some_class_name_);
                startActivity(callIntent);
                */
                //Uploads from gallery and sets the item img to the uploaded photo.

                //TODO Still doesn't set up the imageView for showing the actual photo of the item though.

                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), PICK_IMAGE_REQUEST);

                Snackbar.make(v, "Call image select",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        // Item Creation Time //
        dateField = (TextView)findViewById(R.id.itemCreationField);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy. hh:mm aaa");
        datetime = dateFormat.format(calendar.getTime());
        dateField.setText(datetime);
        item.set_ITEM_CREATED(datetime);

        // Description //
        descField = (EditText)findViewById(R.id.itemDescField);

        // Room Selector (also gets x-,y-coordinates) //
        roomField = (Button)findViewById(R.id.itemRoomButton);
        roomField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Intent callIntent = new Intent(this, _some_class_name_);
                startActivity(callIntent);
                */
                //TODO send to room select + location select
                Snackbar.make(v, "Call room select",
                        Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });

        // Finalize Item //
        finalizeItem = (Button)findViewById(R.id.itemCreateItem);
        finalizeItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!finalizeItemCreation())
                    Snackbar.make(v, "Item names must be unique.",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else
                    Snackbar.make(v, item.get_ITEM_NAME() + " successfully added.",
                            Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    /* Handling the image chosen from the gallery */
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {

            if(data.getData() != null){
                //One image selected
                try {
                    Uri mImageUri=data.getData();
                    Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), mImageUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    item.set_ITEM_IMG(byteArray);

                } catch (IOException e) {
                    Log.e("LOG_TAG", "Caught IOException: " + e.getMessage()); //Should never get here
                }
            } else {
                Toast toast = Toast.makeText(getApplicationContext(), "Please pick an Item Image", Toast.LENGTH_SHORT);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.add_item, menu);
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
     * Add item to database
     * @return True if successful, otherwise false
     */
    private boolean finalizeItemCreation() {
        String itemName = nameField.getText().toString();

        // Names must be unique, check to see if it exists
        if(database.checkItemInDB(itemName))
            return false;

        // Process Item contents
        item.set_ITEM_NAME(itemName);
        item.set_ITEM_DESC(descField.getText().toString());
        item.set_ITEM_ACCESS(datetime);
        item.set_ITEM_CREATED(datetime);
        item.set_ITEM_VIEW_CNT(0);
        item.set_ITEM_LOCATION("some room"); //TODO
        item.set_ITEM_X(0); //TODO
        item.set_ITEM_Y(0); //TODO
        item.set_ITEM_IMG(null); //TODO

        boolean success = database.addNewItemToDB(item);
        return success;
    }
}
