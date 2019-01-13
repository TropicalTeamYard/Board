package xyz.qscftyjm.board;

import android.graphics.Bitmap;
import android.util.Base64;


import java.util.HashMap;
import java.util.Map;

public class BitmapIOUtils {

    public Map<String, Object> getPortrait(String[] userid){
        Map<String, Object> picMap=new HashMap<>();
        return picMap;
    }

    public void savePortrait(Bitmap bitmap){

    }

    public void savePortrait(Object in){

    }

    public Bitmap[] getPic(Object[] ins){
        Bitmap[] bitmaps=new Bitmap[ins.length];
        return bitmaps;
    }

    public static byte[] base64String2ByteFun(String base64Str){
        return Base64.decode(base64Str.toString(),Base64.DEFAULT);
    }
    //byte[]转base64
    public static String byte2Base64StringFun(byte[] b){
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void savePic(Bitmap[] bitmaps){
        int picNum=bitmaps.length;
    }

}
