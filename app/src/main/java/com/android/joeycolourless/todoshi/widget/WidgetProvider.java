package com.android.joeycolourless.todoshi.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.android.joeycolourless.todoshi.BuildConfig;
import com.android.joeycolourless.todoshi.R;

public class WidgetProvider extends AppWidgetProvider {
    private static final String TAG = "PROVIDER";
    public static final String ACTION_ON_ITEM_CLICK = "ON_MORE_CLICK";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        for (int widgetId : appWidgetIds){
            updateWidget(context, appWidgetManager, widgetId);
        }
    }

    private void updateWidget(Context context, AppWidgetManager appWidgetManager, int widgetId ){
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
        setList(views, context, widgetId);

        final  Intent onItemClick = new Intent(context, WidgetProvider.class);
        onItemClick.setAction(ACTION_ON_ITEM_CLICK);
        onItemClick.setData(Uri.parse(onItemClick.toUri(Intent.URI_INTENT_SCHEME)));
        final PendingIntent onClickPendingIntent =
                PendingIntent.getBroadcast(context, 0, onItemClick, PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.widgetList, onClickPendingIntent);

        appWidgetManager.updateAppWidget(widgetId,views);
    }

    private void setList(RemoteViews views, Context context, int widgetId){
        Intent intent = new Intent(context, WidgetRemoteViewsServices.class);
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, widgetId);
        views.setRemoteAdapter(R.id.widgetList, intent);
    }

    public static void sendRefreshBroadcast(Context context){
        Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        intent.setComponent(new ComponentName(context, WidgetProvider.class));
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final  String action = intent.getAction();
        if (BuildConfig.DEBUG) Log.d(TAG, action);
        if (!TextUtils.isEmpty(action)){
            if (action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE)){
                AppWidgetManager manager = AppWidgetManager.getInstance(context);
                ComponentName componentName = new ComponentName(context, WidgetProvider.class);
                manager.notifyAppWidgetViewDataChanged(manager.getAppWidgetIds(componentName), R.id.widgetList);
            }
        }
        super.onReceive(context, intent);
    }
}
