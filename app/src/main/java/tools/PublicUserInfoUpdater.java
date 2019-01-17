package tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import postutil.AsynTaskUtil;

public class PublicUserInfoUpdater {
    private static SQLiteDatabase database;

    public static void CheckUserInfoUpdate(@NonNull Context context){
        database=BoardDBHelper.getMsgDBHelper(context).getWritableDatabase();
        String userid,nickname,portrait;
        ArrayList<Map<String,String>> userInfoMd5Array=new ArrayList<>();
        Map<String, String> userInfoMd5;
        Cursor cursor=database.query("publicinfo",new String[]{"userid","nickname","portrait"},null,null,null,null,null);
        if(cursor.moveToFirst()&&cursor.getCount()>0) {
            do{
                userInfoMd5=new HashMap<>();
                userid=cursor.getString(0);
                nickname=cursor.getString(1);
                portrait=new String(cursor.getBlob(2));
                userInfoMd5.put("userid",userid);
                userInfoMd5.put("md5",getUserMd5(userid,nickname,portrait));
                userInfoMd5Array.add(userInfoMd5);
            }while(cursor.moveToFirst());
            AsynTaskUtil.AsynNetUtils.post(StringCollector.getUserServer(), ParamToString.formUpdateUserInfo(userInfoMd5Array), new AsynTaskUtil.AsynNetUtils.Callback() {
                @Override
                public void onResponse(String response) {

                }
            });
        }
        cursor.close();

    }

    private static String getUserMd5(String userid,String nickname,String portrait){
        if(userid==null||nickname==null||portrait==null){
            return null;
        } else {
            return MD5Util.getMd5(userid+nickname+portrait);
        }
    }

}
