package tools;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;

import postutil.AsynTaskUtil;

public class MsgDataOperator {

    public static void getMsgData(Context context, ArrayList<Msg> msgData, Map<String, PublicUserInfo> userInfoMap) {
        if (context == null) {
            return;
        }
        SQLiteDatabase database = BoardDBHelper.getMsgDBHelper(context).getWritableDatabase();
        Cursor cursor;
        PublicUserInfo userInfo;
        int lastId = 0;
        if (msgData == null) {
            msgData = new ArrayList<>();
        } else {
            if (msgData.size() == 0) {
            } else {
                lastId = msgData.get(0).getId();
            }
        }

        cursor = database.query("publicinfo", new String[]{"userid", "nickname", "portrait"}, null, null, null, null, null);

        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                userInfo = new PublicUserInfo();
                userInfo.userid = cursor.getString(0);
                userInfo.portrait = BitmapUtil.getHexBitmap(context, new String(cursor.getBlob(2)));
                userInfo.nickname = cursor.getString(1);
            } while (cursor.moveToNext());
            userInfoMap.put(userInfo.userid, userInfo);
            cursor.close();
        }

        cursor = database.query("msg", new String[]{"id", "userid", "time", "content", "haspic", "picture", "comment"}, "id>?", new String[]{String.valueOf(lastId)}, null, null, "id", null);
        if (cursor.moveToFirst()) {
            do {
                Msg msg;
                int id = cursor.getInt(0);
                String userid = cursor.getString(1);

                String time = cursor.getString(2);
                String content = cursor.getString(3);
                int haspic = cursor.getInt(4);
                String comment = cursor.getString(6);

                Bitmap[] b_pics = null;
                if (haspic > 0) {

                    try {
                        String t_pics = new String(cursor.getBlob(5));
                        JSONArray arrPic = new JSONArray(t_pics);
                        b_pics = new Bitmap[arrPic.length()];
                        for (int i = 0; i < arrPic.length(); i++) {
                            b_pics[i] = BitmapUtil.getHexBitmap(context, arrPic.getString(i));
                        }
                        haspic = b_pics.length;
                    } catch (JSONException | NullPointerException e) {
                        b_pics = null;
                        haspic = 0;
                        e.printStackTrace();
                    }
                }

                if (b_pics != null && b_pics.length > 0) {
                    msg = new Msg(id, userid, null, time, content, null, haspic, b_pics);
                } else {
                    msg = new Msg(id, userid, null, time, content, null);
                }
                msgData.add(0, msg);
            } while (cursor.moveToNext());
        }
        cursor.close();
        if (database.isOpen()) {
            database.close();
        }
    }

    public static Map<String, PublicUserInfo> getUserInfo(final Context context, ArrayList<String> userids, final Map<String, PublicUserInfo> userInfoMap) {
        PublicUserInfo userInfo;
        final SQLiteDatabase database = BoardDBHelper.getMsgDBHelper(context).getWritableDatabase();
        final Cursor cursor = database.query("publicinfo", new String[]{"userid", "nickname", "portrait"}, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            if (cursor.getCount() > 0) {
                do {
                    userInfo = new PublicUserInfo();
                    userInfo.userid = cursor.getString(0);
                    userInfo.nickname = cursor.getString(2);
                    userInfo.portrait = BitmapUtil.getHexBitmap(context, new String(cursor.getBlob(2)));
                    if (!userInfoMap.containsKey(userInfo.userid)) {
                        userInfoMap.put(userInfo.userid, userInfo);
                    }

                } while (cursor.moveToNext());
            }
        }
        cursor.close();
        for (int i = 0; i < userids.size(); i++) {
            ArrayList<String> needed = new ArrayList<>();

            if (!userInfoMap.containsKey(userids.get(i))) {
                needed.add(userids.get(i));
            }

            if (userids.size() > 0) {

                JSONArray jsonArrNeeded = new JSONArray(needed);
                AsynTaskUtil.AsynNetUtils.post(StringCollector.getUserServer(), ParamToString.formGetPublicInfo(jsonArrNeeded.toString()), new AsynTaskUtil.AsynNetUtils.Callback() {
                    @Override
                    public void onResponse(String response) {
                        JSONObject jsonObj;
                        if (response != null) {
                            Log.d("MDO", response);
                            try {
                                jsonObj = new JSONObject(response);
                                if (jsonObj.optInt("code", -1) == 0) {
                                    JSONArray users = jsonObj.optJSONArray("users");
                                    if (users != null && users.length() > 0) {
                                        for (int i = 0; i < users.length(); i++) {
                                            PublicUserInfo userInfo = new PublicUserInfo();
                                            if (users.optJSONObject(i) != null) {
                                                userInfo.userid = users.optJSONObject(i).optString("userid");
                                                userInfo.nickname = users.optJSONObject(i).optString("nickname");
                                                userInfo.portrait = BitmapUtil.getHexBitmap(context, users.optJSONObject(i).optString("portrait", "00000000"));
                                            } else {
                                                userInfo.userid = "UNDEFINED";
                                                userInfo.nickname = "UNDEFINED";
                                                userInfo.portrait = BitmapUtil.getHexBitmap(context, "00000000");
                                            }

                                            ContentValues values = new ContentValues();
                                            values.put("userid", userInfo.userid);
                                            values.put("nickname", userInfo.nickname);
                                            values.put("portrait", BitmapIOUtil.bytesToHexString(BitmapUtil.Bitmap2Bytes(userInfo.portrait)));

                                            userInfoMap.put(userInfo.userid, userInfo);
                                            database.insertWithOnConflict("publicinfo", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                        }
                                    }
                                } else {
                                    Log.d("Service", jsonObj.optString("msg", "未知错误"));
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }

        }

        return userInfoMap;
    }

    public static Map<String, PublicUserInfo> getUserInfo(int i, final Context context, ArrayList<Msg> msgs, final Map<String, PublicUserInfo> userInfoMap) {
        ArrayList<String> userids = new ArrayList<>();
        PublicUserInfo userInfo;
        final SQLiteDatabase database = BoardDBHelper.getMsgDBHelper(context).getWritableDatabase();
        for (Msg msg : msgs) {
            if (userInfoMap.containsKey(msg.getUserid()) && Objects.requireNonNull(userInfoMap.get(msg.getUserid())).nickname != null && Objects.requireNonNull(userInfoMap.get(msg.getUserid())).portrait != null) {
                Log.d("MDO", "Have " + msg.getUserid());
            } else {
                Cursor cursor = database.query("publicinfo", new String[]{"userid", "nickname", "portrait"}, null, null, null, null, null);
                if (cursor.moveToFirst()) {
                    if (cursor.getCount() > 0) {
                        do {
                            userInfo = new PublicUserInfo();
                            userInfo.userid = cursor.getString(0);
                            userInfo.nickname = cursor.getString(2);
                            userInfo.portrait = BitmapUtil.getHexBitmap(context, new String(cursor.getBlob(2)));
                            userInfoMap.put(userInfo.userid, userInfo);
                        } while (cursor.moveToNext());
                    } else {
                        if (!userids.contains(msg.getUserid())) {
                            userids.add(msg.getUserid());
                            Log.d("MDO", "Need " + msg.getUserid());
                        }
                    }
                } else {
                    if (!userids.contains(msg.getUserid())) {
                        userids.add(msg.getUserid());
                        Log.d("MDO", "Need " + msg.getUserid());
                    }
                }
                cursor.close();
            }
        }
        if (userids.size() > 0) {
            JSONArray jsonArrNeeded = new JSONArray(userids);
            AsynTaskUtil.AsynNetUtils.post(StringCollector.getUserServer(), ParamToString.formGetPublicInfo(jsonArrNeeded.toString()), new AsynTaskUtil.AsynNetUtils.Callback() {
                @Override
                public void onResponse(String response) {
                    JSONObject jsonObj;
                    if (response != null) {
                        Log.d("MDO", response);
                        try {
                            jsonObj = new JSONObject(response);
                            if (jsonObj.optInt("code", -1) == 0) {
                                JSONArray users = jsonObj.optJSONArray("users");
                                if (users != null && users.length() > 0) {
                                    for (int i = 0; i < users.length(); i++) {
                                        PublicUserInfo userInfo = new PublicUserInfo();
                                        if (users.optJSONObject(i) != null) {
                                            userInfo.userid = users.optJSONObject(i).optString("userid");
                                            userInfo.nickname = users.optJSONObject(i).optString("nickname");
                                            userInfo.portrait = BitmapUtil.getHexBitmap(context, users.optJSONObject(i).optString("portrait", "00000000"));
                                        } else {
                                            userInfo.userid = "UNDEFINED";
                                            userInfo.nickname = "UNDEFINED";
                                            userInfo.portrait = BitmapUtil.getHexBitmap(context, "00000000");
                                        }

                                        ContentValues values = new ContentValues();
                                        values.put("userid", userInfo.userid);
                                        values.put("nickname", userInfo.nickname);
                                        values.put("portrait", BitmapIOUtil.bytesToHexString(BitmapUtil.Bitmap2Bytes(userInfo.portrait)));

                                        userInfoMap.put(userInfo.userid, userInfo);
                                        database.insertWithOnConflict("publicinfo", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                    }
                                }
                            } else {
                                Log.d("MDO", jsonObj.optString("msg", "未知错误"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            });
        }
        return userInfoMap;
    }

    static void getUserInfo(final Context context, String userid, final Map<String, PublicUserInfo> userInfoMap) {
        PublicUserInfo userInfo = new PublicUserInfo();
        final SQLiteDatabase database = BoardDBHelper.getMsgDBHelper(context).getWritableDatabase();
        Cursor cursor = database.query("publicinfo", new String[]{"userid", "nickname", "portrait"}, "userid=?", new String[]{userid}, null, null, null);

        if (cursor.moveToFirst() && cursor.getCount() > 0) {
            do {
                userInfo.userid = userid;
                userInfo.portrait = BitmapUtil.getHexBitmap(context, new String(cursor.getBlob(2)));
                userInfo.nickname = cursor.getString(1);
            } while (cursor.moveToNext());
            userInfoMap.put(userid, userInfo);
            cursor.close();
        } else {

            AsynTaskUtil.AsynNetUtils.post(StringCollector.getUserServer(), ParamToString.formGetPublicInfo("['" + userid + "']"), new AsynTaskUtil.AsynNetUtils.Callback() {
                @Override
                public void onResponse(String response) {
                    JSONObject jsonObj;
                    if (response != null) {
                        Log.d("MDO", response);
                        try {
                            jsonObj = new JSONObject(response);
                            if (jsonObj.optInt("code", -1) == 0) {
                                JSONArray users = jsonObj.optJSONArray("users");
                                if (users != null && users.length() > 0) {
                                    PublicUserInfo userInfo = new PublicUserInfo();
                                    if (users.optJSONObject(0) != null) {
                                        userInfo.userid = users.optJSONObject(0).optString("userid");
                                        userInfo.nickname = users.optJSONObject(0).optString("nickname");
                                        userInfo.portrait = BitmapUtil.getHexBitmap(context, users.optJSONObject(0).optString("portrait", "00000000"));
                                    } else {
                                        userInfo.userid = "UNDEFINED";
                                        userInfo.nickname = "UNDEFINED";
                                        userInfo.portrait = BitmapUtil.getHexBitmap(context, "00000000");
                                    }

                                    ContentValues values = new ContentValues();
                                    values.put("userid", userInfo.userid);
                                    values.put("nickname", userInfo.nickname);
                                    values.put("portrait", BitmapIOUtil.bytesToHexString(BitmapUtil.Bitmap2Bytes(userInfo.portrait)));

                                    userInfoMap.put(userInfo.userid, userInfo);
                                    database.insertWithOnConflict("publicinfo", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                }
                            } else {
                                Log.d("MDO", jsonObj.optString("msg", "未知错误"));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    if (database.isOpen()) {
                        database.close();
                    }
                }
            });
        }

    }

}
