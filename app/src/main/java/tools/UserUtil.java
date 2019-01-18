package tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashMap;
import java.util.Map;

public class UserUtil {
    public static Map<String, Object> getUserInfo(Context context){
        Map<String, Object> userInfo=new HashMap<>();
        SQLiteDatabase db= BoardDBHelper.getMsgDBHelper(context).getWritableDatabase();
        Cursor cursor=db.query("userinfo",new String[]{"userid","nickname","portrait","email","priority","token"},null,null,null,null,"id desc","0,1");
        String token;int count=0;
        if(cursor.moveToFirst()){
            count=cursor.getCount();
            if(count>0){

                do{
                    userInfo.put("userid",cursor.getString(0));
                    userInfo.put("nickname",cursor.getString(1));

                    userInfo.put("portrait", BitmapUtil.getHexBitmap(context,new String(cursor.getBlob(2))));

                    userInfo.put("email",cursor.getString(3));
                }while (cursor.moveToNext());

                cursor.close();

            }
        }

        return userInfo;
    }
}
