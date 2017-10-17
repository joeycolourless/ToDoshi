package com.android.joeycolourless.todoshi;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

import com.android.joeycolourless.todoshi.datebase.ToDoBaseHelper;
import com.android.joeycolourless.todoshi.datebase.ToDoCursorWrapper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Executor;

import static com.android.joeycolourless.todoshi.datebase.ToDODbSchema.ToDoCompletedTable;
import static com.android.joeycolourless.todoshi.datebase.ToDODbSchema.ToDoTable;



public class ToDoLab {
    private static ToDoLab sToDoLab;


    private Context mContext;
    private SQLiteDatabase mDateBase;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseAuth.AuthStateListener mAuthListener;

<<<<<<< HEAD
<<<<<<< HEAD
    private DatabaseReference mFirebaseDatabaseRef;
    private List<ToDo> toDos = new ArrayList<>();

=======
    private DatabaseReference mFirebaseDatebaseRef;
>>>>>>> parent of a38eee0... transition to Firebase, transition to firebase RecyclerViewAdapter

    List<ToDo> list = new ArrayList<>();

=======
>>>>>>> parent of 75f766c... transition to Firebase, added functions for to add new Tasks to RealTimeDatabase and update them

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

<<<<<<< HEAD
    public void firebaseAddToDO(ToDo toDo, String tableName){
        mFirebaseDatebaseRef = FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid());
        mFirebaseDatebaseRef.child(tableName).child(toDo.getIdFireBase()).setValue(toDo);
    }

=======
>>>>>>> parent of 75f766c... transition to Firebase, added functions for to add new Tasks to RealTimeDatabase and update them
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

    public void deleteAllToDos(String tableName){
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

<<<<<<< HEAD
<<<<<<< HEAD
    public ToDo getToDoFireBase(String tableName, UUID uuid){
        final ToDo[] toDo1 = new ToDo[1];
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid()).child(tableName).child(String.valueOf(uuid));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                toDo1[0] = dataSnapshot.getValue(ToDo.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
=======
    public ToDo getToDoFireBase(ToDo toDo, String tableName){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid()).child(tableName).child(toDo.getIdFireBase());
>>>>>>> parent of a38eee0... transition to Firebase, transition to firebase RecyclerViewAdapter

        return toDo;
    }

=======
>>>>>>> parent of 75f766c... transition to Firebase, added functions for to add new Tasks to RealTimeDatabase and update them
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
}
