package xyz.qscftyjm.board;


import android.content.Context;

import java.util.ArrayList;
import java.util.Map;

public class MsgDataOperator {

    public static ArrayList<Msg> getTestMsgData(Context context){
        ArrayList<Msg> msgs=new ArrayList<>();

        // public Msg(int id, String userid, String nickname, String time, String content, Bitmap portrait, boolean hasPic, Bitmap[] picture) {
        for(int i=0;i<10;i++){
            msgs.add(new Msg(i,"10001"+i,"wcf","2019-01-01 19:0"+i,"3132165136513213"+i,BitMapUtil.getDefaultPortrait(context),true,BitMapUtil.getDefaultPics(context)));
        }


        return msgs;
    }

}
