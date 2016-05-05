package com.example.jmaeng.found_it;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class AddItemActivity extends AppCompatActivity {

    /**
     * Fields on the screen
     */
    private MainDB database;
    private EditText name_input;
    private TextView date_input;
    private EditText desc_input;
    private Spinner room_select;
    private Button proceed_next;

    private String datetime;

    /**
     * onCreate
     * @param savedInstanceState (Possible) previous state to restore from
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);
        Intent intent = getIntent();

        // Connect to Database //
        database = MainDB.getInstance(getApplicationContext());

        // Item Name //
        name_input = (EditText)findViewById(R.id.itemNameInput);

        // Date Display Field //
        date_input = (TextView)findViewById(R.id.itemCreatedOnInput);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy. hh:mm aaa");
        datetime = dateFormat.format(calendar.getTime());
        date_input.setText(datetime);

        // Description Input Text Field //
        desc_input = (EditText)findViewById(R.id.itemDescInput);

        // Room Select Spinner //
        room_select = (Spinner)findViewById(R.id.itemRoomSelect);
        ArrayList<String> addedRooms = new ArrayList<String>(); //TODO: grab list of all added rooms from database, not room face
        addedRooms.add("Kitchen"); addedRooms.add("Living Room"); addedRooms.add("Office"); //TODO: remove later
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, addedRooms);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        room_select.setAdapter(adapter);



        // Proceed To Next //
        //TODO: adjust to move to next activity, showing input for testing for now
        //TODO: use intent with extra message set to selected_room to narrow down room choice
        proceed_next = (Button)findViewById(R.id.itemNextButton);
        proceed_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeNewItem();
            }
        });
    }

    //TODO hardcoded location, x-coord, y-coord, image
    private void makeNewItem() {
        //TODO USE THIS TO EXTRACT USER SELECTION FOR SPINNER
        int spinner_selection = room_select.getSelectedItemPosition();
        String selected_room = room_select.getItemAtPosition(spinner_selection).toString();

        Item item = new Item();
        item.set_ITEM_NAME(name_input.getText().toString());
        item.set_ITEM_DESC(desc_input.getText().toString());
        item.set_ITEM_ACCESS(datetime);
        item.set_ITEM_CREATED(datetime);
        item.set_ITEM_VIEW_CNT(0);
        item.set_ITEM_LOCATION(selected_room); //TODO
        item.set_ITEM_X(0); //TODO
        item.set_ITEM_Y(0); //TODO
        item.set_ITEM_IMG(null); //TODO

        database.addNewItemToDB(item);
    }
}
