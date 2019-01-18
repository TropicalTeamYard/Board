package tools;

import android.graphics.Bitmap;
import android.util.Log;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class BitmapIOUtil {

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

//    public static byte[] base64tring2ByteFun(String base64Str){
//        return Base64.decode(base64Str.toString(),Base64.DEFAULT);
//    }
//
//    public static String byte2Base64StringFun(byte[] b){
//        Log.d("Board",Base64.encodeToString(b, Base64.DEFAULT));
//        return Base64.encodeToString(b, Base64.DEFAULT);
//    }

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
            //Log.d("hex", "Argument(s) for hexStringToByteArray(String s)"+ "was not a hex string");
        }
        return data;
    }

    public static byte[] ReadImage(String imagepath) throws Exception {
        FileInputStream fs = new FileInputStream(imagepath);
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while (-1 != (len = fs.read(buffer))) {
            outStream.write(buffer, 0, len);
        }
        outStream.close();
        fs.close();
        return outStream.toByteArray();
    }

    public void savePic(Bitmap[] bitmaps){
        int picNum=bitmaps.length;
    }

}
