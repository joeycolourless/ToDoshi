package com.android.joeycolourless.todoshi.datebase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.joeycolourless.todoshi.datebase.ToDODbSchema.ToDoTable;

import static com.android.joeycolourless.todoshi.datebase.ToDODbSchema.*;

/**
 * Created by admin on 19.03.2017.
 */

public class ToDoBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "toDoBase.db";

    public ToDoBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + ToDoTable.NAME + "(" +
            " _id integer primary key autoincrement, " +
            ToDoTable.Cols.UUID + ", " +
            ToDoTable.Cols.TITLE + ", " +
            ToDoTable.Cols.DETAILS + ", " +
            ToDoTable.Cols.DATE + ", " +
            ToDoTable.Cols.PRIORITY +  ", " +
            ToDoTable.Cols.POSITION + ", " +
            ToDoTable.Cols.FINISH + ", " +
            ToDoTable.Cols.NOTIFICATION_DATE + ", "+
            ToDoTable.Cols.NOTIFICATION  +  ")"
        );
        db.execSQL("create table " + ToDoCompletedTable.NAME + "(" +
                " _id integer primary key autoincrement, " +
                ToDoCompletedTable.Cols.UUID + ", " +
                ToDoCompletedTable.Cols.TITLE + ", " +
                ToDoCompletedTable.Cols.DETAILS + ", " +
                ToDoCompletedTable.Cols.DATE + ", " +
                ToDoCompletedTable.Cols.FINISH + ", " +
                ToDoCompletedTable.Cols.COMMENTS +
                   ")"
        );


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
