package tools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import xyz.qscftyjm.board.R;

public class MsgListAdapter extends BaseAdapter {

    ArrayList<Msg> msgList;
    Map<String, PublicUserInfo> userInfoMap;
    Context context;
    ViewHolder viewHolder;
    SQLiteDatabase database;

    public MsgListAdapter(ArrayList<Msg> msgList,Map<String, PublicUserInfo> userInfoMap, Context context){
        this.context=context;this.msgList=msgList;this.userInfoMap=userInfoMap;
        // TODO init data
        PublicUserInfo userInfo;
        database=BoardDBHelper.getMsgDBHelper(context).getWritableDatabase();
        Cursor cursor=database.query("publicinfo",new String[]{"userid","nickname","portrait"},null,null,null,null,null);

        if(cursor.moveToFirst()&&cursor.getCount()>0){
            do{
                userInfo=new PublicUserInfo();
                userInfo.userid=cursor.getString(0);
                userInfo.portrait= BitmapUtil.getHexBitmap(context,new String(cursor.getBlob(2)));
                userInfo.nickname=cursor.getString(1);
            }while (cursor.moveToNext());
            userInfoMap.put(userInfo.userid,userInfo);
            cursor.close();
        }
    }

    @Override
    public int getCount() {
        return msgList.size();
    }

    @Override
    public Object getItem(int position) {
        return msgList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.msg_card, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(userInfoMap.containsKey(msgList.get(position).getUserid())){
            viewHolder.portrait.setImageBitmap(userInfoMap.get(msgList.get(position).getUserid()).portrait);
            viewHolder.nickname.setText(userInfoMap.get(msgList.get(position).getUserid()).nickname);
        } else {
            MsgDataOperator.getUserInfo(context,msgList.get(position).getUserid(),userInfoMap);
        }

        if(userInfoMap.containsKey(msgList.get(position).getUserid())){
            viewHolder.portrait.setImageBitmap(userInfoMap.get(msgList.get(position).getUserid()).portrait);
            viewHolder.nickname.setText(userInfoMap.get(msgList.get(position).getUserid()).nickname);
        }

        viewHolder.time.setText(msgList.get(position).getTime());

        viewHolder.content.setText(msgList.get(position).getContent());
        if(msgList.get(position).getHasPic()>0){
            viewHolder.picture.setVisibility(View.VISIBLE);
            viewHolder.picture.setImageBitmap(msgList.get(position).getPicture()[0]);
        } else {
            viewHolder.picture.setVisibility(View.GONE);
        }


        return convertView;
    }

    class ViewHolder{
        ImageView portrait, picture;
        TextView time, nickname, content;

        public ViewHolder(View view) {
            portrait=view.findViewById(R.id.msg_head_portrait);
            picture=view.findViewById(R.id.msg_picture);
            time=view.findViewById(R.id.msg_time);
            nickname=view.findViewById(R.id.msg_nickname);
            content=view.findViewById(R.id.msg_content);
        }
    }

}
