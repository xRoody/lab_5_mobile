package com.example.notebook;

import android.os.Build;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.database.SQLException;
import android.util.Log;
import android.widget.*;
import android.support.annotation.NonNull;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

import android.view.MenuItem;

public class NotesActivity extends AppCompatActivity {
    private Date date;

    Button dateButton;
    EditText dateBox;
    EditText nameBox;
    EditText bodyBox;
    EditText tagBox;
    Button delBtn;

    Button cancel;


    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    Cursor tagesCursor;
    long noteId = 0;

    Spinner mSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes);
        dateButton = findViewById(R.id.buttondate);
        dateBox = findViewById(R.id.editText);
        nameBox = findViewById(R.id.editText2);
        bodyBox = findViewById(R.id.editText3);
        tagBox = findViewById(R.id.editText4);
        delBtn = findViewById(R.id.button2);
        cancel = findViewById(R.id.otmena);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        dateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonDate_onclick(view);
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                go();
            }
        });

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            noteId = extras.getLong("id");
        }

        if (noteId > 0) {

            userCursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.NOTES + " WHERE " + DatabaseHelper.ID + "=? ",
                    new String[]{String.valueOf(noteId)});
            userCursor.moveToFirst();

            dateBox.setText(userCursor.getString(1));

            nameBox.setText(userCursor.getString(2));
            bodyBox.setText(userCursor.getString(3));
            tagBox.setText(userCursor.getString(4));
            userCursor.close();
        } else {
            delBtn.setVisibility(View.GONE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateBox.setText(LocalDate.now().toString());
            }
        }
        db = dbHelper.getReadableDatabase();
        tagesCursor = db.query("tags", new String[]{"_id", "bodytag"}, null, null, null, null, null);


        mSpinner = findViewById(R.id.spinner);

        final SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, android.R.layout.simple_spinner_item, tagesCursor, new String[]{"bodytag"}, new int[]{android.R.id.text1});

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinner.setAdapter(adapter);

        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String query = "SELECT * FROM tags";

                tagBox.setText(customQuery(query)[position][1]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }

    public String[][] customQuery(String query) {
        Cursor cur = null;
        try {
            cur = db.rawQuery(query, null);
        } catch (SQLException e) {
            Log.d("DATABASE", e.toString());
        }

        String data[][] = new String[cur.getCount()][cur.getColumnCount()];
        int i = 0;
        if (cur != null) {

            while (cur.moveToNext()) {
                int j = 0;
                while (j < 2) {
                    data[i][j] = cur.getString(j);
                    j++;
                }
                i++;

            }
            cur.close();
        }
        return data;
    }

    public void buttonDate_onclick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(R.layout.activity_calendar);
        AlertDialog alertDialog = builder.show();
        CalendarView calendarView = alertDialog.findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dateofmonth) {
                calendarView_onSelectedDayChange(calendarView, year, month, dateofmonth);

            }
        });
    }

    private void calendarView_onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dateofmonth) {
        try {

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            date = simpleDateFormat.parse(year + "-" + month + "-" + dateofmonth);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dateBox.setText(LocalDate.of(year, month + 1, dateofmonth).toString());
            }

        } catch (Exception e) {
            date = null;

        }
    }

    public void save(View view) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.DATE, dateBox.getText().toString());
        cv.put(DatabaseHelper.NAME, nameBox.getText().toString());
        cv.put(DatabaseHelper.BODY, bodyBox.getText().toString());
        cv.put(DatabaseHelper.TAG, tagBox.getText().toString());



        if (noteId > 0) {
            db.update(DatabaseHelper.NOTES, cv, DatabaseHelper.ID + "=" + String.valueOf(noteId), null);
        } else {
            db.insert(DatabaseHelper.NOTES, null, cv);
        }

        go();
    }

    public void delete(View view) {
        db.delete(DatabaseHelper.NOTES, "_id = ?", new String[]{String.valueOf(noteId)});
        go();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void go() {
        db.close();
        Bundle extras = getIntent().getExtras();
        Long tag = null;

        if (extras != null) {
            tag = extras.getLong("tagId");
        }

        Intent intent;

        if(tag == null || tag == 0) {
            intent = new Intent(this, MainActivity.class);
        } else {
            intent = new Intent(this, Addtag_Activity.class);
            intent.putExtra("id", tag);
        }
        startActivity(intent);
    }

}