package com.android.joeycolourless.todoshi.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.android.joeycolourless.todoshi.R;
import com.android.joeycolourless.todoshi.ToDo;
import com.android.joeycolourless.todoshi.ToDoLab;
import com.android.joeycolourless.todoshi.ToDoPagerActivity;
import com.android.joeycolourless.todoshi.datebase.ToDODbSchema;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    private Context mContext;
    private List<ToDo> list;
    private DateFormat dateFormat;
    private int mWidgetId;

    public WidgetRemoteViewsFactory(Context context, Intent intent){
        mContext = context;
        mWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        list = new ArrayList<>();
        String pattern = ((SimpleDateFormat) DateFormat.getDateInstance(DateFormat.MEDIUM,
                Locale.getDefault())).toPattern();
        dateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
    }

    @Override
    public void onDataSetChanged() {
        list.clear();

        list = ToDoLab.get(mContext).getToDos(ToDODbSchema.ToDoTable.NAME);
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        ToDo toDo = list.get(position);
        RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(), R.layout.list_item_todo);

        remoteViews.setTextViewText(R.id.list_item_todo_title_text_view, toDo.getTitle());

        remoteViews.setTextViewText(R.id.list_item_todo_date_text_view, dateFormat.format(toDo.getDate()));

        if (toDo.isNotification()){
            remoteViews.setImageViewResource(R.id.list_item_notification_status_image_view, R.drawable.ic_notifications_active_black_24dp);
        }else {
            remoteViews.setImageViewResource(R.id.list_item_notification_status_image_view, R.drawable.ic_notifications_off_black_24dp);
        }

        if (toDo.isPriority()){
            remoteViews.setImageViewResource(R.id.list_item_priority_status_image_view, R.drawable.ic_priority_high_black_24dp);
            remoteViews.setViewVisibility(R.id.list_item_priority_status_image_view, View.VISIBLE);
        }else{
            remoteViews.setViewVisibility(R.id.list_item_notification_status_image_view, View.INVISIBLE);
        }

        Intent notificationIntent = ToDoPagerActivity.newIntent(mContext, toDo.getId(), ToDODbSchema.ToDoTable.NAME);
        PendingIntent contentIntent = PendingIntent.getActivity(mContext,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);
        remoteViews.setOnClickPendingIntent(R.id.list_id, contentIntent);


        return remoteViews;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }


}
