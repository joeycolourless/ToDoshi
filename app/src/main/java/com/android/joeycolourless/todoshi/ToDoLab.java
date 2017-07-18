package com.android.joeycolourless.todoshi;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.android.joeycolourless.todoshi.datebase.ToDoBaseHelper;
import com.android.joeycolourless.todoshi.datebase.ToDoCursorWrapper;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

import static com.android.joeycolourless.todoshi.datebase.ToDODbSchema.ToDoCompletedTable;
import static com.android.joeycolourless.todoshi.datebase.ToDODbSchema.ToDoTable;

/**
 * Created by admin on 13.03.2017.
 */

public class ToDoLab {
    private static ToDoLab sToDoLab;


    private Context mContext;
    private SQLiteDatabase mDateBase;


    public static ToDoLab get(Context context){
        if (sToDoLab == null){
            sToDoLab = new ToDoLab(context);
        }
        return sToDoLab;

    }
    private ToDoLab(Context context){
        mContext = context.getApplicationContext();
        mDateBase = new ToDoBaseHelper(mContext)
                .getWritableDatabase();

    }

    public void addToDo(ToDo toDo, String tableName){
        ContentValues values;
        if (tableName.equals(ToDoTable.NAME)) {
           values  = getContentValues(toDo);
        }else {
            values = getContentValuesForCompletedToDo(toDo);
        }
        mDateBase.insert(tableName, null, values);
    }

    private ContentValues getContentValuesForCompletedToDo(ToDo toDo) {
        ContentValues values = new ContentValues();
        values.put(ToDoCompletedTable.Cols.UUID, toDo.getId().toString());
        values.put(ToDoCompletedTable.Cols.TITLE, toDo.getTitle());
        values.put(ToDoCompletedTable.Cols.DETAILS, toDo.getDetails());
        values.put(ToDoCompletedTable.Cols.DATE, toDo.getDate().getTime());
        values.put(ToDoCompletedTable.Cols.COMMENTS, toDo.getComments());
        values.put(ToDoTable.Cols.FINISH, toDo.isFinish() ? 1 : 0);


        return values;
    }

    public List<ToDo> getToDos(String tableName){
        List<ToDo> toDos = new ArrayList<>();

        ToDoCursorWrapper cursorWrapper = queryToDos(tableName,null, null);

        try{
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()){
                if (tableName.equals(ToDoTable.NAME)) {
                    toDos.add(cursorWrapper.getToDo());
                }else {
                    toDos.add(cursorWrapper.getCompletedTodo());
                }
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }
        Collections.sort(toDos, new Comparator<ToDo>() {
            @Override
            public int compare(ToDo o1, ToDo o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });

        return toDos;
    }

    public void deleteToDo(ToDo toDo, String tableName, String UUID){
        mDateBase.delete(tableName, UUID + " = ?" , new String[]{toDo.getId().toString()});
    }

    public ToDo getTodo(UUID id, String tableName, String uuid){
        ToDoCursorWrapper cursorWrapper = queryToDos(tableName, uuid + " = ?",
                new String[]{ id.toString()}
        );
        try {
            if (cursorWrapper.getCount() == 0){
                return null;
            }

            cursorWrapper.moveToFirst();
            if (tableName.equals(ToDoTable.NAME)){
                return cursorWrapper.getToDo();
            }else {
                return cursorWrapper.getCompletedTodo();
            }

        }finally {
            cursorWrapper.close();
        }
    }

    public File getPhotoFile(ToDo toDo){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null){
            return null;
        }
        return new File(externalFilesDir, toDo.getPhotoFilename());
    }

    public void updateToDo(ToDo toDo, String tableName, String uuid){
        String uuidString = toDo.getId().toString();
        ContentValues values;
        if (tableName.equals(ToDoTable.NAME)){
            values = getContentValues(toDo);
        }else {
            values = getContentValuesForCompletedToDo(toDo);
        }


        mDateBase.update(tableName, values, uuid + " = ?", new String[]{ uuidString});
    }

    private static ContentValues getContentValues(ToDo toDo){
        ContentValues values = new ContentValues();
        values.put(ToDoTable.Cols.UUID, toDo.getId().toString());
        values.put(ToDoTable.Cols.TITLE, toDo.getTitle());
        values.put(ToDoTable.Cols.DETAILS, toDo.getDetails());
        values.put(ToDoTable.Cols.DATE, toDo.getDate().getTime());
        values.put(ToDoTable.Cols.PRIORITY, toDo.isPriority() ? 1 : 0);
        values.put(ToDoTable.Cols.FINISH, toDo.isFinish() ? 1 : 0);
        values.put(ToDoTable.Cols.POSITION, toDo.getPosition());
        values.put(ToDoTable.Cols.NOTIFICATION, toDo.isNotification() ? 1 : 0);
        if (toDo.getNotificationDate() != null){
            values.put(ToDoTable.Cols.NOTIFICATION_DATE, toDo.getNotificationDate().getTime());
        }

        return values;
    }

   private ToDoCursorWrapper queryToDos(String tableName, String whereClause, String[] whereArgs){
        Cursor cursor = mDateBase.query(
                tableName,
                null, // Columns - null вибирає всі стовпці
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new ToDoCursorWrapper(cursor);
    }
}