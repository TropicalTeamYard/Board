package tools;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class MsgDataOperator {


    public static ArrayList<Msg> getTestMsgData(Context context){
        ArrayList<Msg> msgs=new ArrayList<>();

        // public Msg(int id, String userid, String nickname, String time, String content, Bitmap portrait, boolean hasPic, Bitmap[] picture) {
        for(int i=0;i<10;i++){
            msgs.add(new Msg(i,"10001"+i,"wcf","2019-01-01 19:0"+i,"3132165136513213"+i, BitMapUtil.getDefaultPortrait(context),1,BitMapUtil.getDefaultPics(context)));
        }

        return msgs;
    }

    public static ArrayList<Msg> getMsgData(Context context,ArrayList<Msg> msgData){
        SQLiteDatabase database=BoardDBHelper.getMsgDBHelper(context).getWritableDatabase();
        int lastId=0;
        if(msgData==null){
            msgData=new ArrayList<>();
        } else {
            if (msgData.size()==0){
                lastId=0;
            } else {
                lastId=msgData.get(0).getId();
            }
        }
//        Msg(int id, String userid, String nickname, String time, String content, Bitmap portrait, boolean hasPic, Bitmap[] picture)
//        for(int i=0;i<10;i++){
//            msgs.add(new Msg(i,"10001"+i,"wcf","2019-01-01 19:0"+i,"3132165136513213"+i,BitMapUtil.getDefaultPortrait(context),true,BitMapUtil.getDefaultPics(context)));
//        }

        Cursor cursor = database.query("msg",new String[]{"id","userid","time","content","haspic","picture","comment"}, "id>?", new String[]{String.valueOf(lastId)}, null, null, "id", null);
        if(cursor.moveToFirst()){
            do{
                Msg msg;
                int id=cursor.getInt(0);
                String userid=cursor.getString(1);
                String time = cursor.getString(2);
                String content = cursor.getString(3);
                int haspic=cursor.getInt(4);
                String comment = cursor.getString(6);

                Bitmap[] b_pics=null;
                if(haspic>0){
                    String t_pics=new String(cursor.getBlob(5));
                    try {
                        JSONArray arrPic=new JSONArray(t_pics);
                        b_pics=new Bitmap[arrPic.length()];
                        for (int i=0;i<arrPic.length();i++){
                            b_pics[i]=BitMapUtil.getHexBitmap(context,arrPic.getString(i));
                        }
                        haspic=b_pics.length;
                    } catch (JSONException e) {
                        b_pics=null;
                        haspic=0;
                        e.printStackTrace();
                    }
                }

                if (b_pics!=null&&b_pics.length>0){
                    msg=new Msg(id,userid,null,time,content,null,haspic,b_pics);
                } else {
                    msg=new Msg(id,userid,null,time,content,null);
                }

                msgData.add(0,msg);
            }while (cursor.moveToNext());
        }

        cursor.close();


        return msgData;
    }

}
