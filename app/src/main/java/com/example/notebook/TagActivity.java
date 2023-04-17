package com.example.notebook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;


public class TagActivity extends AppCompatActivity {
    ListView tagList;
    //TextView count;
    DatabaseHelper dbHelpert;
    SQLiteDatabase dbt;
    Cursor tagCursor;
    SimpleCursorAdapter tagAdapter;

    ListView lvMain;
    String[] day_of_weeks = {"Понедельник", "Вторник", "Среда", "Четверг", "Пятница" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        tagList=findViewById(R.id.listt);


        tagList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), Addtag_Activity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });

        dbHelpert=new DatabaseHelper(getApplicationContext());
    }

    public void notesPages(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void addTag(View view){
        Intent intent = new Intent(this, Addtag_Activity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        dbt = dbHelpert.getReadableDatabase();
        tagCursor = dbt.rawQuery("SELECT * FROM " + DatabaseHelper.TAGS, null);
        String[] dbArrayt = new String[]{DatabaseHelper.BODYTAG};


        tagAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, tagCursor, dbArrayt,
                new int[]{android.R.id.text1},0);

        tagList.setAdapter(tagAdapter);
     }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbt.close();
        tagCursor.close();
    }


}