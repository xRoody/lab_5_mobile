package com.example.notebook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

public class MainActivity extends AppCompatActivity {


    ListView userList;
    //TextView count;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        userList=findViewById(R.id.list);

        userList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), NotesActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        dbHelper=new DatabaseHelper(getApplicationContext());

    }
    public void tagPages(View view) {
        Intent intent = new Intent(this, TagActivity.class);
        startActivity(intent);
    }

    public void add(View view){
        Intent intent = new Intent(this, NotesActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        db=dbHelper.getReadableDatabase();
        userCursor=db.rawQuery("SELECT notes._id, notes.date, notes.name FROM "+ DatabaseHelper.NOTES, null);
        String[] dbArray = new String[]{DatabaseHelper.DATE, DatabaseHelper.NAME};

        userAdapter=new SimpleCursorAdapter(this, android.R.layout.two_line_list_item, userCursor, dbArray,
                new int[]{android.R.id.text1, android.R.id.text2},0);

        userList.setAdapter(userAdapter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
        userCursor.close();
    }
}
