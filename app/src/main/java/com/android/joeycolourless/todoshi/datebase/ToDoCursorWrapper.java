package com.android.joeycolourless.todoshi.datebase;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.android.joeycolourless.todoshi.ToDo;

import java.util.Date;
import java.util.UUID;

import static com.android.joeycolourless.todoshi.datebase.ToDODbSchema.*;


public class ToDoCursorWrapper extends CursorWrapper {

    public ToDoCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public ToDo getToDo(){
        String uuidString = getString(getColumnIndex(ToDoTable.Cols.UUID));
        String idFB = getString(getColumnIndex(ToDoTable.Cols.IDFB));
        String title = getString(getColumnIndex(ToDoTable.Cols.TITLE));
        String details = getString(getColumnIndex(ToDoTable.Cols.DETAILS));
        long date = getLong(getColumnIndex(ToDoTable.Cols.DATE));
        long notificationDate = getLong(getColumnIndex(ToDoTable.Cols.NOTIFICATION_DATE));
        int isPriority = getInt(getColumnIndex(ToDoTable.Cols.PRIORITY));
        int isFinish = getInt(getColumnIndex(ToDoTable.Cols.FINISH));
        int position = getInt(getColumnIndex(ToDoTable.Cols.POSITION));
        int isNotification = getInt(getColumnIndex(ToDoTable.Cols.NOTIFICATION));


        ToDo toDo = new ToDo(UUID.fromString(uuidString));
        toDo.setTitle(title);
        toDo.setIdFireBase(idFB);
        toDo.setDetails(details);
        toDo.setDate(new Date(date));
        toDo.setPriority(isPriority != 0);
        toDo.setFinish(isFinish != 0);
        toDo.setPosition(position);
        toDo.setNotificationDate(new Date(notificationDate));
        toDo.setNotification(isNotification != 0);

        return toDo;
    }

    public ToDo getCompletedTodo(){
        String uuidString = getString(getColumnIndex(ToDoCompletedTable.Cols.UUID));
        String idFB = getString(getColumnIndex(ToDoCompletedTable.Cols.IDFB));
        String title = getString(getColumnIndex(ToDoCompletedTable.Cols.TITLE));
        String details = getString(getColumnIndex(ToDoCompletedTable.Cols.DETAILS));
        long date = getLong(getColumnIndex(ToDoCompletedTable.Cols.DATE));
        String comments = getString(getColumnIndex(ToDoCompletedTable.Cols.COMMENTS));
        int isFinish = getInt(getColumnIndex(ToDoCompletedTable.Cols.FINISH));
        int isSuccess = getInt(getColumnIndex(ToDoCompletedTable.Cols.SUCCESS));

        ToDo toDo = new ToDo(UUID.fromString(uuidString));
        toDo.setIdFireBase(idFB);
        toDo.setTitle(title);
        toDo.setDetails(details);
        toDo.setDate(new Date(date));
        toDo.setComments(comments);
        toDo.setFinish(isFinish != 0);
        toDo.setSuccess(isSuccess != 0);


        return toDo;
    }
}
