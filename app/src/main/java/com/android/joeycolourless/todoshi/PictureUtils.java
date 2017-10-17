package com.android.joeycolourless.todoshi;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * Created by admin on 21.03.2017.
 */

public class PictureUtils {
    public static  Bitmap getScaleBitmap(String path, Activity activity){
        Point size = new Point();

        activity.getWindowManager().getDefaultDisplay().getSize(size);

        return getScaleBitmap(path, size.x, size.y);
    }

    public static Bitmap getScaleBitmap(String path, int destWidth, int destHeight){
        //Read size of image on the disk
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        //Scale calculation
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth){
            if (srcWidth > srcHeight){
                inSampleSize = Math.round(srcHeight / destHeight);
            }else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        //Read data and creating final image
        return BitmapFactory.decodeFile(path, options);
    }
}
