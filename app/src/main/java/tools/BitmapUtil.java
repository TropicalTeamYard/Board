package tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

import xyz.qscftyjm.board.R;

public class BitmapUtil {

    static Bitmap getDefaultPortrait(Context context) {
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_huaji);
    }

    static Bitmap[] getDefaultPics(Context context) {
        int size = 3;
        Bitmap[] bitmaps = new Bitmap[size];
        bitmaps[0] = BitmapFactory.decodeResource(context.getResources(), R.drawable.chat);
        bitmaps[1] = BitmapFactory.decodeResource(context.getResources(), R.drawable.user);
        bitmaps[2] = BitmapFactory.decodeResource(context.getResources(), R.drawable.write);
        return bitmaps;
    }

    public static Bitmap getHexBitmap(Context context, String hexStr) {
        Bitmap bitmap;
        if (hexStr.length() < 128) {
            bitmap = getDefaultPortrait(context);
        } else {
            byte[] b = BitmapIOUtil.hexStringToByteArray(hexStr);
            bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
        }
        return bitmap;
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

}
