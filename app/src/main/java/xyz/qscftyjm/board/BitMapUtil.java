package xyz.qscftyjm.board;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class BitMapUtil {
    static Resources res;

    public static Bitmap getDefaultPortrait(Context context){

        Bitmap bmp= BitmapFactory.decodeResource(context.getResources(), R.drawable.user);
        return bmp;
    }

    public static Bitmap[] getDefaultPics(Context context){
        int size=3;
        Bitmap[] bitmaps=new Bitmap[size];
        bitmaps[0]=BitmapFactory.decodeResource(context.getResources(), R.drawable.chat);
        bitmaps[1]=BitmapFactory.decodeResource(context.getResources(), R.drawable.user);
        bitmaps[2]=BitmapFactory.decodeResource(context.getResources(), R.drawable.write);
        return bitmaps;
    }

}
