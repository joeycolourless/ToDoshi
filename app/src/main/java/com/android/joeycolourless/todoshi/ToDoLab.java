package com.android.joeycolourless.todoshi;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Toast;

import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;
import com.android.joeycolourless.todoshi.datebase.ToDoBaseHelper;
import com.android.joeycolourless.todoshi.datebase.ToDoCursorWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.android.joeycolourless.todoshi.datebase.ToDODbSchema.ToDoCompletedTable;
import static com.android.joeycolourless.todoshi.datebase.ToDODbSchema.ToDoTable;



public class ToDoLab {
    private static ToDoLab sToDoLab;

    public static final int NOTHING = 0;
    public static final int ADD_SYNC = 1;
    public static final int DELETE = 2;
    public static final int DONE = 3;


    private Context mContext;
    private SQLiteDatabase mDateBase;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private DatabaseReference mFirebaseDateBaseRef;

    private ToDo mToDoPrevious;
    private boolean isOnTheWeek = false;


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
        switch (tableName){
            case ToDoTable.NAME:
                values  = getContentValues(toDo);
                break;
            case ToDoCompletedTable.NAME:
                values = getContentValuesForCompletedToDo(toDo);
                break;
            case ToDODbSchema.ToDoDeletedTable.NAME:
                values = getContentValueForDeletedToDo(toDo);
                break;
            default:
                values = new ContentValues();
                break;
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
        values.put(ToDoTable.Cols.IDFB, toDo.getIdFirebase());
        values.put(ToDoTable.Cols.SUCCESS, toDo.isSuccess() ? 1 : 0);


        return values;
    }

    private ContentValues getContentValueForDeletedToDo(ToDo toDo){
        ContentValues values = new ContentValues();
        values.put(ToDoTable.Cols.UUID, toDo.getId().toString());
        values.put(ToDoTable.Cols.IDFB, toDo.getIdFirebase());
        values.put(ToDODbSchema.ToDoDeletedTable.Cols.USER, toDo.getUser());
        values.put(ToDODbSchema.ToDoDeletedTable.Cols.TABLE, toDo.getTable());

        return values;
    }

    public List<ToDo> getToDos(String tableName){
        List<ToDo> toDos = new ArrayList<>();

        ToDoCursorWrapper cursorWrapper = queryToDos(tableName,null, null);

        try{
            cursorWrapper.moveToFirst();
            while (!cursorWrapper.isAfterLast()){
                switch (tableName){
                    case ToDoTable.NAME:
                        toDos.add(cursorWrapper.getToDo());
                        break;
                    case ToDoCompletedTable.NAME:
                        toDos.add(cursorWrapper.getCompletedTodo());
                        break;
                    case ToDODbSchema.ToDoDeletedTable.NAME:
                        toDos.add(cursorWrapper.getDeletedToDo());
                        break;
                }
                cursorWrapper.moveToNext();
            }
        }finally {
            cursorWrapper.close();
        }
        Collections.sort(toDos, new Comparator<ToDo>() {
            @Override
            public int compare(ToDo o1, ToDo o2) {
                return o1.getNotificationDate().compareTo(o2.getNotificationDate());
            }
        });

        return toDos;
    }

    public void deleteToDo(ToDo toDo, String tableName, String UUID){
        toDo.setSync(DELETE);
        updateToDo(toDo, tableName, UUID);
        firebaseSyncToDO(toDo, tableName, DELETE, mContext);

        mDateBase.delete(tableName, UUID + " = ?" , new String[]{toDo.getId().toString()});

    }

    public void deleteAllToDos(String tableName, boolean fromFirebaseToo){
        if (fromFirebaseToo){
            List<ToDo> list = getToDos(tableName);
            for (ToDo toDo : list){
                firebaseSyncToDO(toDo, tableName, DELETE, mContext);
            }
        }

        mDateBase.delete(tableName, null, null);
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






    public List<ToDo> getToDosSearch(String searchTerm, String tableName){
        List<ToDo> toDos = new ArrayList<>();
        String[] title = new String[2];
        title[0] = ToDoTable.Cols.DETAILS;
        title[1] = ToDoTable.Cols.TITLE;
        ToDoCursorWrapper cursor = queryToDos(tableName,  "(" + ToDoTable.Cols.TITLE + " LIKE '%" + searchTerm+"%')" +
        "OR (" + ToDoTable.Cols.DETAILS + " LIKE '%" + searchTerm + "%')",null);
        try{
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                if (tableName.equals(ToDoTable.NAME)) {
                    toDos.add(cursor.getToDo());
                }else {
                    toDos.add(cursor.getCompletedTodo());
                }
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        Collections.sort(toDos, new Comparator<ToDo>() {
            @Override
            public int compare(ToDo o1, ToDo o2) {
                return o1.getDate().compareTo(o2.getDate());
            }
        });
        if (toDos.size() == 0){
            return null;
        }
        return toDos;

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

    public void updateToDo(ToDo toDo, String tableName, String uuid, int sync){
        String uuidString = toDo.getId().toString();
        if (toDo.getSync() != DELETE){
            firebaseSyncToDO(toDo, tableName, sync, this.mContext);
        }

        ContentValues values;
        if (tableName.equals(ToDoTable.NAME)){
            values = getContentValues(toDo);
        }else {
            values = getContentValuesForCompletedToDo(toDo);
        }


        mDateBase.update(tableName, values, uuid + " = ?", new String[]{ uuidString});
    }

    public void doneToDo(ToDo toDo){
        addToDo(toDo, ToDODbSchema.ToDoCompletedTable.NAME);
        updateToDo(toDo, ToDODbSchema.ToDoCompletedTable.NAME, ToDODbSchema.ToDoCompletedTable.Cols.UUID, ToDoLab.ADD_SYNC);
        deleteToDo(toDo, ToDODbSchema.ToDoTable.NAME, ToDODbSchema.ToDoTable.Cols.UUID);
    }

    public void firebaseSyncToDO(ToDo toDo, String tableName, int firebaseOption, Context context){
        if (StartActivity.isOnline(context)){
            mFirebaseDateBaseRef = FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid()).child(tableName);
            switch (firebaseOption){
                case NOTHING:
                    break;
                case ADD_SYNC:
                    toDo.setSync(NOTHING);
                    updateToDo(toDo, tableName, ToDoTable.Cols.UUID);
                    mFirebaseDateBaseRef.child(toDo.getIdFirebase()).setValue(toDo);
                    break;
                case DELETE:
                    mFirebaseDateBaseRef.child(toDo.getIdFirebase()).removeValue();
                    break;
                case DONE:
                    break;
                default:
                    break;
            }
        }else
            switch (firebaseOption){
                case DELETE:
                    toDo.setUser(mAuth.getUid());
                    toDo.setTable(tableName);
                    addToDo(toDo, ToDODbSchema.ToDoDeletedTable.NAME);

            }

    }
    //Method for delete from firebase if task was deleted from SQLite without internet
    public void firebaseDeleteToDo(ToDo toDo){
        mFirebaseDateBaseRef = FirebaseDatabase.getInstance().getReference(toDo.getUser()).child(toDo.getTable());
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
        values.put(ToDoTable.Cols.IDFB, toDo.getIdFirebase());
        values.put(ToDoTable.Cols.SHOW_DATE_TEXT_VIEW, toDo.isShowDateTextView());

        return values;
    }

   private ToDoCursorWrapper queryToDos(String tableName, String whereClause, String[] whereArgs){
        Cursor cursor = mDateBase.query(
                tableName,
                null, // Columns - null takes all columns
                whereClause,
                whereArgs,
                null, // groupBy
                null, // having
                null // orderBy
        );
        return new ToDoCursorWrapper(cursor);
    }

    public FirebaseAuth signUpUser(String email, String password, Activity activity){

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isComplete()){
                    Toast.makeText(mContext, R.string.auth_success,
                            Toast.LENGTH_SHORT).show();
                }else
                    Toast.makeText(mContext, R.string.auth_failed, Toast.LENGTH_SHORT).show();
            }
        });
        return mAuth;
    }

    public FirebaseAuth signInUser(String email, String password, Activity activity){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {


                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {

                            Toast.makeText(mContext, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(mContext, R.string.auth_success,
                                    Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
        return mAuth;
    }
    //Method compares two NotificationDates in two tasks and return true if the same and false if not
    private boolean dateCompare(Date toDoDate, Date toDoPreviousDate, boolean compareWithTodayDate){
        Calendar toDoCalendar = Calendar.getInstance();
        toDoCalendar.setTime(toDoDate);

        Calendar toDoPreviousCalendar = Calendar.getInstance();
        if (compareWithTodayDate){
            toDoPreviousCalendar.setTime(new Date());
        }else toDoPreviousCalendar.setTime(toDoPreviousDate);

        int toDoYear = toDoCalendar.get(Calendar.YEAR);
        int toDoPreviousYear = toDoPreviousCalendar.get(Calendar.YEAR);

        int toDoDayOfYear = toDoCalendar.get(Calendar.DAY_OF_YEAR);
        int toDoPreviousDayOfYear = toDoPreviousCalendar.get(Calendar.DAY_OF_YEAR);

        if (toDoYear == toDoPreviousYear){
            if (toDoDayOfYear == toDoPreviousDayOfYear){
                return true;
            }else return false;
        }
        return false;
    }
    //Method checks is notificationDate longer then seven days from today
    public boolean isDateMoreThenSevenDays(ToDo toDo){
        Calendar toDoCalendar = Calendar.getInstance();
        toDoCalendar.setTime(toDo.getNotificationDate());
        int toDoDayOfYear = toDoCalendar.get(Calendar.DAY_OF_YEAR);

        Calendar currentDateCalendar = Calendar.getInstance();
        currentDateCalendar.setTime(new Date());
        int currentDateDayOfYear = currentDateCalendar.get(Calendar.DAY_OF_YEAR);
        int result = toDoDayOfYear - currentDateDayOfYear;
        return result > 7;
    }
    //Method scans task and compare with previous task and changing or not variable mSortDateTextView
    private void isToDoTabWithDateTextView(ToDo toDo, int position){
        if (position == 0){
            if (dateCompare(toDo.getNotificationDate(), null, true)){
                //mSortDateTextView.setText(R.string.today);
                textViewMarksChangerForToDo(toDo, true);
                mToDoPrevious = toDo;
            }else {
                if (toDo.getNotificationDate().getTime() == 0){
                    //mSortDateTextView.setText(R.string.daily);
                    textViewMarksChangerForToDo(toDo, true);
                }else //mSortDateTextView.setText(simpleDateFormat.format(toDo.getNotificationDate()));
                    textViewMarksChangerForToDo(toDo, true);
                mToDoPrevious = toDo;
            }
        }else {
            if (dateCompare(toDo.getNotificationDate(), mToDoPrevious.getNotificationDate(), false)){
                textViewMarksChangerForToDo(toDo, false);
                mToDoPrevious = toDo;
            }else {
                if (dateCompare(toDo.getNotificationDate(), null, true)){
                    //mSortDateTextView.setText(R.string.today);
                    textViewMarksChangerForToDo(toDo, true);
                }else{
                    if (isDateMoreThenSevenDays(toDo)){
                        if (!isOnTheWeek){
                            //mSortDateTextView.setVisibility(View.VISIBLE);
                            //mSortDateTextView.setText(R.string.on_the_next_week);
                            textViewMarksChangerForToDo(toDo, true);
                            isOnTheWeek = true;
                        }

                    }else //mSortDateTextView.setText(simpleDateFormat.format(toDo.getNotificationDate()));
                        textViewMarksChangerForToDo(toDo, true);
                }
                mToDoPrevious = toDo;
            }
        }
    }
    //Method checks need or not to change variable in task
    private void textViewMarksChangerForToDo(ToDo toDo, boolean showDateTextView) {
        if (!toDo.isShowDateTextView()) {
            toDo.setShowDateTextView(showDateTextView);
            updateToDo(toDo, ToDoTable.NAME, ToDoTable.Cols.UUID, ADD_SYNC);
        }else {
            if (!showDateTextView){
                toDo.setShowDateTextView(showDateTextView);
                updateToDo(toDo, ToDoTable.NAME, ToDoTable.Cols.UUID, ADD_SYNC);
            }
        }

    }
    //Method reads all tasks and changes variable ToDo.mShowDateTextView if it need
    public void setTextViewMarksForToDos(){
        List<ToDo> toDos = getToDos(ToDoTable.NAME);
        if (toDos.size() == 0){
            return;
        }
        int countPosition = 0;
        mToDoPrevious = new ToDo();
        for (ToDo toDo: toDos){
            isToDoTabWithDateTextView(toDo, countPosition);
            countPosition++;
        }

    }
}
