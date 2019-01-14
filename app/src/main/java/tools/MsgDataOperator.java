package tools;


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

public class MsgDataOperator {


    public static ArrayList<Msg> getTestMsgData(Context context){
        ArrayList<Msg> msgs=new ArrayList<>();

        // public Msg(int id, String userid, String nickname, String time, String content, Bitmap portrait, boolean hasPic, Bitmap[] picture) {
        for(int i=0;i<10;i++){
            msgs.add(new Msg(i,"10001"+i,"wcf","2019-01-01 19:0"+i,"3132165136513213"+i, BitMapUtil.getDefaultPortrait(context),true,BitMapUtil.getDefaultPics(context)));
        }

        return msgs;
    }

    public static ArrayList<Msg> getMsgData(Context context,ArrayList<Msg> msgData){
        ArrayList<Msg> msgs=new ArrayList<>();
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

        Cursor cursor = database.query("msg",new String[]{"id","userid","time","content","haspic","picture","comment"}, "where id>?", new String[]{String.valueOf(lastId)}, null, null, "id", null);
        if(cursor.moveToFirst()){
            do{
                Msg msg;
                msg=new Msg(cursor.getInt())
                msgData.add(0,msg);
            }while (cursor.moveToNext());
        }



        return msgs;
    }

}
