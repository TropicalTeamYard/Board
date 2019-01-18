package tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import postutil.NetUtils;

public class PublicUserInfoUpdater {
    private static SQLiteDatabase database;

    public static void CheckPublicUserInfoUpdate(@NonNull final Context context){
        new Thread(){
            @Override
            public void run() {
                super.run();
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
                    }while(cursor.moveToNext());
                    Log.d("PUIU","size: "+userInfoMd5Array.size());
                    String response=NetUtils.post(StringCollector.getUserServer(), ParamToString.formUpdatePublicUserInfo(userInfoMd5Array));
                    if(response!=null) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (jsonObject.optInt("code", -1) == 0) {
                                String _userid, _nickname, _portrait;
                                JSONArray jsonArray = jsonObject.optJSONArray("data");
                                Log.d("PUIU", "Has Update: " + jsonArray.length());
                                if (jsonArray.length() > 0) {
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        _userid = jsonArray.optJSONObject(i).optString("userid");
                                        _nickname = jsonArray.optJSONObject(i).optString("nickname");
                                        _portrait = jsonArray.optJSONObject(i).optString("portrait");
                                        if (!_userid.equals("") && !_nickname.equals("") && !_portrait.equals("")) {
                                            ContentValues values = new ContentValues();
                                            values.put("nickname", _nickname);
                                            values.put("portrait", _portrait);
                                            database.update("publicinfo",values,"userid=?",new String[]{_userid});
                                        }
                                    }
                                }
                            }
                        } catch(JSONException ignored){ }
                    }
                }
                cursor.close();
            }
        }.start();


    }

    private static String getUserMd5(String userid,String nickname,String portrait){
        if(userid==null||nickname==null||portrait==null){
            return null;
        } else {
            return MD5Util.getMd5(userid+nickname+portrait);
        }
    }

}

