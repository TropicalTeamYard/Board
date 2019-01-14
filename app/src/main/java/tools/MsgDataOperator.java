package tools;


import android.content.Context;
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

    public static ArrayList<Msg> getMsgData(Context context){
        ArrayList<Msg> msgs=new ArrayList<>();
        SQLiteDatabase database=BoardDBHelper.getMsgDBHelper(context).getWritableDatabase();
//        Msg(int id, String userid, String nickname, String time, String content, Bitmap portrait, boolean hasPic, Bitmap[] picture)
//        for(int i=0;i<10;i++){
//            msgs.add(new Msg(i,"10001"+i,"wcf","2019-01-01 19:0"+i,"3132165136513213"+i,BitMapUtil.getDefaultPortrait(context),true,BitMapUtil.getDefaultPics(context)));
//        }



        return msgs;
    }

}
