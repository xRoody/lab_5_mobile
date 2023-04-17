package com.example.notebook;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME="database13.db";

    //
    public static final String ID="_id";
    public static final String NOTES="notes";
    public static final String NAME="name";
    public static final String DATE="date";
    public static final String BODY="body";
    public static final String TAG="tag";

    //
    public static final String IDTAG="_id";
    public static final String TAGS="tags";
    public static final String BODYTAG="bodytag";


    public DatabaseHelper(Context context){
        super(context,DB_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //CREATE TABLE - создание таблицы
        //INSERT INTO - заполнение полей в таблице  INSERT INTO users (name,age) VALUES ("Иван", 20)
        //SELECT - выборка из таблицы для вывода
        db.execSQL("CREATE TABLE notes ("+ID+" INTEGER PRIMARY KEY AUTOINCREMENT, " + DATE+ " TEXT, "+ NAME+ " TEXT, "+ BODY+ " TEXT, " +TAG+ " TEXT)");

        db.execSQL("CREATE TABLE tags ("+IDTAG+" INTEGER PRIMARY KEY AUTOINCREMENT, " + BODYTAG+" TEXT)");
//        db.execSQL("INSERT INTO " + TAGS + " (" + BODYTAG + ") VALUES ('Срочно')");
//        db.execSQL("INSERT INTO " + TAGS + " (" + BODYTAG + ") VALUES ('Важно')");
//        db.execSQL("INSERT INTO " + TAGS + " (" + BODYTAG + ") VALUES ('Не срочно')");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        /* DROP - удаление бд*/
        db.execSQL("DROP TABLE IF EXISTS "+NOTES);
        db.execSQL("DROP TABLE IF EXISTS "+TAGS);

        onCreate(db);
    }
}