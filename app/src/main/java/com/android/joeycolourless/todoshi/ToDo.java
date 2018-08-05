package com.android.joeycolourless.todoshi;

import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

/**
 * Created by admin on 12.03.2017.
 */

public class ToDo implements Cloneable{

    private UUID mId;
    private String mTitle;
    private String mDetails;
    private Date mDate;
    private Date mNotificationDate;
    private Date mDateChange;
    private String mIdFirebase;
    private String mUser;
    private String mTable;
    private boolean mPriority;
    private boolean mFinish;
    private boolean mNotification;
    private boolean mSuccess;
    private boolean mShowDateTextView;
    private int mSync;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public boolean isNotification() {
        return mNotification;
    }

    public void setNotification(boolean notification) {
        mNotification = notification;
    }

    private int mPosition;
    private String mComments;

    public ToDo(){
       this(UUID.randomUUID());
    }

    public ToDo(UUID id){
        mId = id;
        mTitle = "";
        mDetails = "";
        mIdFirebase = FirebaseDatabase.getInstance().getReference(mAuth.getCurrentUser().getUid()).child(ToDODbSchema.ToDoTable.NAME).push().getKey();
        mDate = new Date();
        mDateChange = new Date();
        mNotificationDate = new Date(0);
        mFinish = false;
        mNotification = false;
        mSuccess = false;
        mSync = ToDoLab.ADD_SYNC;
        mShowDateTextView = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToDo toDo = (ToDo) o;
        return mPriority == toDo.mPriority &&
                mFinish == toDo.mFinish &&
                mNotification == toDo.mNotification &&
                mSuccess == toDo.mSuccess &&
                mShowDateTextView == toDo.mShowDateTextView &&
                mSync == toDo.mSync &&
                mPosition == toDo.mPosition &&
                Objects.equals(mId, toDo.mId) &&
                Objects.equals(mTitle, toDo.mTitle) &&
                Objects.equals(mDetails, toDo.mDetails) &&
                Objects.equals(mDate, toDo.mDate) &&
                Objects.equals(mNotificationDate, toDo.mNotificationDate) &&
                Objects.equals(mDateChange, toDo.mDateChange) &&
                Objects.equals(mIdFirebase, toDo.mIdFirebase) &&
                Objects.equals(mUser, toDo.mUser) &&
                Objects.equals(mTable, toDo.mTable) &&
                Objects.equals(mAuth, toDo.mAuth) &&
                Objects.equals(mComments, toDo.mComments);
    }

    @Override
    public int hashCode() {

        return Objects.hash(mId, mTitle, mDetails, mDate, mNotificationDate, mDateChange, mIdFirebase, mUser, mTable, mPriority, mFinish, mNotification, mSuccess, mShowDateTextView, mSync, mAuth, mPosition, mComments);
    }

    @Override
    public ToDo clone() throws CloneNotSupportedException {
        return (ToDo) super.clone();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isPriority() {
        return mPriority;
    }

    public void setPriority(boolean priority) {
        mPriority = priority;
    }

    public int getPosition() {
        return mPosition;
    }

    public void setPosition(int position) {
        mPosition = position;
    }

    public String getComments() {
        return mComments;
    }

    public void setComments(String comments) {
        mComments = comments;
    }

    public Date getNotificationDate() {
        return mNotificationDate;
    }

    public void setNotificationDate(Date notificationDate) {
        mNotificationDate = notificationDate;
    }

    public boolean isFinish() {
        return mFinish;
    }

    public void setFinish(boolean finish) {
        mFinish = finish;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public void setSuccess(boolean mSuccess) {
        this.mSuccess = mSuccess;
    }

    public int getSync() {
        return mSync;
    }

    public void setSync(int mSync) {
        this.mSync = mSync;
    }

    public String getIdFirebase() {
        return mIdFirebase;
    }

    public void setIdFirebase(String mIdFirebase) {
        this.mIdFirebase = mIdFirebase;
    }

    public String getUser() {
        return mUser;
    }

    public void setUser(String mUser) {
        this.mUser = mUser;
    }

    public String getTable() {
        return mTable;
    }

    public void setTable(String mTable) {
        this.mTable = mTable;
    }

    public boolean isShowDateTextView() {
        return mShowDateTextView;
    }

    public void setShowDateTextView(boolean mShowDateTextView) {
        this.mShowDateTextView = mShowDateTextView;
    }

    public Date getDateChange() {
        return mDateChange;
    }

    public void setDateChange(Date dateChange) {
        mDateChange = dateChange;
    }
}
