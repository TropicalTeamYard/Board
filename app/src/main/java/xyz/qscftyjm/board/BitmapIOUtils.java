package xyz.qscftyjm.board;

import android.graphics.Bitmap;
import android.util.Base64;
import android.util.Log;


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

    public static String byte2Base64StringFun(byte[] b){
        Log.d("Board",Base64.encodeToString(b, Base64.DEFAULT));
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public static String bytesToHexString(byte[] bytes){
        StringBuilder stringBuilder = new StringBuilder("");
        if (bytes == null || bytes.length <= 0) {
            return null;
        }
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    public static byte[] hexStringToByteArray(String str) {
        int len = str.length();
        byte[] data = new byte[len / 2];
        try {
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(str.charAt(i), 16) << 4)
                        + Character.digit(str.charAt(i+1), 16));
            }
        } catch (Exception e) {
            //Log.d("", "Argument(s) for hexStringToByteArray(String s)"+ "was not a hex string");
        }
        return data;
    }

    public void savePic(Bitmap[] bitmaps){
        int picNum=bitmaps.length;
    }

}
