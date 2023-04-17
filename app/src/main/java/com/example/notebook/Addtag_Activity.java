package com.example.notebook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.*;

public class Addtag_Activity extends AppCompatActivity {
    EditText tagnameBox;

    String tagName;

    Button delBtn;

    DatabaseHelper dbHelpert;
    SQLiteDatabase dbt;
    Cursor tagCursort;


    Cursor noteCursor;
    SimpleCursorAdapter noteAdapter;

    long tagId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addtag);

        tagnameBox = findViewById(R.id.editTexttag);

        delBtn = findViewById(R.id.button2deletetag);

        dbHelpert = new DatabaseHelper(this);
        dbt = dbHelpert.getWritableDatabase();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            tagId = extras.getLong("id");
        }

        if (tagId > 0) {
            tagCursort = dbt.rawQuery("SELECT * FROM " + DatabaseHelper.TAGS + " WHERE " + DatabaseHelper.IDTAG + "=? ",
                    new String[]{String.valueOf(tagId)});
            tagCursort.moveToFirst();

            tagName = tagCursort.getString(1);

            tagCursort.close();
            tagnameBox.setText(tagName);

            noteCursor = dbt.rawQuery("SELECT notes._id, notes.date, notes.name FROM " + DatabaseHelper.NOTES +
                    " WHERE " + DatabaseHelper.TAG + " = ?",  new String[]{tagName});
            String[] dbArray = new String[]{DatabaseHelper.DATE, DatabaseHelper.NAME};

            ListView listView = findViewById(R.id.list2notes);

            noteAdapter = new SimpleCursorAdapter(
                    this,
                    android.R.layout.two_line_list_item,
                    noteCursor,
                    dbArray,
                    new int[]{android.R.id.text1, android.R.id.text2},
                    0
            );
            listView.setAdapter(noteAdapter);

            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
                    intent.putExtra("id", id);
                    intent.putExtra("tagId", tagId);
                    startActivity(intent);
                }
            });

        } else {
            delBtn.setVisibility(View.GONE);
        }


    }

    public void savetag(View view) {
        ContentValues cvTag = new ContentValues();
        cvTag.put(DatabaseHelper.BODYTAG, tagnameBox.getText().toString());
        ContentValues cvNote = new ContentValues();
        cvNote.put(DatabaseHelper.TAG, tagnameBox.getText().toString());


        if (tagId > 0) {
            dbt.update(DatabaseHelper.TAGS, cvTag, DatabaseHelper.IDTAG + "= ?", new String[]{String.valueOf(tagId)});
            dbt.update(DatabaseHelper.NOTES, cvNote, DatabaseHelper.TAG +" = ?", new String[]{tagName});
        } else {
            dbt.insert(DatabaseHelper.TAGS, null, cvTag);
        }
        go();
    }

    public void deletetag(View view) {
        dbt.delete(DatabaseHelper.TAGS, "_id = ?", new String[]{String.valueOf(tagId)});
        go();
    }

    private void go() {
        dbt.close();
        Intent intent = new Intent(this, TagActivity.class);
        startActivity(intent);
    }
}