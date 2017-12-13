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
        String title = getString(getColumnIndex(ToDoTable.Cols.TITLE));
        String details = getString(getColumnIndex(ToDoTable.Cols.DETAILS));
        String idFB = getString(getColumnIndex(ToDoTable.Cols.IDFB));
        long date = getLong(getColumnIndex(ToDoTable.Cols.DATE));
        long notificationDate = getLong(getColumnIndex(ToDoTable.Cols.NOTIFICATION_DATE));
        int isPriority = getInt(getColumnIndex(ToDoTable.Cols.PRIORITY));
        int isFinish = getInt(getColumnIndex(ToDoTable.Cols.FINISH));
        int position = getInt(getColumnIndex(ToDoTable.Cols.POSITION));
        int isNotification = getInt(getColumnIndex(ToDoTable.Cols.NOTIFICATION));
        int isSync = getInt(getColumnIndex(ToDoTable.Cols.SYNC));
        int isSuccess = getInt(getColumnIndex(ToDoCompletedTable.Cols.SUCCESS));
        int isShowDateTextView = getInt(getColumnIndex(ToDoTable.Cols.SHOW_DATE_TEXT_VIEW));



        ToDo toDo = new ToDo(UUID.fromString(uuidString));
        toDo.setTitle(title);
        toDo.setDetails(details);
        toDo.setDate(new Date(date));
        toDo.setPriority(isPriority != 0);
        toDo.setFinish(isFinish != 0);
        toDo.setPosition(position);
        toDo.setNotificationDate(new Date(notificationDate));
        toDo.setNotification(isNotification != 0);
        toDo.setSync(isSync);
        toDo.setIdFirebase(idFB);
        toDo.setSuccess(isSuccess != 0);
        toDo.setShowDateTextView(isShowDateTextView != 0);


        return toDo;
    }

    public ToDo getCompletedTodo(){
        String uuidString = getString(getColumnIndex(ToDoCompletedTable.Cols.UUID));
        String title = getString(getColumnIndex(ToDoCompletedTable.Cols.TITLE));
        String details = getString(getColumnIndex(ToDoCompletedTable.Cols.DETAILS));
        String idFB = getString(getColumnIndex(ToDoTable.Cols.IDFB));
        long date = getLong(getColumnIndex(ToDoCompletedTable.Cols.DATE));
        String comments = getString(getColumnIndex(ToDoCompletedTable.Cols.COMMENTS));
        int isFinish = getInt(getColumnIndex(ToDoCompletedTable.Cols.FINISH));
        int isSuccess = getInt(getColumnIndex(ToDoCompletedTable.Cols.SUCCESS));
        int isSync = getInt(getColumnIndex(ToDoTable.Cols.SYNC));

        ToDo toDo = new ToDo(UUID.fromString(uuidString));
        toDo.setTitle(title);
        toDo.setDetails(details);
        toDo.setDate(new Date(date));
        toDo.setComments(comments);
        toDo.setFinish(isFinish != 0);
        toDo.setSuccess(isSuccess != 0);
        toDo.setSync(isSync);
        toDo.setIdFirebase(idFB);


        return toDo;
    }

    public ToDo getDeletedToDo(){
        String uuidString = getString(getColumnIndex(ToDoCompletedTable.Cols.UUID));
        String idFB = getString(getColumnIndex(ToDoTable.Cols.IDFB));
        String user = getString(getColumnIndex(ToDoDeletedTable.Cols.USER));

        ToDo toDo = new ToDo(UUID.fromString(uuidString));
        toDo.setIdFirebase(idFB);
        toDo.setUser(user);
        return toDo;
    }
}
