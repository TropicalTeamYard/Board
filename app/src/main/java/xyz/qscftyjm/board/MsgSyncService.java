package xyz.qscftyjm.board;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import postutil.AsynTaskUtil;
import tools.BoardDBHelper;
import tools.ParamToString;
import tools.StringCollector;

public class MsgSyncService extends Service {
    Handler handler=new Handler();
    Runnable runnable;
    SQLiteDatabase database;

    public MsgSyncService() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel mChannel = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            mChannel = new NotificationChannel("Msg001", "GetMsg", NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(mChannel);
            Notification notification = new Notification.Builder(getApplicationContext(), "Msg001").build();
            startForeground(1, notification);
        }

        database= BoardDBHelper.getMsgDBHelper(this).getWritableDatabase();
        runnable=new Runnable() {

            @Override
            public void run() {
                int lastId=0;
                handler.postDelayed(this, 1000);
                Cursor cursor=database.query("msg", new String[] { "id" }, null, null, null, null, "id desc", "0,1");
                if(cursor.moveToFirst()) {
                    lastId=cursor.getInt(0);
                    Log.d("Service","lsatId: "+lastId);
                }
                cursor.close();

                String userid,token;

                cursor=database.query("userinfo", new String[] { "userid","token" }, null, null, null, null, "id desc", "0,1");
                if(cursor.moveToFirst()) {
                    userid=cursor.getString(0);
                    token=cursor.getString(1);
                } else {
                    return;
                }
                cursor.close();
                AsynTaskUtil.AsynNetUtils.post(StringCollector.getMsgServer(), ParamToString.formGetMsg(userid,token,String.valueOf(lastId)), new AsynTaskUtil.AsynNetUtils.Callback() {

                    @Override
                    public void onResponse(String response) {
                        if(response!=null) {
                            try {
                                JSONObject jsonObj=new JSONObject(response);
                                if(jsonObj.optInt("code", -1)==0) {
                                    JSONArray delArr=jsonObj.optJSONArray("delete");
                                    if(delArr!=null&&delArr.length()>0){
                                        for (int i=0;i<delArr.length();i++){
                                            int num=database.delete("msg","id=?",new String[]{String.valueOf(delArr.optInt(i,-1))});
                                        }
                                    }
                                    JSONArray msgArr=jsonObj.optJSONArray("msgs");
                                    if(jsonObj.optInt("msgct",0)==0){
                                        Log.d("Service Msg","No new msg");
                                        return;
                                    }
                                    int msgCt=msgArr.length();

                                    if(msgCt>0) {
                                        for(int i=0;i<msgCt;i++) {
                                            JSONObject newObj=msgArr.getJSONObject(i);
                                            ContentValues values=new ContentValues();
                                            if(newObj.optInt("id",-1)==-1){
                                                continue;
                                            }
                                            values.put("id", newObj.optInt("id",-1));
                                            values.put("userid", newObj.optString("userid","error"));
                                            values.put("time", newObj.optString("time","error"));
                                            values.put("content", newObj.optString("content","error"));
                                            values.put("haspic",newObj.optInt("haspic",0));
                                            if(newObj.optInt("haspic",0)>0){
                                                JSONArray jsonArray=newObj.optJSONArray("pics");
                                                values.put("pics",jsonArray.toString());
                                            }
                                            values.put("comment",newObj.optString("comment","{'comment':null}"));
                                            database.insertWithOnConflict("msg", null, values, SQLiteDatabase.CONFLICT_REPLACE);
                                        }
                                        Intent intent = new Intent();
                                        intent.setAction("xyz.qscftyjm.board.HAS_NEW_MSG");
                                        intent.putExtra("msg", msgArr.toString());
                                        sendBroadcast(intent);
                                    } else {
                                        Log.d("Service Msg","No new msg");
                                    }

                                } else {
                                    Log.d("Service",jsonObj.optString("msg","未知错误"));
                                }
                            } catch (JSONException | NullPointerException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                });
            }
        };



    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        runnable.run();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

}
