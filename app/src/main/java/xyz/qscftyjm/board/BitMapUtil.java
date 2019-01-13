package xyz.qscftyjm.board;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

public class BitMapUtil {
    static Resources res;

    public static Bitmap getDefaultPortrait(Context context){

        Bitmap bmp= BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_huaji);
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

    public static Bitmap getBitmap(Context context,byte[] bytes){
        Bitmap bitmap;
        if(bytes.length<32){
            bitmap = getDefaultPortrait(context);
        } else {
            bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }


        return bitmap;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
