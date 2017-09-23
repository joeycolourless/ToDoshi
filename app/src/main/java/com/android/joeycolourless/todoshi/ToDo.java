package com.android.joeycolourless.todoshi;

import java.util.Date;
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
    private boolean mPriority;
    private boolean mFinish;
    private boolean mNotification;
    private boolean mSuccess;

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
        mDate = new Date();
        mNotificationDate = null;
        mFinish = false;
        mNotification = false;
        mSuccess = false;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ToDo toDo = (ToDo) o;

        if (mPriority != toDo.mPriority) return false;
        if (mFinish != toDo.mFinish) return false;
        if (mNotification != toDo.mNotification) return false;
        if (mSuccess != toDo.mSuccess) return false;
        if (mPosition != toDo.mPosition) return false;
        if (mId != null ? !mId.equals(toDo.mId) : toDo.mId != null) return false;
        if (mTitle != null ? !mTitle.equals(toDo.mTitle) : toDo.mTitle != null) return false;
        if (mDetails != null ? !mDetails.equals(toDo.mDetails) : toDo.mDetails != null)
            return false;
        if (mDate != null ? !mDate.equals(toDo.mDate) : toDo.mDate != null) return false;
        if (mNotificationDate != null ? !mNotificationDate.equals(toDo.mNotificationDate) : toDo.mNotificationDate != null)
            return false;
        return mComments != null ? mComments.equals(toDo.mComments) : toDo.mComments == null;

    }

    @Override
    public int hashCode() {
        int result = mId != null ? mId.hashCode() : 0;
        result = 31 * result + (mTitle != null ? mTitle.hashCode() : 0);
        result = 31 * result + (mDetails != null ? mDetails.hashCode() : 0);
        result = 31 * result + (mDate != null ? mDate.hashCode() : 0);
        result = 31 * result + (mNotificationDate != null ? mNotificationDate.hashCode() : 0);
        result = 31 * result + (mPriority ? 1 : 0);
        result = 31 * result + (mFinish ? 1 : 0);
        result = 31 * result + (mNotification ? 1 : 0);
        result = 31 * result + (mSuccess ? 1 : 0);
        result = 31 * result + mPosition;
        result = 31 * result + (mComments != null ? mComments.hashCode() : 0);
        return result;
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

    public String getPhotoFilename(){
        return "IMG_" + getId().toString()+ ".jpg";
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

    public boolean ismSuccess() {
        return mSuccess;
    }

    public void setmSuccess(boolean mSuccess) {
        this.mSuccess = mSuccess;
    }
}
