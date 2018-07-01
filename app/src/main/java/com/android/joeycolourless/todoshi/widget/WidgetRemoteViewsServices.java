package com.android.joeycolourless.todoshi.widget;

import android.content.Intent;
import android.widget.RemoteViewsService;


public class WidgetRemoteViewsServices extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}
